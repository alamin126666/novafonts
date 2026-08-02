package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_fonts")
data class FavoriteFontEntity(
    @PrimaryKey val fontId: String,
    val fontName: String,
    val timestamp: Long = System.currentTimeMillis()
)
