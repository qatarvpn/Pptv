package com.example.ui.i18n

object AppStrings {
    data class Strings(
        val appName: String,
        val navLive: String,
        val navMovies: String,
        val navSeries: String,
        val navEpg: String,
        val navFavorites: String,
        val navSettings: String,
        val language: String,
        val english: String,
        val arabic: String,
        val addPlaylist: String,
        val noPlaylists: String,
        val noPlaylistsDesc: String,
        val loadSample: String
    )

    fun get(isArabic: Boolean): Strings {
        // English only - always return English
        return Strings(
            appName = "IPTV Player",
            navLive = "Live",
            navMovies = "Movies",
            navSeries = "Series",
            navEpg = "EPG",
            navFavorites = "Favorites",
            navSettings = "Settings",
            language = "Language",
            english = "English",
            arabic = "العربية",
            addPlaylist = "Add Playlist",
            noPlaylists = "No Playlists",
            noPlaylistsDesc = "Add a playlist to get started",
            loadSample = "Load Sample"
        )
    }
}
