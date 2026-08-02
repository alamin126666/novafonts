package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_texts")
data class RecentTextEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalText: String,
    val transformedText: String,
    val fontName: String,
    val timestamp: Long = System.currentTimeMillis()
)
