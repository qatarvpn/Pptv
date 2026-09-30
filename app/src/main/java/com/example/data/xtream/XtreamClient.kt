package com.example.data.xtream

import com.example.data.local.StreamEntity
import com.example.data.model.ChannelType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class XtreamClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()
) {

    data class AuthResult(
        val success: Boolean,
        val message: String? = null,
        val serverInfo: String? = null,
        val expiryDate: String? = null,
        val maxConnections: String? = null
    )

    suspend fun authenticate(
        serverUrl: String,
        user: String,
        pass: String
    ): AuthResult = withContext(Dispatchers.IO) {
        try {
            val base = sanitizeServerUrl(serverUrl)
            val url = "$base/player_api.php?username=$user&password=$pass"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            
            return@withContext try {
                val body = response.body?.string() ?: return@withContext AuthResult(false, "Empty server response")
                val json = JSONObject(body)
                val userInfo = json.optJSONObject("user_info")
                
                if (userInfo != null && userInfo.optString("auth") == "1" && userInfo.optString("status") == "Active") {
                    val serverInfo = json.optJSONObject("server_info")
                    val expDate = userInfo.optString("exp_date")
                    val maxConns = userInfo.optString("max_connections")
                    AuthResult(
                        success = true,
                        serverInfo = serverInfo?.optString("url") ?: base,
                        expiryDate = expDate,
                        maxConnections = maxConns
                    )
                } else {
                    val msg = userInfo?.optString("message", "Authentication failed. Check credentials.") ?: "Invalid response"
                    AuthResult(false, msg)
                }
            } finally {
                response.close()
            }
        } catch (e: Exception) {
            AuthResult(false, e.localizedMessage ?: "Connection error")
        }
    }

    suspend fun fetchAllStreams(
        playlistId: Long,
        serverUrl: String,
        user: String,
        pass: String
    ): List<StreamEntity> = withContext(Dispatchers.IO) {
        val base = sanitizeServerUrl(serverUrl)
        val result = mutableListOf<StreamEntity>()

        // Fetch category mappings for organizing streams
        val liveCatMap = fetchCategoriesMap(base, user, pass, "get_live_categories")
        val vodCatMap = fetchCategoriesMap(base, user, pass, "get_vod_categories")
        val seriesCatMap = fetchCategoriesMap(base, user, pass, "get_series_categories")

        // Fetch Live Streams
        try {
            val liveUrl = "$base/player_api.php?username=$user&password=$pass&action=get_live_streams"
            val liveJson = fetchJsonArray(liveUrl)
            for (i in 0 until liveJson.length()) {
                val item = liveJson.getJSONObject(i)
                val streamId = item.optString("stream_id")
                val name = item.optString("name", "Channel")
                val icon = item.optString("stream_icon").takeIf { it.isNotBlank() }
                val catId = item.optString("category_id")
                val catName = liveCatMap[catId] ?: "Live TV"
                val epgId = item.optString("epg_channel_id").takeIf { it.isNotBlank() }

                val playUrl = "$base/live/$user/$pass/$streamId.ts"
                result.add(
                    StreamEntity(
                        playlistId = playlistId,
                        streamId = streamId,
                        name = name,
                        streamUrl = playUrl,
                        logoUrl = icon,
                        category = catName,
                        type = ChannelType.LIVE,
                        epgChannelId = epgId
                    )
                )
            }
        } catch (e: Exception) {
            // Log or handle: Live streams fetch failed
        }

        // Fetch VOD Movies
        try {
            val vodUrl = "$base/player_api.php?username=$user&password=$pass&action=get_vod_streams"
            val vodJson = fetchJsonArray(vodUrl)
            for (i in 0 until vodJson.length()) {
                val item = vodJson.getJSONObject(i)
                val streamId = item.optString("stream_id")
                val name = item.optString("name", "Movie")
                val icon = item.optString("stream_icon").takeIf { it.isNotBlank() }
                val catId = item.optString("category_id")
                val catName = vodCatMap[catId] ?: "Movies"
                val ext = item.optString("container_extension", "mp4").ifBlank { "mp4" }
                val rating = item.optString("rating").takeIf { it.isNotBlank() }

                val playUrl = "$base/movie/$user/$pass/$streamId.$ext"
                result.add(
                    StreamEntity(
                        playlistId = playlistId,
                        streamId = streamId,
                        name = name,
                        streamUrl = playUrl,
                        logoUrl = icon,
                        category = catName,
                        type = ChannelType.MOVIE,
                        rating = rating
                    )
                )
            }
        } catch (e: Exception) {
            // Log or handle: VOD streams fetch failed
        }

        // Fetch Series
        try {
            val seriesUrl = "$base/player_api.php?username=$user&password=$pass&action=get_series"
            val seriesJson = fetchJsonArray(seriesUrl)
            for (i in 0 until seriesJson.length()) {
                val item = seriesJson.getJSONObject(i)
                val seriesId = item.optString("series_id")
                val name = item.optString("name", "Series")
                val cover = item.optString("cover").takeIf { it.isNotBlank() }
                val catId = item.optString("category_id")
                val catName = seriesCatMap[catId] ?: "Series"
                val rating = item.optString("rating").takeIf { it.isNotBlank() }
                val plot = item.optString("plot").takeIf { it.isNotBlank() }

                val playUrl = "$base/series/$user/$pass/$seriesId.mp4"
                result.add(
                    StreamEntity(
                        playlistId = playlistId,
                        streamId = seriesId,
                        name = name,
                        streamUrl = playUrl,
                        logoUrl = cover,
                        category = catName,
                        type = ChannelType.SERIES,
                        rating = rating,
                        plot = plot,
                        seriesId = seriesId
                    )
                )
            }
        } catch (e: Exception) {
            // Log or handle: Series fetch failed
        }

        result
    }

    private suspend fun fetchCategoriesMap(
        base: String,
        user: String,
        pass: String,
        action: String
    ): Map<String, String> {
        val map = mutableMapOf<String, String>()
        try {
            val url = "$base/player_api.php?username=$user&password=$pass&action=$action"
            val array = fetchJsonArray(url)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("category_id")
                val name = obj.optString("category_name")
                if (id.isNotBlank() && name.isNotBlank()) {
                    map[id] = name
                }
            }
        } catch (e: Exception) {
            // Categories fetch failed - return empty map
        }
        return map
    }

    private fun fetchJsonArray(url: String): JSONArray {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return try {
            val body = response.body?.string() ?: "[]"
            JSONArray(body)
        } finally {
            response.close()
        }
    }

    private fun sanitizeServerUrl(raw: String): String {
        var trimmed = raw.trim()
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "http://$trimmed"
        }
        if (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length - 1)
        }
        return trimmed
    }
}