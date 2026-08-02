package com.example.ui.theme

import androidx.compose.ui.graphics.Color

data class AppThemeColors(
    val name: String,
    val primary: Color,
    val secondary: Color,
    val accent: Color = Color(0xFF06B6D4),
    val background: Color,
    val surface: Color,
    val onPrimary: Color = Color.White,
    val onBackground: Color,
    val onSurface: Color
)

object FontNovaThemes {
    val SLEEK_LIGHT = AppThemeColors(
        name = "Sleek Light",
        primary = Color(0xFF2563EB),
        secondary = Color(0xFF3B82F6),
        accent = Color(0xFF06B6D4),
        background = Color(0xFFF8FAFC),
        surface = Color(0xFFFFFFFF),
        onBackground = Color(0xFF0F172A),
        onSurface = Color(0xFF1E293B)
    )

    val DARK_ELEGANCE = AppThemeColors(
        name = "Dark Elegance",
        primary = Color(0xFF38BDF8),
        secondary = Color(0xFF0284C7),
        accent = Color(0xFF06B6D4),
        background = Color(0xFF0F172A),
        surface = Color(0xFF1E293B),
        onBackground = Color(0xFFF8FAFC),
        onSurface = Color(0xFFF1F5F9)
    )

    val AMOLED_BLACK = AppThemeColors(
        name = "AMOLED Black",
        primary = Color(0xFF2563EB),
        secondary = Color(0xFF7C3AED),
        accent = Color(0xFF06B6D4),
        background = Color(0xFF000000),
        surface = Color(0xFF121212),
        onBackground = Color(0xFFFFFFFF),
        onSurface = Color(0xFFE2E8F0)
    )

    val BLUE_NEON = AppThemeColors(
        name = "Blue Neon",
        primary = Color(0xFF06B6D4),
        secondary = Color(0xFF2563EB),
        accent = Color(0xFF38BDF8),
        background = Color(0xFF0B132B),
        surface = Color(0xFF1C2541),
        onBackground = Color(0xFFE0F2FE),
        onSurface = Color(0xFFF0F9FF)
    )

    val PURPLE_NEON = AppThemeColors(
        name = "Purple Neon",
        primary = Color(0xFF7C3AED),
        secondary = Color(0xFFA855F7),
        accent = Color(0xFF06B6D4),
        background = Color(0xFF100B2B),
        surface = Color(0xFF1D153B),
        onBackground = Color(0xFFF3E8FF),
        onSurface = Color(0xFFFAF5FF)
    )

    val GREEN_NEON = AppThemeColors(
        name = "Green Neon",
        primary = Color(0xFF22C55E),
        secondary = Color(0xFF10B981),
        accent = Color(0xFF06B6D4),
        background = Color(0xFF061A14),
        surface = Color(0xFF0F2E23),
        onBackground = Color(0xFFDCFCE7),
        onSurface = Color(0xFFF0FDF4)
    )

    val CRIMSON_RED = AppThemeColors(
        name = "Crimson Red",
        primary = Color(0xFFEF4444),
        secondary = Color(0xFFF87171),
        accent = Color(0xFFF59E0B),
        background = Color(0xFF1A0B0C),
        surface = Color(0xFF2D1214),
        onBackground = Color(0xFFFEE2E2),
        onSurface = Color(0xFFFEF2F2)
    )

    val ALL_THEMES = listOf(
        SLEEK_LIGHT,
        DARK_ELEGANCE,
        AMOLED_BLACK,
        BLUE_NEON,
        PURPLE_NEON,
        GREEN_NEON,
        CRIMSON_RED
    )

    fun getThemeByName(name: String): AppThemeColors {
        return ALL_THEMES.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: ALL_THEMES.firstOrNull { it.name.contains(name, ignoreCase = true) }
            ?: SLEEK_LIGHT
    }
}

