package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.EpgProgramEntity
import com.example.data.local.IptvDatabase
import com.example.data.model.ChannelType
import com.example.data.model.PlaylistItem
import com.example.data.model.StreamItem
import com.example.data.repo.IptvRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavScreen {
    LIVE_TV,
    MOVIES,
    SERIES,
    EPG,
    FAVORITES,
    SETTINGS,
    SEARCH
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = IptvDatabase.getInstance(application)
    private val repository = IptvRepository(db.iptvDao())

    // Language state (default Arabic/English switcher)
    private val _isArabic = MutableStateFlow(true)
    val isArabic: StateFlow<Boolean> = _isArabic.asStateFlow()

    // Navigation State
    private val _currentScreen = MutableStateFlow(AppNavScreen.LIVE_TV)
    val currentScreen: StateFlow<AppNavScreen> = _currentScreen.asStateFlow()

    // Video Player Active Stream (null = normal screens, non-null = video player active)
    private val _currentPlayingStream = MutableStateFlow<StreamItem?>(null)
    val currentPlayingStream: StateFlow<StreamItem?> = _currentPlayingStream.asStateFlow()

    // Dialog & Loading
    private val _isAddPlaylistOpen = MutableStateFlow(false)
    val isAddPlaylistOpen: StateFlow<Boolean> = _isAddPlaylistOpen.asStateFlow()

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _importMessage = MutableStateFlow<String?>(null)
    val importMessage: StateFlow<String?> = _importMessage.asStateFlow()

    // Playlists
    val allPlaylists: StateFlow<List<PlaylistItem>> = repository.allPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activePlaylist: StateFlow<PlaylistItem?> = repository.activePlaylist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Streams by Active Playlist
    val liveChannels: StateFlow<List<StreamItem>> = activePlaylist.flatMapLatest { pl ->
        if (pl != null) repository.getStreams(pl.id, ChannelType.LIVE) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveCategories: StateFlow<List<String>> = activePlaylist.flatMapLatest { pl ->
        if (pl != null) repository.getCategories(pl.id, ChannelType.LIVE) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val movies: StateFlow<List<StreamItem>> = activePlaylist.flatMapLatest { pl ->
        if (pl != null) repository.getStreams(pl.id, ChannelType.MOVIE) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val movieCategories: StateFlow<List<String>> = activePlaylist.flatMapLatest { pl ->
        if (pl != null) repository.getCategories(pl.id, ChannelType.MOVIE) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val series: StateFlow<List<StreamItem>> = activePlaylist.flatMapLatest { pl ->
        if (pl != null) repository.getStreams(pl.id, ChannelType.SERIES) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val seriesCategories: StateFlow<List<String>> = activePlaylist.flatMapLatest { pl ->
        if (pl != null) repository.getCategories(pl.id, ChannelType.SERIES) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<StreamItem>> = repository.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<StreamItem>> = repository.getWatchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // EPG Map for active channels
    private val _epgMap = MutableStateFlow<Map<String, List<EpgProgramEntity>>>(emptyMap())
    val epgMap: StateFlow<Map<String, List<EpgProgramEntity>>> = _epgMap.asStateFlow()

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<StreamItem>> = combine(
        activePlaylist,
        _searchQuery
    ) { pl: PlaylistItem?, query: String ->
        Pair(pl?.id ?: 0L, query)
    }.flatMapLatest { (playlistId, query) ->
        if (query.isBlank()) {
            flowOf(emptyList())
        } else {
            repository.searchStreams(playlistId, query)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.loadSampleDataIfNeeded()
            loadEpgForLiveChannels()
        }
    }

    private fun loadEpgForLiveChannels() {
        viewModelScope.launch {
            liveChannels.collect { channels ->
                val map = mutableMapOf<String, List<EpgProgramEntity>>()
                channels.forEach { ch ->
                    val id = ch.epgChannelId ?: ch.streamId
                    repository.getUpcomingEpg(id).collect { list ->
                        map[id] = list
                        _epgMap.value = map.toMap()
                    }
                }
            }
        }
    }

    fun setScreen(screen: AppNavScreen) {
        _currentScreen.value = screen
    }

    fun toggleLanguage(isAr: Boolean) {
        _isArabic.value = isAr
    }

    fun setPlayingStream(stream: StreamItem?) {
        _currentPlayingStream.value = stream
    }

    fun setAddPlaylistOpen(open: Boolean) {
        _isAddPlaylistOpen.value = open
        _importMessage.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(stream: StreamItem) {
        viewModelScope.launch {
            repository.toggleFavorite(stream.id, stream.isFavorite)
        }
    }

    fun saveWatchProgress(streamId: Long, progressMs: Long, durationMs: Long) {
        viewModelScope.launch {
            repository.recordWatchProgress(streamId, progressMs, durationMs)
        }
    }

    fun selectActivePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.setActivePlaylist(playlistId)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    fun loadSamplePlaylist() {
        viewModelScope.launch {
            _isImporting.value = true
            _importMessage.value = null
            repository.forceLoadSampleData()
            _isImporting.value = false
            _isAddPlaylistOpen.value = false
        }
    }

    fun importM3u(name: String, url: String) {
        viewModelScope.launch {
            _isImporting.value = true
            _importMessage.value = null
            val result = repository.importM3uUrl(name, url)
            _isImporting.value = false
            if (result.isSuccess) {
                _isAddPlaylistOpen.value = false
            } else {
                _importMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to import M3U"
            }
        }
    }

    fun importXtream(name: String, server: String, user: String, pass: String) {
        viewModelScope.launch {
            _isImporting.value = true
            _importMessage.value = null
            val result = repository.importXtream(name, server, user, pass)
            _isImporting.value = false
            if (result.isSuccess) {
                _isAddPlaylistOpen.value = false
            } else {
                _importMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Xtream login error"
            }
        }
    }
}
