package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.GameEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.ExpiringBadge
import com.example.ui.components.LivePulseIndicator
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.ClaimGreen
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberBgSecondary
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.ExpiringAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameClaimViewModel
import com.example.ui.viewmodel.NavigationTab
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: GameClaimViewModel,
    modifier: Modifier = Modifier
) {
    val games by viewModel.rawGames.collectAsStateWithLifecycle()
    val claims by viewModel.claims.collectAsStateWithLifecycle()
    val accounts by viewModel.connectedAccounts.collectAsStateWithLifecycle()
    val totalSaved by viewModel.totalSaved.collectAsStateWithLifecycle()
    val claimedCount by viewModel.claimedCount.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    val claimedGameIds = claims.filter { it.status == "CLAIMED" }.map { it.gameId }.toSet()
    val connectedCount = accounts.count { it.isConnected }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(CyberBgSecondary, CyberBg)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GAMECLAIM AI",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = NeonCyan
                            )
                            Text(
                                text = "Automated Free PC Games Engine",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        // Background Worker Pulse indicator
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(CyberCardBg)
                                .border(1.dp, ClaimGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LivePulseIndicator(modifier = Modifier.padding(end = 6.dp))
                            Text(
                                text = "WORKER LIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClaimGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action bar: Quick Claim All & Scan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.quickClaimAll() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_claim_all_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = CyberBg
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Claim All Free", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.syncStoresNow() },
                            modifier = Modifier.testTag("scan_stores_button"),
                            enabled = !isSyncing,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = NeonCyan
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(NeonCyan, ElectricPurple))
                            )
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = NeonCyan
                                )
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = "Scan", modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isSyncing) "Scanning..." else "Scan Now", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Stats Grid (4 Cards)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Free Right Now",
                        value = "${games.size}",
                        subtitle = "Active promotions",
                        icon = Icons.Default.LocalOffer,
                        accentColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Games Claimed",
                        value = "$claimedCount",
                        subtitle = "In your library",
                        icon = Icons.Default.Games,
                        accentColor = ElectricPurple,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Money Saved",
                        value = "$${String.format(Locale.US, "%.2f", totalSaved)}",
                        subtitle = "100% Free value",
                        icon = Icons.Default.Savings,
                        accentColor = ClaimGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Stores Linked",
                        value = "$connectedCount / ${accounts.size}",
                        subtitle = "Auto-sync ready",
                        icon = Icons.Default.Storefront,
                        accentColor = ExpiringAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Urgent Free Drops (Carousel)
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Featured Free Games",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CyberBadge(text = "LIMITED TIME", color = ExpiringAmber)
                    }

                    Text(
                        text = "View All",
                        fontSize = 13.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            viewModel.selectTab(NavigationTab.EXPLORE)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(games.take(6), key = { it.id }) { game ->
                        val isClaimed = game.id in claimedGameIds
                        FeaturedGameCard(
                            game = game,
                            isClaimed = isClaimed,
                            onCardClick = { viewModel.selectGame(game) },
                            onClaimClick = { viewModel.claimGame(game) }
                        )
                    }
                }
            }
        }

        // Connected Gaming Platforms Bar
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Platform Integrations",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Manage",
                        fontSize = 13.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            viewModel.selectTab(NavigationTab.STORES)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accounts.forEach { account ->
                        CyberCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectTab(NavigationTab.STORES) }
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                PlatformBadge(platform = account.platformName)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (account.isConnected) ClaimGreen else TextMuted)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (account.isConnected) "Linked" else "Connect",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (account.isConnected) ClaimGreen else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Activity & Claims
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Recent Automation Activity",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (claims.isEmpty()) {
            item {
                CyberCard(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)) {
                    Text(
                        text = "No games claimed yet. Tap 'Claim All Free' to start building your library!",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(claims.take(5), key = { it.id }) { claim ->
                CyberCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ClaimGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ClaimGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = claim.gameTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${claim.platform} • Saved $${String.format(Locale.US, "%.2f", claim.valueSaved)}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        CyberBadge(
                            text = claim.status,
                            color = if (claim.status == "CLAIMED") ClaimGreen else ExpiringAmber
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    CyberCard(
        modifier = modifier,
        borderColor = accentColor.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 11.sp, color = TextMuted)
        }
    }
}

@Composable
fun FeaturedGameCard(
    game: GameEntity,
    isClaimed: Boolean,
    onCardClick: () -> Unit,
    onClaimClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CyberCard(
        modifier = modifier
            .width(260.dp)
            .clickable { onCardClick() },
        borderColor = if (isClaimed) ClaimGreen.copy(alpha = 0.4f) else CyberCardBorder
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = game.thumbnailUrl,
                    contentDescription = game.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, CyberCardBg.copy(alpha = 0.9f))
                            )
                        )
                )

                // Platform badge top left
                Box(modifier = Modifier.padding(8.dp)) {
                    PlatformBadge(platform = game.platform)
                }

                // 100% OFF badge top right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    CyberBadge(text = "100% FREE", color = ClaimGreen)
                }

                if (game.status == "EXPIRING_SOON") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        ExpiringBadge(deadline = game.endDate)
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = game.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = game.worthString,
                        fontSize = 12.sp,
                        color = TextMuted,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FREE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = ClaimGreen
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isClaimed) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ClaimGreen.copy(alpha = 0.2f))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ClaimGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("In Library", color = ClaimGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = onClaimClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("claim_game_${game.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = CyberBg
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("Claim Free", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
