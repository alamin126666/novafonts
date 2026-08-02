package com.example.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "fontnova_settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val ACTIVE_THEME = stringPreferencesKey("active_theme")
        val ACTIVE_FONT_ID = stringPreferencesKey("active_font_id")
        val KEYBOARD_HEIGHT = floatPreferencesKey("keyboard_height")
        val KEY_SOUND_ENABLED = booleanPreferencesKey("key_sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val FONT_SIZE_SCALE = floatPreferencesKey("font_size_scale")
        val AUTO_CAPITALIZATION = booleanPreferencesKey("auto_capitalization")
        val POPUP_KEY_PREVIEW = booleanPreferencesKey("popup_key_preview")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val activeTheme: Flow<String> = context.dataStore.data.map { it[ACTIVE_THEME] ?: "White & Blue" }
    val activeFontId: Flow<String> = context.dataStore.data.map { it[ACTIVE_FONT_ID] ?: "bold_sans" }
    val keyboardHeight: Flow<Float> = context.dataStore.data.map { it[KEYBOARD_HEIGHT] ?: 260f }
    val keySoundEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SOUND_ENABLED] ?: true }
    val vibrationEnabled: Flow<Boolean> = context.dataStore.data.map { it[VIBRATION_ENABLED] ?: true }
    val fontSizeScale: Flow<Float> = context.dataStore.data.map { it[FONT_SIZE_SCALE] ?: 1.0f }
    val autoCapitalization: Flow<Boolean> = context.dataStore.data.map { it[AUTO_CAPITALIZATION] ?: true }
    val popupKeyPreview: Flow<Boolean> = context.dataStore.data.map { it[POPUP_KEY_PREVIEW] ?: true }
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }

    suspend fun setActiveTheme(theme: String) {
        context.dataStore.edit { it[ACTIVE_THEME] = theme }
    }

    suspend fun setActiveFontId(fontId: String) {
        context.dataStore.edit { it[ACTIVE_FONT_ID] = fontId }
    }

    suspend fun setKeyboardHeight(height: Float) {
        context.dataStore.edit { it[KEYBOARD_HEIGHT] = height }
    }

    suspend fun setKeySoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SOUND_ENABLED] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[VIBRATION_ENABLED] = enabled }
    }

    suspend fun setFontSizeScale(scale: Float) {
        context.dataStore.edit { it[FONT_SIZE_SCALE] = scale }
    }

    suspend fun setAutoCapitalization(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_CAPITALIZATION] = enabled }
    }

    suspend fun setPopupKeyPreview(enabled: Boolean) {
        context.dataStore.edit { it[POPUP_KEY_PREVIEW] = enabled }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }
}
