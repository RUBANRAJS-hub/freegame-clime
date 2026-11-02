package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AutomationLogEntity
import com.example.data.model.ClaimRecordEntity
import com.example.data.model.ConnectedAccountEntity
import com.example.data.model.EmailNotificationEntity
import com.example.data.model.GameEntity
import com.example.data.repository.GameClaimRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab(val label: String) {
    DASHBOARD("Dashboard"),
    EXPLORE("Explore"),
    LIBRARY("Library"),
    STORES("Stores"),
    AUTOMATION("Automation")
}

class GameClaimViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameClaimRepository(application)

    val connectedAccounts: StateFlow<List<ConnectedAccountEntity>> = repository.connectedAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val claims: StateFlow<List<ClaimRecordEntity>> = repository.claims
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automationLogs: StateFlow<List<AutomationLogEntity>> = repository.automationLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emailNotifications: StateFlow<List<EmailNotificationEntity>> = repository.emailNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSaved: StateFlow<Double> = repository.totalSaved
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val claimedCount: StateFlow<Int> = repository.claimedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _selectedTab = MutableStateFlow(NavigationTab.DASHBOARD)
    val selectedTab: StateFlow<NavigationTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPlatformFilter = MutableStateFlow("ALL")
    val selectedPlatformFilter: StateFlow<String> = _selectedPlatformFilter.asStateFlow()

    private val _selectedAvailabilityFilter = MutableStateFlow("ALL")
    val selectedAvailabilityFilter: StateFlow<String> = _selectedAvailabilityFilter.asStateFlow()

    private val _activeGameDetails = MutableStateFlow<GameEntity?>(null)
    val activeGameDetails: StateFlow<GameEntity?> = _activeGameDetails.asStateFlow()

    private val _selectedEmailDetails = MutableStateFlow<EmailNotificationEntity?>(null)
    val selectedEmailDetails: StateFlow<EmailNotificationEntity?> = _selectedEmailDetails.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Filtered Games Flow
    val filteredGames: StateFlow<List<GameEntity>> = combine(
        repository.games,
        _searchQuery,
        _selectedPlatformFilter,
        _selectedAvailabilityFilter
    ) { allGames, query, platform, availability ->
        allGames.filter { game ->
            val matchesQuery = query.isBlank() ||
                    game.title.contains(query, ignoreCase = true) ||
                    game.genres.contains(query, ignoreCase = true) ||
                    game.platform.contains(query, ignoreCase = true)

            val matchesPlatform = platform == "ALL" || game.platform.equals(platform, ignoreCase = true)

            val matchesAvailability = when (availability) {
                "ACTIVE" -> game.status == "ACTIVE" || game.status == "EXPIRING_SOON"
                "EXPIRING_SOON" -> game.status == "EXPIRING_SOON"
                "UPCOMING" -> game.status == "UPCOMING"
                else -> true
            }

            matchesQuery && matchesPlatform && matchesAvailability
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawGames: StateFlow<List<GameEntity>> = repository.games
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            // Run background sync
            syncStoresNow()
        }
    }

    fun selectTab(tab: NavigationTab) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPlatformFilter(platform: String) {
        _selectedPlatformFilter.value = platform
    }

    fun setAvailabilityFilter(availability: String) {
        _selectedAvailabilityFilter.value = availability
    }

    fun selectGame(game: GameEntity?) {
        _activeGameDetails.value = game
    }

    fun selectEmail(email: EmailNotificationEntity?) {
        _selectedEmailDetails.value = email
    }

    fun dismissSnackbar() {
        _snackbarMessage.value = null
    }

    fun syncStoresNow() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            val result = repository.syncGiveawaysFromNetwork()
            _isSyncing.value = false
            if (result.isSuccess) {
                _snackbarMessage.value = "Stores synchronized! ${result.getOrNull() ?: 0} active free games."
            } else {
                _snackbarMessage.value = "Sync completed using offline verified cache."
            }
        }
    }

    fun claimGame(game: GameEntity) {
        viewModelScope.launch {
            val claim = repository.claimGame(game, isAutomated = false)
            if (claim.status == "CLAIMED") {
                _snackbarMessage.value = "🎉 Successfully claimed '${game.title}' to your ${game.platform} account!"
            } else {
                _snackbarMessage.value = "⚠️ Manual action required for ${game.platform}. Opened store link."
            }
        }
    }

    fun quickClaimAll() {
        viewModelScope.launch {
            val allGames = rawGames.value
            val claimedIds = claims.value.filter { it.status == "CLAIMED" }.map { it.gameId }.toSet()
            val unclaimed = allGames.filter { it.id !in claimedIds }

            if (unclaimed.isEmpty()) {
                _snackbarMessage.value = "All available games are already in your library!"
                return@launch
            }

            var claimedCount = 0
            for (game in unclaimed) {
                val claim = repository.claimGame(game, isAutomated = true)
                if (claim.status == "CLAIMED") {
                    claimedCount++
                }
            }
            _snackbarMessage.value = "⚡ Batch claim processed! $claimedCount games added to library."
        }
    }

    fun togglePlatformConnection(platformId: String, connect: Boolean, accountName: String = "") {
        viewModelScope.launch {
            repository.updatePlatformConnection(platformId, connect, accountName)
            _snackbarMessage.value = if (connect) "Connected to $platformId account!" else "Disconnected from $platformId."
        }
    }

    fun toggleAutoClaim(platformId: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAutoClaim(platformId, enabled)
            _snackbarMessage.value = if (enabled) "Auto-claim enabled for $platformId." else "Auto-claim paused for $platformId."
        }
    }
}
