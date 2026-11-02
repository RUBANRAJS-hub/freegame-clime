package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AutomationLogEntity
import com.example.data.model.EmailNotificationEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.LivePulseIndicator
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.ClaimGreen
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberBgSecondary
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ExpiringAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameClaimViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AutomationScreen(
    viewModel: GameClaimViewModel,
    modifier: Modifier = Modifier
) {
    val automationLogs by viewModel.automationLogs.collectAsStateWithLifecycle()
    val emailNotifications by viewModel.emailNotifications.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val selectedEmail by viewModel.selectedEmailDetails.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Monitoring, 1: Audit Logs, 2: Email Logs
    var selectedLogFilter by remember { mutableStateOf("ALL") }

    val logFilters = listOf("ALL", "DISCOVERY", "AUTO_CLAIM", "SECURITY")
    val dateFormatter = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }
    val fullDateFormatter = remember { SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.US) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .testTag("automation_screen")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberBgSecondary)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "System Monitoring & Admin",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Background scheduler and claim automation engine",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                LivePulseIndicator()
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub tabs
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = CyberCardBg,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                        color = NeonCyan
                    )
                }
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = { Text("Engine Health", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = { Text("Audit Logs (${automationLogs.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSubTab == 2,
                    onClick = { activeSubTab = 2 },
                    text = { Text("Emails (${emailNotifications.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Content Area based on tab
        when (activeSubTab) {
            0 -> EngineHealthView(viewModel = viewModel, isSyncing = isSyncing)
            1 -> AuditLogsView(
                logs = automationLogs,
                selectedFilter = selectedLogFilter,
                onFilterSelected = { selectedLogFilter = it },
                dateFormatter = dateFormatter
            )
            2 -> EmailNotificationsView(
                emails = emailNotifications,
                onSelectEmail = { viewModel.selectEmail(it) },
                dateFormatter = fullDateFormatter
            )
        }
    }

    // Email Preview Dialog
    selectedEmail?.let { email ->
        AlertDialog(
            onDismissRequest = { viewModel.selectEmail(null) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dispatched Email Notification",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = "Subject: ${email.subject}", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "To: ${email.recipient}", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "Sent: ${fullDateFormatter.format(Date(email.sentAt))}", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCardBg)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = email.previewText + "\n\n" + email.htmlBody.replace("<[^>]*>".toRegex(), " ").replace("\\s+".toRegex(), " ").trim(),
                            color = TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.selectEmail(null) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = CyberBg
                    )
                ) {
                    Text("Close")
                }
            },
            containerColor = CyberBgSecondary,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun EngineHealthView(
    viewModel: GameClaimViewModel,
    isSyncing: Boolean
) {
    var selectedInterval by remember { mutableStateOf("30 min") }
    val intervals = listOf("15 min", "30 min", "1 hr", "6 hr")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Scheduler State Card
        item {
            CyberCard(borderColor = NeonCyan.copy(alpha = 0.5f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NeonCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Memory, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "BACKGROUND SCHEDULER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = if (isSyncing) "Scanning Active Stores..." else "Idle (Waiting Next Interval)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSyncing) ExpiringAmber else ClaimGreen
                                )
                            }
                        }

                        CyberBadge(text = "PID: #4921", color = ClaimGreen)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Scan Interval", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        intervals.forEach { interval ->
                            val isSelected = selectedInterval == interval
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonCyan else CyberBgSecondary)
                                    .clickable { selectedInterval = interval }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = interval,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                    color = if (isSelected) CyberBg else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.syncStoresNow() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSyncing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = CyberBg
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberBg, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isSyncing) "Executing Store Scans..." else "Force Run Store Scan Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Store API Health Endpoints
        item {
            Text(
                text = "Store Discovery Integrations",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        val endpoints = listOf(
            Triple("GamerPower Giveaways Feed", "200 OK • 120ms", ClaimGreen),
            Triple("Epic Games Store Promotions API", "200 OK • 145ms", ClaimGreen),
            Triple("Steam Store Promotional Webhook", "200 OK • 88ms", ClaimGreen),
            Triple("GOG GALAXY Free Claim Parser", "200 OK • 210ms", ClaimGreen),
            Triple("Prime Gaming Loot Dispatcher", "200 OK • 190ms", ClaimGreen)
        )

        items(endpoints) { (name, statusText, color) ->
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Official endpoint protocol", fontSize = 11.sp, color = TextMuted)
                    }
                    CyberBadge(text = statusText, color = color)
                }
            }
        }
    }
}

@Composable
fun AuditLogsView(
    logs: List<AutomationLogEntity>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    dateFormatter: SimpleDateFormat
) {
    val filteredLogs = logs.filter {
        selectedFilter == "ALL" || it.eventType.equals(selectedFilter, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("ALL", "DISCOVERY", "AUTO_CLAIM", "SECURITY")) { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonCyan else CyberCardBg)
                        .clickable { onFilterSelected(filter) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        color = if (isSelected) CyberBg else TextSecondary
                    )
                }
            }
        }

        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No logs for this filter.", color = TextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredLogs, key = { it.id }) { log ->
                    CyberCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CyberBadge(
                                        text = log.eventType,
                                        color = when (log.eventType) {
                                            "AUTO_CLAIM" -> ClaimGreen
                                            "DISCOVERY" -> NeonCyan
                                            "SECURITY" -> ElectricPurple
                                            else -> ExpiringAmber
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = log.platform,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                }

                                Text(
                                    text = dateFormatter.format(Date(log.timestamp)),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = log.message,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )

                            if (log.details.isNotBlank()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = log.details,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmailNotificationsView(
    emails: List<EmailNotificationEntity>,
    onSelectEmail: (EmailNotificationEntity) -> Unit,
    dateFormatter: SimpleDateFormat
) {
    if (emails.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No emails dispatched yet.", color = TextMuted, fontSize = 14.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(emails, key = { it.id }) { email ->
                CyberCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectEmail(email) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CyberBadge(
                                text = email.triggerType,
                                color = if (email.triggerType == "GAME_CLAIMED") ClaimGreen else NeonCyan
                            )
                            Text(
                                text = dateFormatter.format(Date(email.sentAt)),
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = email.subject,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = email.previewText,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "To: ${email.recipient}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "Tap to Preview HTML Email →",
                                fontSize = 11.sp,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
