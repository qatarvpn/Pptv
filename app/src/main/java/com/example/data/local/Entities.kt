package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.ChannelType
import com.example.data.model.PlaylistType

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: PlaylistType,
    val sourceUrl: String,
    val username: String? = null,
    val password: String? = null,
    val channelCount: Int = 0,
    val movieCount: Int = 0,
    val seriesCount: Int = 0,
    val isActive: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "streams",
    indices = [
        Index(value = ["playlistId", "type"]),
        Index(value = ["playlistId", "category"]),
        Index(value = ["isFavorite"]),
        Index(value = ["lastPlayed"])
    ]
)
data class StreamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playlistId: Long,
    val streamId: String,
    val name: String,
    val streamUrl: String,
    val logoUrl: String? = null,
    val category: String = "General",
    val type: ChannelType = ChannelType.LIVE,
    val epgChannelId: String? = null,
    val isFavorite: Boolean = false,
    val lastPlayed: Long = 0,
    val watchProgressMs: Long = 0,
    val durationMs: Long = 0,
    val releaseDate: String? = null,
    val rating: String? = null,
    val plot: String? = null,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val seriesId: String? = null
)

@Entity(
    tableName = "epg_programs",
    indices = [Index(value = ["channelId", "startEpoch", "endEpoch"])]
)
data class EpgProgramEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val channelId: String,
    val title: String,
    val description: String? = null,
    val startEpoch: Long,
    val endEpoch: Long
)
