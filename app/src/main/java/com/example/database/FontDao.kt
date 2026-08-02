package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FontDao {

    @Query("SELECT * FROM favorite_fonts ORDER BY timestamp DESC")
    fun getAllFavoriteFonts(): Flow<List<FavoriteFontEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteFont(font: FavoriteFontEntity)

    @Query("DELETE FROM favorite_fonts WHERE fontId = :fontId")
    suspend fun deleteFavoriteFont(fontId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_fonts WHERE fontId = :fontId)")
    fun isFavorite(fontId: String): Flow<Boolean>

    @Query("SELECT * FROM recent_texts ORDER BY timestamp DESC LIMIT 50")
    fun getRecentTexts(): Flow<List<RecentTextEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentText(recentText: RecentTextEntity)

    @Query("DELETE FROM recent_texts WHERE id = :id")
    suspend fun deleteRecentText(id: Int)

    @Query("DELETE FROM recent_texts")
    suspend fun clearAllRecentTexts()
}
