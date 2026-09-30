package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChannelType
import kotlinx.coroutines.flow.Flow

@Dao
interface IptvDao {

    // --- Playlists ---
    @Query("SELECT * FROM playlists ORDER BY id DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE isActive = 1 LIMIT 1")
    fun getActivePlaylist(): Flow<PlaylistEntity?>

    @Query("SELECT * FROM playlists WHERE isActive = 1 LIMIT 1")
    suspend fun getActivePlaylistOnce(): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("UPDATE playlists SET isActive = CASE WHEN id = :playlistId THEN 1 ELSE 0 END")
    suspend fun setActivePlaylist(playlistId: Long)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("DELETE FROM streams WHERE playlistId = :playlistId")
    suspend fun deleteStreamsByPlaylist(playlistId: Long)

    // --- Streams ---
    @Query("SELECT * FROM streams WHERE playlistId = :playlistId AND type = :type ORDER BY name ASC")
    fun getStreams(playlistId: Long, type: ChannelType): Flow<List<StreamEntity>>

    @Query("SELECT * FROM streams WHERE playlistId = :playlistId AND type = :type AND category = :category ORDER BY name ASC")
    fun getStreamsByCategory(playlistId: Long, type: ChannelType, category: String): Flow<List<StreamEntity>>

    @Query("SELECT DISTINCT category FROM streams WHERE playlistId = :playlistId AND type = :type ORDER BY category ASC")
    fun getCategories(playlistId: Long, type: ChannelType): Flow<List<String>>

    @Query("SELECT * FROM streams WHERE isFavorite = 1 ORDER BY lastPlayed DESC, name ASC")
    fun getAllFavorites(): Flow<List<StreamEntity>>

    @Query("SELECT * FROM streams WHERE isFavorite = 1 AND type = :type ORDER BY lastPlayed DESC, name ASC")
    fun getFavoritesByType(type: ChannelType): Flow<List<StreamEntity>>

    @Query("SELECT * FROM streams WHERE lastPlayed > 0 ORDER BY lastPlayed DESC LIMIT 30")
    fun getWatchHistory(): Flow<List<StreamEntity>>

    @Query("SELECT * FROM streams WHERE (playlistId = :playlistId OR :playlistId = 0) AND name LIKE '%' || :query || '%' ORDER BY name ASC LIMIT 100")
    fun searchStreams(playlistId: Long, query: String): Flow<List<StreamEntity>>

    @Query("SELECT * FROM streams WHERE id = :id LIMIT 1")
    suspend fun getStreamById(id: Long): StreamEntity?

    @Query("SELECT * FROM streams WHERE seriesId = :seriesId ORDER BY seasonNumber ASC, episodeNumber ASC")
    fun getEpisodesForSeries(seriesId: String): Flow<List<StreamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<StreamEntity>)

    @Query("UPDATE streams SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE streams SET watchProgressMs = :progressMs, durationMs = :durationMs, lastPlayed = :lastPlayed WHERE id = :id")
    suspend fun updateWatchProgress(id: Long, progressMs: Long, durationMs: Long, lastPlayed: Long)

    @Query("SELECT COUNT(*) FROM streams WHERE playlistId = :playlistId AND type = :type")
    suspend fun getStreamCount(playlistId: Long, type: ChannelType): Int

    // --- EPG ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpgPrograms(programs: List<EpgProgramEntity>)

    @Query("SELECT * FROM epg_programs WHERE channelId = :channelId AND endEpoch >= :nowEpoch ORDER BY startEpoch ASC LIMIT 10")
    fun getUpcomingEpg(channelId: String, nowEpoch: Long): Flow<List<EpgProgramEntity>>

    @Query("SELECT * FROM epg_programs WHERE channelId = :channelId AND :nowEpoch BETWEEN startEpoch AND endEpoch LIMIT 1")
    suspend fun getCurrentProgram(channelId: String, nowEpoch: Long): EpgProgramEntity?

    @Query("DELETE FROM epg_programs WHERE endEpoch < :cutoffEpoch")
    suspend fun cleanupOldEpg(cutoffEpoch: Long)
}
