package com.example.data.model

enum class ChannelType {
    LIVE,
    MOVIE,
    SERIES
}

enum class PlaylistType {
    M3U_URL,
    M3U_CONTENT,
    XTREAM
}

data class PlaylistItem(
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

data class StreamItem(
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

data class CategorySummary(
    val name: String,
    val type: ChannelType,
    val count: Int
)

data class EpgProgramItem(
    val id: Long = 0,
    val channelId: String,
    val title: String,
    val description: String? = null,
    val startEpoch: Long,
    val endEpoch: Long
) {
    fun isCurrentlyAir(nowEpoch: Long = System.currentTimeMillis()): Boolean {
        return nowEpoch in startEpoch..endEpoch
    }

    fun progressFraction(nowEpoch: Long = System.currentTimeMillis()): Float {
        if (endEpoch <= startEpoch) return 0f
        if (nowEpoch <= startEpoch) return 0f
        if (nowEpoch >= endEpoch) return 1f
        return (nowEpoch - startEpoch).toFloat() / (endEpoch - startEpoch).toFloat()
    }
}

data class XtreamCredentials(
    val serverUrl: String,
    val username: String,
    val password: String
)
