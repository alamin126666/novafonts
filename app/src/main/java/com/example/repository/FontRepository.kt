package com.example.repository

import com.example.database.FavoriteFontEntity
import com.example.database.FontDao
import com.example.database.RecentTextEntity
import kotlinx.coroutines.flow.Flow

class FontRepository(private val fontDao: FontDao) {

    val favoriteFonts: Flow<List<FavoriteFontEntity>> = fontDao.getAllFavoriteFonts()
    val recentTexts: Flow<List<RecentTextEntity>> = fontDao.getRecentTexts()

    suspend fun toggleFavorite(fontId: String, fontName: String, isCurrentlyFavorite: Boolean) {
        if (isCurrentlyFavorite) {
            fontDao.deleteFavoriteFont(fontId)
        } else {
            fontDao.insertFavoriteFont(FavoriteFontEntity(fontId = fontId, fontName = fontName))
        }
    }

    suspend fun addRecentText(originalText: String, transformedText: String, fontName: String) {
        if (originalText.isNotBlank()) {
            fontDao.insertRecentText(
                RecentTextEntity(
                    originalText = originalText,
                    transformedText = transformedText,
                    fontName = fontName
                )
            )
        }
    }

    suspend fun deleteRecentText(id: Int) {
        fontDao.deleteRecentText(id)
    }

    suspend fun clearHistory() {
        fontDao.clearAllRecentTexts()
    }
}
