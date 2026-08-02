package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteFontEntity::class, RecentTextEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FontDatabase : RoomDatabase() {

    abstract fun fontDao(): FontDao

    companion object {
        @Volatile
        private var INSTANCE: FontDatabase? = null

        fun getInstance(context: Context): FontDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FontDatabase::class.java,
                    "fontnova_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
