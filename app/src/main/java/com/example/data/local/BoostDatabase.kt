package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BoostOrderEntity::class], version = 1, exportSchema = false)
abstract class BoostDatabase : RoomDatabase() {
    abstract fun boostOrderDao(): BoostOrderDao

    companion object {
        @Volatile
        private var INSTANCE: BoostDatabase? = null

        fun getInstance(context: Context): BoostDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BoostDatabase::class.java,
                    "boostke_orders.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
