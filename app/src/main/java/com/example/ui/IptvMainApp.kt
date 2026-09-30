package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.player.VideoPlayerComposable
import com.example.ui.components.AddPlaylistDialog
import com.example.ui.i18n.AppStrings
import com.example.ui.screens.EpgScreen
import com.example.ui.screens.FavoritesHistoryScreen
import com.example.ui.screens.LiveTvScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SeriesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VodMoviesScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LiveRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IptvMainApp(
    viewModel: MainViewModel
) {
    // Always use English (isArabic = false)
    val isArabic = false
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val playingStream by viewModel.currentPlayingStream.collectAsStateWithLifecycle()

    val activePlaylist by viewModel.activePlaylist.collectAsStateWithLifecycle()
    val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()

    val liveChannels by viewModel.liveChannels.collectAsStateWithLifecycle()
    val liveCategories by viewModel.liveCategories.collectAsStateWithLifecycle()

    val movies by viewModel.movies.collectAsStateWithLifecycle()
    val movieCategories by viewModel.movieCategories.collectAsStateWithLifecycle()

    val series by viewModel.series.collectAsStateWithLifecycle()
    val seriesCategories by viewModel.seriesCategories.collectAsStateWithLifecycle()

    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val epgMap by viewModel.epgMap.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val isAddPlaylistOpen by viewModel.isAddPlaylistOpen.collectAsStateWithLifecycle()
    val isImporting by viewModel.isImporting.collectAsStateWithLifecycle()

    val strings = AppStrings.get(isArabic)
    val layoutDirection = LayoutDirection.Ltr  // Always LTR for English

    // Handle System Back button
    BackHandler(enabled = playingStream != null || currentScreen != AppNavScreen.LIVE_TV) {
        if (playingStream != null) {
            viewModel.setPlayingStream(null)
        } else if (currentScreen != AppNavScreen.LIVE_TV) {
            viewModel.setScreen(AppNavScreen.LIVE_TV)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
        ) {
            // Full Video Player overlay if playing
            if (playingStream != null) {
                val currentCategoryStreams = when (playingStream!!.type) {
                    com.example.data.model.ChannelType.LIVE -> liveChannels.filter { it.category == playingStream!!.category }
                    com.example.data.model.ChannelType.MOVIE -> movies.filter { it.category == playingStream!!.category }
                    com.example.data.model.ChannelType.SERIES -> series.filter { it.seriesId == playingStream!!.seriesId }
                }

                VideoPlayerComposable(
                    stream = playingStream!!,
                    allStreamsInCat = currentCategoryStreams,
                    onClosePlayer = { viewModel.setPlayingStream(null) },
                    onSelectStream = { newStream -> viewModel.setPlayingStream(newStream) },
                    onToggleFavorite = { stream -> viewModel.toggleFavorite(stream) },
                    onProgressUpdate = { pos, dur ->
                        viewModel.saveWatchProgress(playingStream!!.id, pos, dur)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(LiveRed, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strings.appName,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                    if (activePlaylist != null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = DarkSurfaceVariant,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = activePlaylist!!.name,
                                                color = AccentCyan,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            actions = {
                                // Search Icon
                                IconButton(onClick = { viewModel.setScreen(AppNavScreen.SEARCH) }) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = if (currentScreen == AppNavScreen.SEARCH) AccentCyan else TextPrimary
                                    )
                                }

                                // Quick Add Playlist
                                IconButton(onClick = { viewModel.setAddPlaylistOpen(true) }) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Playlist", tint = TextPrimary)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = DarkSurface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = DarkSurface,
                            contentColor = TextPrimary
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == AppNavScreen.LIVE_TV,
                                onClick = { viewModel.setScreen(AppNavScreen.LIVE_TV) },
                                icon = { Icon(Icons.Default.LiveTv, contentDescription = strings.navLive) },
                                label = { Text(strings.navLive, fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = AccentCyan,
                                    indicatorColor = PrimaryBlue,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppNavScreen.MOVIES,
                                onClick = { viewModel.setScreen(AppNavScreen.MOVIES) },
                                icon = { Icon(Icons.Default.Movie, contentDescription = strings.navMovies) },
                                label = { Text(strings.navMovies, fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = AccentCyan,
                                    indicatorColor = PrimaryBlue,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppNavScreen.SERIES,
                                onClick = { viewModel.setScreen(AppNavScreen.SERIES) },
                                icon = { Icon(Icons.Default.Tv, contentDescription = strings.navSeries) },
                                label = { Text(strings.navSeries, fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = AccentCyan,
                                    indicatorColor = PrimaryBlue,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppNavScreen.EPG,
                                onClick = { viewModel.setScreen(AppNavScreen.EPG) },
                                icon = { Icon(Icons.Default.Schedule, contentDescription = strings.navEpg) },
                                label = { Text(strings.navEpg, fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = AccentCyan,
                                    indicatorColor = PrimaryBlue,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppNavScreen.FAVORITES,
                                onClick = { viewModel.setScreen(AppNavScreen.FAVORITES) },
                                icon = { Icon(Icons.Default.Favorite, contentDescription = strings.navFavorites) },
                                label = { Text(strings.navFavorites, fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = AccentCyan,
                                    indicatorColor = PrimaryBlue,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppNavScreen.SETTINGS,
                                onClick = { viewModel.setScreen(AppNavScreen.SETTINGS) },
                                icon = { Icon(Icons.Default.Settings, contentDescription = strings.navSettings) },
                                label = { Text(strings.navSettings, fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = AccentCyan,
                                    indicatorColor = PrimaryBlue,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "ScreenTransition",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) { screen ->
                        when (screen) {
                            AppNavScreen.LIVE_TV -> LiveTvScreen(
                                channels = liveChannels,
                                categories = liveCategories,
                                strings = strings,
                                onPlayChannel = { ch -> viewModel.setPlayingStream(ch) },
                                onToggleFavorite = { ch -> viewModel.toggleFavorite(ch) },
                                onOpenAddPlaylist = { viewModel.setAddPlaylistOpen(true) }
                            )

                            AppNavScreen.MOVIES -> VodMoviesScreen(
                                movies = movies,
                                categories = movieCategories,
                                strings = strings,
                                onPlayMovie = { m -> viewModel.setPlayingStream(m) },
                                onToggleFavorite = { m -> viewModel.toggleFavorite(m) }
                            )

                            AppNavScreen.SERIES -> SeriesScreen(
                                seriesList = series,
                                categories = seriesCategories,
                                strings = strings,
                                onPlayEpisode = { ep -> viewModel.setPlayingStream(ep) },
                                onToggleFavorite = { s -> viewModel.toggleFavorite(s) }
                            )

                            AppNavScreen.EPG -> EpgScreen(
                                channels = liveChannels,
                                epgMap = epgMap,
                                strings = strings,
                                onPlayChannel = { ch -> viewModel.setPlayingStream(ch) }
                            )

                            AppNavScreen.FAVORITES -> FavoritesHistoryScreen(
                                favorites = favorites,
                                history = history,
                                strings = strings,
                                onPlayStream = { st -> viewModel.setPlayingStream(st) },
                                onToggleFavorite = { st -> viewModel.toggleFavorite(st) }
                            )

                            AppNavScreen.SETTINGS -> SettingsScreen(
                                playlists = allPlaylists,
                                activePlaylist = activePlaylist,
                                isArabic = isArabic,
                                strings = strings,
                                onToggleLanguage = { viewModel.toggleLanguage(it) },
                                onSelectActivePlaylist = { id -> viewModel.selectActivePlaylist(id) },
                                onDeletePlaylist = { id -> viewModel.deletePlaylist(id) },
                                onOpenAddPlaylist = { viewModel.setAddPlaylistOpen(true) },
                                onLoadSamplePlaylist = { viewModel.loadSamplePlaylist() }
                            )

                            AppNavScreen.SEARCH -> SearchScreen(
                                searchQuery = searchQuery,
                                onQueryChange = { viewModel.setSearchQuery(it) },
                                searchResults = searchResults,
                                strings = strings,
                                onPlayStream = { st -> viewModel.setPlayingStream(st) },
                                onToggleFavorite = { st -> viewModel.toggleFavorite(st) }
                            )
                        }
                    }
                }
            }

            // Add Playlist Dialog Modal
            if (isAddPlaylistOpen) {
                AddPlaylistDialog(
                    strings = strings,
                    isLoading = isImporting,
                    onDismiss = { viewModel.setAddPlaylistOpen(false) },
                    onImportM3u = { name, url -> viewModel.importM3u(name, url) },
                    onImportXtream = { name, server, user, pass ->
                        viewModel.importXtream(name, server, user, pass)
                    },
                    onLoadSample = { viewModel.loadSamplePlaylist() }
                )
            }
        }
    }
}
