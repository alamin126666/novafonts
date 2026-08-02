package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.FontDatabase
import com.example.fonts.FontEngine
import com.example.model.FontCategory
import com.example.model.FontStyle
import com.example.repository.FontRepository
import com.example.utils.PreferencesManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MainUiState(
    val selectedCategory: FontCategory = FontCategory.ALL,
    val searchQuery: String = "",
    val activeFontId: String = "bold_sans",
    val inputText: String = "Hello 123",
    val activeThemeName: String = "Sleek Light",
    val keyboardHeight: Float = 260f,
    val keySoundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val fontSizeScale: Float = 1.0f,
    val autoCapitalization: Boolean = true,
    val popupKeyPreview: Boolean = true,
    val onboardingCompleted: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FontDatabase.getInstance(application)
    private val repository = FontRepository(db.fontDao())
    val preferencesManager = PreferencesManager(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val favoriteFonts = repository.favoriteFonts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentTexts = repository.recentTexts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            preferencesManager.activeTheme.collect { theme ->
                _uiState.update { it.copy(activeThemeName = theme) }
            }
        }
        viewModelScope.launch {
            preferencesManager.activeFontId.collect { fontId ->
                _uiState.update { it.copy(activeFontId = fontId) }
            }
        }
        viewModelScope.launch {
            preferencesManager.keyboardHeight.collect { h ->
                _uiState.update { it.copy(keyboardHeight = h) }
            }
        }
        viewModelScope.launch {
            preferencesManager.keySoundEnabled.collect { sound ->
                _uiState.update { it.copy(keySoundEnabled = sound) }
            }
        }
        viewModelScope.launch {
            preferencesManager.vibrationEnabled.collect { vib ->
                _uiState.update { it.copy(vibrationEnabled = vib) }
            }
        }
        viewModelScope.launch {
            preferencesManager.fontSizeScale.collect { scale ->
                _uiState.update { it.copy(fontSizeScale = scale) }
            }
        }
        viewModelScope.launch {
            preferencesManager.autoCapitalization.collect { autoCap ->
                _uiState.update { it.copy(autoCapitalization = autoCap) }
            }
        }
        viewModelScope.launch {
            preferencesManager.popupKeyPreview.collect { popup ->
                _uiState.update { it.copy(popupKeyPreview = popup) }
            }
        }
        viewModelScope.launch {
            preferencesManager.onboardingCompleted.collect { comp ->
                _uiState.update { it.copy(onboardingCompleted = comp) }
            }
        }
    }

    fun setCategory(category: FontCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun setActiveFont(fontId: String) {
        _uiState.update { it.copy(activeFontId = fontId) }
        viewModelScope.launch {
            preferencesManager.setActiveFontId(fontId)
        }
    }

    fun toggleFavorite(fontId: String, fontName: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(fontId, fontName, isFavorite)
        }
    }

    fun saveRecentText(originalText: String, transformedText: String, fontName: String) {
        viewModelScope.launch {
            repository.addRecentText(originalText, transformedText, fontName)
        }
    }

    fun deleteRecentText(id: Int) {
        viewModelScope.launch {
            repository.deleteRecentText(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun setTheme(themeName: String) {
        viewModelScope.launch {
            preferencesManager.setActiveTheme(themeName)
        }
    }

    fun setKeyboardHeight(height: Float) {
        viewModelScope.launch {
            preferencesManager.setKeyboardHeight(height)
        }
    }

    fun setKeySoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setKeySoundEnabled(enabled)
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setVibrationEnabled(enabled)
        }
    }

    fun setFontSizeScale(scale: Float) {
        viewModelScope.launch {
            preferencesManager.setFontSizeScale(scale)
        }
    }

    fun setAutoCapitalization(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setAutoCapitalization(enabled)
        }
    }

    fun setPopupKeyPreview(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setPopupKeyPreview(enabled)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.setOnboardingCompleted(true)
        }
    }
}
