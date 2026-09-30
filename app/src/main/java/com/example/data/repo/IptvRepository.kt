package com.example.data.repo

import android.content.Context
import com.example.data.local.EpgProgramEntity
import com.example.data.local.IptvDao
import com.example.data.local.PlaylistEntity
import com.example.data.local.StreamEntity
import com.example.data.model.ChannelType
import com.example.data.model.PlaylistItem
import com.example.data.model.PlaylistType
import com.example.data.model.StreamItem
import com.example.data.parser.M3uParser
import com.example.data.sample.SamplePlaylists
import com.example.data.security.CryptoHelper
import com.example.data.xtream.XtreamClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.util.concurrent.TimeUnit

class IptvRepository(
    private val dao: IptvDao,
    private val xtreamClient: XtreamClient = XtreamClient(),
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
) {

    val allPlaylists: Flow<List<PlaylistItem>> = dao.getAllPlaylists().map { list ->
        list.map { it.toModel() }
    }

    val activePlaylist: Flow<PlaylistItem?> = dao.getActivePlaylist().map { it?.toModel() }

    fun getStreams(playlistId: Long, type: ChannelType): Flow<List<StreamItem>> {
        return dao.getStreams(playlistId, type).map { list -> list.map { it.toModel() } }
    }

    fun getStreamsByCategory(playlistId: Long, type: ChannelType, category: String): Flow<List<StreamItem>> {
        return dao.getStreamsByCategory(playlistId, type, category).map { list -> list.map { it.toModel() } }
    }

    fun getCategories(playlistId: Long, type: ChannelType): Flow<List<String>> {
        return dao.getCategories(playlistId, type)
    }

    fun getAllFavorites(): Flow<List<StreamItem>> {
        return dao.getAllFavorites().map { list -> list.map { it.toModel() } }
    }

    fun getFavoritesByType(type: ChannelType): Flow<List<StreamItem>> {
        return dao.getFavoritesByType(type).map { list -> list.map { it.toModel() } }
    }

    fun getWatchHistory(): Flow<List<StreamItem>> {
        return dao.getWatchHistory().map { list -> list.map { it.toModel() } }
    }

    fun searchStreams(playlistId: Long, query: String): Flow<List<StreamItem>> {
        return dao.searchStreams(playlistId, query).map { list -> list.map { it.toModel() } }
    }

    fun getEpisodesForSeries(seriesId: String): Flow<List<StreamItem>> {
        return dao.getEpisodesForSeries(seriesId).map { list -> list.map { it.toModel() } }
    }

    fun getUpcomingEpg(channelId: String): Flow<List<EpgProgramEntity>> {
        return dao.getUpcomingEpg(channelId, System.currentTimeMillis())
    }

    suspend fun getCurrentProgram(channelId: String): EpgProgramEntity? {
        return dao.getCurrentProgram(channelId, System.currentTimeMillis())
    }

    suspend fun toggleFavorite(streamId: Long, currentFav: Boolean) = withContext(Dispatchers.IO) {
        dao.updateFavorite(streamId, !currentFav)
    }

    suspend fun recordWatchProgress(streamId: Long, progressMs: Long, durationMs: Long) = withContext(Dispatchers.IO) {
        dao.updateWatchProgress(streamId, progressMs, durationMs, System.currentTimeMillis())
    }

    suspend fun setActivePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        dao.setActivePlaylist(playlistId)
    }

    suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        dao.deleteStreamsByPlaylist(playlistId)
        dao.deletePlaylist(playlistId)
    }

    suspend fun loadSampleDataIfNeeded() = withContext(Dispatchers.IO) {
        val active = dao.getActivePlaylistOnce()
        if (active == null) {
            forceLoadSampleData()
        }
    }

    suspend fun forceLoadSampleData() = withContext(Dispatchers.IO) {
        val samplePlaylist = SamplePlaylists.createSamplePlaylist()
        val playlistId = dao.insertPlaylist(samplePlaylist)
        dao.setActivePlaylist(playlistId)

        val streams = SamplePlaylists.createSampleStreams(playlistId)
        dao.insertStreams(streams)

        val epg = SamplePlaylists.createSampleEpg()
        dao.insertEpgPrograms(epg)
    }

    suspend fun importM3uUrl(name: String, url: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                response.close()
                return@withContext Result.failure(Exception("HTTP Error: ${response.code}"))
            }

            val bodyBytes = response.body?.bytes()
            response.close()
            
            if (bodyBytes == null) {
                return@withContext Result.failure(Exception("Empty playlist body"))
            }

            val playlistEntity = PlaylistEntity(
                name = name.ifBlank { "M3U Playlist" },
                type = PlaylistType.M3U_URL,
                sourceUrl = url,
                isActive = true
            )
            val playlistId = dao.insertPlaylist(playlistEntity)
            dao.setActivePlaylist(playlistId)

            val parsedStreams = M3uParser.parse(ByteArrayInputStream(bodyBytes), playlistId)
            dao.insertStreams(parsedStreams)

            val liveCount = parsedStreams.count { it.type == ChannelType.LIVE }
            val movieCount = parsedStreams.count { it.type == ChannelType.MOVIE }
            val seriesCount = parsedStreams.count { it.type == ChannelType.SERIES }

            dao.updatePlaylist(
                playlistEntity.copy(
                    id = playlistId,
                    channelCount = liveCount,
                    movieCount = movieCount,
                    seriesCount = seriesCount
                )
            )

            Result.success(playlistId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importM3uContent(name: String, content: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val stream = ByteArrayInputStream(content.toByteArray(Charsets.UTF_8))
            val playlistEntity = PlaylistEntity(
                name = name.ifBlank { "Imported M3U" },
                type = PlaylistType.M3U_CONTENT,
                sourceUrl = "local_content",
                isActive = true
            )
            val playlistId = dao.insertPlaylist(playlistEntity)
            dao.setActivePlaylist(playlistId)

            val parsedStreams = M3uParser.parse(stream, playlistId)
            dao.insertStreams(parsedStreams)

            val liveCount = parsedStreams.count { it.type == ChannelType.LIVE }
            val movieCount = parsedStreams.count { it.type == ChannelType.MOVIE }
            val seriesCount = parsedStreams.count { it.type == ChannelType.SERIES }

            dao.updatePlaylist(
                playlistEntity.copy(
                    id = playlistId,
                    channelCount = liveCount,
                    movieCount = movieCount,
                    seriesCount = seriesCount
                )
            )

            Result.success(playlistId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importXtream(
        name: String,
        serverUrl: String,
        user: String,
        pass: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val auth = xtreamClient.authenticate(serverUrl, user, pass)
            if (!auth.success) {
                return@withContext Result.failure(Exception(auth.message ?: "Authentication failed"))
            }

            // Encrypt password before storing in database
            val encryptedPassword = if (pass.isNotBlank()) CryptoHelper.encrypt(pass) else null
            
            val playlistEntity = PlaylistEntity(
                name = name.ifBlank { "Xtream Account" },
                type = PlaylistType.XTREAM,
                sourceUrl = serverUrl,
                username = user,
                password = encryptedPassword,
                isActive = true
            )
            val playlistId = dao.insertPlaylist(playlistEntity)
            dao.setActivePlaylist(playlistId)

            val streams = xtreamClient.fetchAllStreams(playlistId, serverUrl, user, pass)
            dao.insertStreams(streams)

            val liveCount = streams.count { it.type == ChannelType.LIVE }
            val movieCount = streams.count { it.type == ChannelType.MOVIE }
            val seriesCount = streams.count { it.type == ChannelType.SERIES }

            dao.updatePlaylist(
                playlistEntity.copy(
                    id = playlistId,
                    channelCount = liveCount,
                    movieCount = movieCount,
                    seriesCount = seriesCount
                )
            )

            Result.success(playlistId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun PlaylistEntity.toModel() = PlaylistItem(
        id = id,
        name = name,
        type = type,
        sourceUrl = sourceUrl,
        username = username,
        password = CryptoHelper.decryptOrNull(password),
        channelCount = channelCount,
        movieCount = movieCount,
        seriesCount = seriesCount,
        isActive = isActive,
        lastUpdated = lastUpdated
    )

    private fun StreamEntity.toModel() = StreamItem(
        id = id,
        playlistId = playlistId,
        streamId = streamId,
        name = name,
        streamUrl = streamUrl,
        logoUrl = logoUrl,
        category = category,
        type = type,
        epgChannelId = epgChannelId,
        isFavorite = isFavorite,
        lastPlayed = lastPlayed,
        watchProgressMs = watchProgressMs,
        durationMs = durationMs,
        releaseDate = releaseDate,
        rating = rating,
        plot = plot,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        seriesId = seriesId
    )
}