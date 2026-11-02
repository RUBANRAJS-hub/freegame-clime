package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val platform: String, // "Epic Games", "Steam", "GOG", "Prime Gaming", "Humble"
    val storeUrl: String,
    val thumbnailUrl: String,
    val originalPrice: Double,
    val worthString: String,
    val endDate: String,
    val isWorthIt: Boolean = true,
    val instructions: String = "",
    val genres: String = "Action, Adventure",
    val status: String = "ACTIVE", // "ACTIVE", "EXPIRING_SOON", "UPCOMING", "EXPIRED"
    val discoveredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "connected_accounts")
data class ConnectedAccountEntity(
    @PrimaryKey
    val platformId: String, // "EPIC", "STEAM", "GOG", "PRIME"
    val platformName: String,
    val accountName: String,
    val accountIdentifier: String,
    val isConnected: Boolean,
    val connectedAt: Long,
    val avatarUrl: String = "",
    val autoClaimEnabled: Boolean = true,
    val lastSyncTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "claim_records")
data class ClaimRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: String,
    val gameTitle: String,
    val platform: String,
    val status: String, // "CLAIMED", "MANUAL_ACTION", "ALREADY_OWNED", "PROCESSING", "FAILED"
    val claimedAt: Long = System.currentTimeMillis(),
    val valueSaved: Double,
    val confirmationCode: String,
    val storeClaimUrl: String,
    val notes: String = ""
)

@Entity(tableName = "automation_logs")
data class AutomationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // "DISCOVERY", "AUTO_CLAIM", "SYNC", "SECURITY", "EXPIRY_WARNING"
    val platform: String,
    val status: String, // "SUCCESS", "INFO", "WARNING", "ERROR"
    val message: String,
    val details: String = ""
)

@Entity(tableName = "email_notifications")
data class EmailNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipient: String,
    val subject: String,
    val previewText: String,
    val htmlBody: String,
    val sentAt: Long = System.currentTimeMillis(),
    val triggerType: String, // "NEW_GAME", "GAME_CLAIMED", "MANUAL_REQUIRED", "EXPIRING_SOON"
    val status: String = "DELIVERED"
)

enum class ClaimStatus(val displayName: String) {
    PENDING("Pending"),
    PROCESSING("Processing"),
    CLAIMED("Successfully Claimed"),
    ALREADY_OWNED("Already Owned"),
    MANUAL_ACTION("Manual Action Required"),
    FAILED("Failed"),
    EXPIRED("Expired")
}
