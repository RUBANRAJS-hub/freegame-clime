package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AutomationLogEntity
import com.example.data.model.ClaimRecordEntity
import com.example.data.model.ConnectedAccountEntity
import com.example.data.model.EmailNotificationEntity
import com.example.data.model.GameEntity

@Database(
    entities = [
        GameEntity::class,
        ConnectedAccountEntity::class,
        ClaimRecordEntity::class,
        AutomationLogEntity::class,
        EmailNotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GameClaimDatabase : RoomDatabase() {
    abstract fun gameClaimDao(): GameClaimDao

    companion object {
        @Volatile
        private var INSTANCE: GameClaimDatabase? = null

        fun getInstance(context: Context): GameClaimDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameClaimDatabase::class.java,
                    "gameclaim_ai.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
