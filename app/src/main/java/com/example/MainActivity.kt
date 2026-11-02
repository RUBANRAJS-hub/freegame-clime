package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.LivePulseIndicator
import com.example.ui.screens.AutomationScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.GameDetailSheet
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.StoresScreen
import com.example.ui.screens.UserProfileDialog
import com.example.ui.theme.ClaimGreen
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberBgSecondary
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameClaimViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {

    private val viewModel: GameClaimViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
                val activeGame by viewModel.activeGameDetails.collectAsStateWithLifecycle()
                val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
                val claims by viewModel.claims.collectAsStateWithLifecycle()
                val accounts by viewModel.connectedAccounts.collectAsStateWithLifecycle()

                val snackbarHostState = remember { SnackbarHostState() }
                var showProfileDialog by remember { mutableStateOf(false) }

                val claimedIds = remember(claims) {
                    claims.filter { it.status == "CLAIMED" }.map { it.gameId }.toSet()
                }

                // Handle back navigation
                BackHandler(enabled = activeGame != null || showProfileDialog || selectedTab != NavigationTab.DASHBOARD) {
                    when {
                        showProfileDialog -> showProfileDialog = false
                        activeGame != null -> viewModel.selectGame(null)
                        selectedTab != NavigationTab.DASHBOARD -> viewModel.selectTab(NavigationTab.DASHBOARD)
                    }
                }

                // Show Snackbar on messages
                LaunchedEffect(snackbarMessage) {
                    snackbarMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.dismissSnackbar()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = CyberBg,
                    contentColor = TextPrimary,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CyberCardBg)
                                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.SportsEsports,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "GAMECLAIM AI",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = TextPrimary
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { viewModel.syncStoresNow() },
                                    modifier = Modifier.testTag("appbar_sync_button")
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Sync Stores",
                                        tint = NeonCyan
                                    )
                                }

                                IconButton(
                                    onClick = { showProfileDialog = true },
                                    modifier = Modifier.testTag("appbar_profile_button")
                                ) {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = "Profile & Settings",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = CyberBgSecondary,
                                titleContentColor = TextPrimary
                            ),
                            windowInsets = WindowInsets.statusBars
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("bottom_nav_bar"),
                            containerColor = CyberBgSecondary,
                            contentColor = TextSecondary
                        ) {
                            val items = listOf(
                                TabItem(NavigationTab.DASHBOARD, Icons.Default.Dashboard, "Dashboard"),
                                TabItem(NavigationTab.EXPLORE, Icons.Default.Explore, "Explore"),
                                TabItem(NavigationTab.LIBRARY, Icons.Default.SportsEsports, "Library"),
                                TabItem(NavigationTab.STORES, Icons.Default.Storefront, "Stores"),
                                TabItem(NavigationTab.AUTOMATION, Icons.Default.Memory, "Automation")
                            )

                            items.forEach { item ->
                                val isSelected = selectedTab == item.tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.selectTab(item.tab) },
                                    icon = {
                                        Icon(
                                            item.icon,
                                            contentDescription = item.label,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CyberBg,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = NeonCyan,
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${item.tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Crossfade(targetState = selectedTab, label = "tab_fade") { tab ->
                            when (tab) {
                                NavigationTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                                NavigationTab.EXPLORE -> ExploreScreen(viewModel = viewModel)
                                NavigationTab.LIBRARY -> LibraryScreen(viewModel = viewModel)
                                NavigationTab.STORES -> StoresScreen(viewModel = viewModel)
                                NavigationTab.AUTOMATION -> AutomationScreen(viewModel = viewModel)
                            }
                        }
                    }

                    // Game Detail Bottom Sheet
                    if (activeGame != null) {
                        val isClaimed = activeGame!!.id in claimedIds
                        GameDetailSheet(
                            game = activeGame,
                            isClaimed = isClaimed,
                            onDismiss = { viewModel.selectGame(null) },
                            onClaimClick = { game ->
                                viewModel.claimGame(game)
                                viewModel.selectGame(null)
                            }
                        )
                    }

                    // User Profile & Settings Dialog
                    if (showProfileDialog) {
                        UserProfileDialog(
                            onDismiss = { showProfileDialog = false },
                            claimedCount = claims.count { it.status == "CLAIMED" },
                            connectedStoresCount = accounts.count { it.isConnected }
                        )
                    }
                }
            }
        }
    }
}

private data class TabItem(
    val tab: NavigationTab,
    val icon: ImageVector,
    val label: String
)
