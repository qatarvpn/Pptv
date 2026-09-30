package com.example.player

enum class AspectRatioMode {
    FIT,
    FILL,
    ZOOM,
    RATIO_16_9,
    RATIO_4_3
}

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val currentPositionMs: Long = 0,
    val durationMs: Long = 0,
    val isLive: Boolean = true,
    val isControlsVisible: Boolean = true,
    val isFullscreen: Boolean = false,
    val aspectRatio: AspectRatioMode = AspectRatioMode.FIT,
    val errorMessage: String? = null,
    val channelListVisible: Boolean = false
)
