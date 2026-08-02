package com.example.model

enum class FontCategory(val displayName: String) {
    ALL("All"),
    CLASSIC("Classic"),
    BOLD("Bold"),
    ITALIC("Italic"),
    SANS("Sans"),
    MONOSPACE("Monospace"),
    DOUBLE_STRIKE("Double Strike"),
    BUBBLE("Bubble"),
    SQUARE("Square"),
    TINY("Tiny"),
    FANCY("Fancy"),
    SYMBOLS("Symbols"),
    NUMBERS("Numbers")
}

data class FontStyle(
    val id: String,
    val name: String,
    val category: FontCategory,
    val sampleText: String = "FontNova 123",
    val isFavorite: Boolean = false,
    val transform: (String) -> String
)
