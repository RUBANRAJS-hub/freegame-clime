package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AutomationLogEntity
import com.example.data.model.ClaimRecordEntity
import com.example.data.model.ConnectedAccountEntity
import com.example.data.model.EmailNotificationEntity
import com.example.data.model.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameClaimDao {

    // --- Games ---
    @Query("SELECT * FROM games ORDER BY discoveredAt DESC")
    fun getAllGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :id LIMIT 1")
    suspend fun getGameById(id: String): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Query("SELECT COUNT(*) FROM games")
    fun getGamesCount(): Flow<Int>

    // --- Connected Accounts ---
    @Query("SELECT * FROM connected_accounts")
    fun getConnectedAccounts(): Flow<List<ConnectedAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<ConnectedAccountEntity>)

    @Update
    suspend fun updateAccount(account: ConnectedAccountEntity)

    @Query("UPDATE connected_accounts SET isConnected = :connected, lastSyncTime = :syncTime WHERE platformId = :platformId")
    suspend fun updateConnectionStatus(platformId: String, connected: Boolean, syncTime: Long = System.currentTimeMillis())

    @Query("UPDATE connected_accounts SET autoClaimEnabled = :enabled WHERE platformId = :platformId")
    suspend fun updateAutoClaim(platformId: String, enabled: Boolean)

    // --- Claims ---
    @Query("SELECT * FROM claim_records ORDER BY claimedAt DESC")
    fun getAllClaims(): Flow<List<ClaimRecordEntity>>

    @Query("SELECT * FROM claim_records WHERE gameId = :gameId LIMIT 1")
    suspend fun getClaimByGameId(gameId: String): ClaimRecordEntity?

    @Query("SELECT * FROM claim_records WHERE gameId = :gameId LIMIT 1")
    fun getClaimFlowByGameId(gameId: String): Flow<ClaimRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClaim(claim: ClaimRecordEntity): Long

    @Query("SELECT COUNT(*) FROM claim_records WHERE status = 'CLAIMED'")
    fun getClaimedCount(): Flow<Int>

    @Query("SELECT SUM(valueSaved) FROM claim_records WHERE status = 'CLAIMED'")
    fun getTotalSaved(): Flow<Double?>

    // --- Automation Logs ---
    @Query("SELECT * FROM automation_logs ORDER BY timestamp DESC LIMIT 150")
    fun getRecentLogs(): Flow<List<AutomationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AutomationLogEntity)

    // --- Email Notifications ---
    @Query("SELECT * FROM email_notifications ORDER BY sentAt DESC LIMIT 100")
    fun getEmailNotifications(): Flow<List<EmailNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmail(email: EmailNotificationEntity)
}
