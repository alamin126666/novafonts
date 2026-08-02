package com.example.keyboard

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fonts.FontEngine
import com.example.model.FontStyle

enum class KeyboardTab {
    QWERTY,
    SYMBOLS,
    EMOJI,
    CLIPBOARD
}

@Composable
fun FontNovaKeyboardView(
    activeFontId: String,
    onFontSelected: (String) -> Unit,
    onKeyTyped: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onSpace: () -> Unit,
    onCursorLeft: () -> Unit = {},
    onCursorRight: () -> Unit = {},
    vibrationEnabled: Boolean = true,
    keySoundEnabled: Boolean = true,
    popupPreviewEnabled: Boolean = true,
    themePrimaryColor: Color = Color(0xFF2563EB),
    themeBackgroundColor: Color = Color(0xFFF8FAFC),
    themeKeyColor: Color = Color(0xFFFFFFFF),
    themeTextColor: Color = Color(0xFF0F172A),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isShifted by remember { mutableStateOf(false) }
    var isCapsLock by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(KeyboardTab.QWERTY) }
    var pressedKeyPreview by remember { mutableStateOf<String?>(null) }

    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager }

    fun playFeedback() {
        if (vibrationEnabled && vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(15)
            }
        }
        if (keySoundEnabled && audioManager != null) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.4f)
        }
    }

    fun handleKeyPress(rawKey: String) {
        playFeedback()
        val textToCommit = if (isShifted || isCapsLock) rawKey.uppercase() else rawKey.lowercase()
        val transformed = FontEngine.transformText(textToCommit, activeFontId)
        onKeyTyped(transformed)

        if (isShifted && !isCapsLock) {
            isShifted = false
        }
    }

    val activeFont = remember(activeFontId) {
        FontEngine.ALL_FONTS.firstOrNull { it.id == activeFontId } ?: FontEngine.ALL_FONTS.first()
    }

    // Sample emojis for emoji drawer
    val emojis = listOf(
        "😊", "😂", "🥰", "😍", "✨", "🔥", "❤️", "👍", "🙏", "🎉", "😎", "🥳", "💯", "💖", "🌸",
        "⭐", "🙌", "👑", "🚀", "💡", "🎯", "🎵", "💬", "⚡", "🍀", "💎", "🏆", "🌟", "🤩", "😜",
        "🤪", "🤫", "🤔", "😌", "😴", "😇", "🥳", "🤠", "🤝", "💪", "🌈", "🦋", "🎨", "📱", "🎁"
    )

    // Sample clipboard history items
    var clipboardHistory by remember {
        mutableStateOf(
            listOf(
                "𝗙𝗼𝗻𝘁𝗡𝗼𝘃𝗮 𝟭𝟮𝟯",
                "★彡 FontNova 彡★",
                "𝔻𝕠𝕦𝕓𝕝𝕖 𝕊𝕥𝕣𝕚𝕜𝕖",
                "𝓈𝒸𝓇𝒾𝓅𝓉 𝓉ℯ𝓍𝓉"
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(themeBackgroundColor)
            .padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        // TOP TOOLBAR: Font Chips Carousel & Mode Selectors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Scrollable Font Selector Chips
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FontEngine.ALL_FONTS.take(16).forEach { font ->
                    val isSelected = font.id == activeFontId
                    Surface(
                        onClick = {
                            playFeedback()
                            onFontSelected(font.id)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) themePrimaryColor else themeKeyColor,
                        contentColor = if (isSelected) Color.White else themeTextColor,
                        shadowElevation = if (isSelected) 3.dp else 1.dp
                    ) {
                        Text(
                            text = font.sampleText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Action Quick Buttons
            IconButton(
                onClick = {
                    playFeedback()
                    currentTab = if (currentTab == KeyboardTab.EMOJI) KeyboardTab.QWERTY else KeyboardTab.EMOJI
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (currentTab == KeyboardTab.EMOJI) themePrimaryColor else themeKeyColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Mood,
                    contentDescription = "Emoji",
                    tint = if (currentTab == KeyboardTab.EMOJI) Color.White else themePrimaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = {
                    playFeedback()
                    currentTab = if (currentTab == KeyboardTab.CLIPBOARD) KeyboardTab.QWERTY else KeyboardTab.CLIPBOARD
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (currentTab == KeyboardTab.CLIPBOARD) themePrimaryColor else themeKeyColor)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Clipboard",
                    tint = if (currentTab == KeyboardTab.CLIPBOARD) Color.White else themePrimaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Popup key preview indicator
        if (popupPreviewEnabled && pressedKeyPreview != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = themePrimaryColor,
                    contentColor = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = pressedKeyPreview ?: "",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // KEYBOARD BODY CONTENT BY TAB
        when (currentTab) {
            KeyboardTab.QWERTY -> {
                val row1 = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
                val row2 = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
                val row3 = listOf("Z", "X", "C", "V", "B", "N", "M")

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        row1.forEach { key ->
                            val transformedKey = FontEngine.transformText(
                                if (isShifted || isCapsLock) key else key.lowercase(),
                                activeFontId
                            )
                            KeyButton(
                                label = transformedKey,
                                modifier = Modifier.weight(1f),
                                keyColor = themeKeyColor,
                                textColor = themeTextColor,
                                onPress = {
                                    if (popupPreviewEnabled) pressedKeyPreview = transformedKey
                                },
                                onRelease = {
                                    pressedKeyPreview = null
                                    handleKeyPress(key)
                                }
                            )
                        }
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(0.5f))
                        row2.forEach { key ->
                            val transformedKey = FontEngine.transformText(
                                if (isShifted || isCapsLock) key else key.lowercase(),
                                activeFontId
                            )
                            KeyButton(
                                label = transformedKey,
                                modifier = Modifier.weight(1f),
                                keyColor = themeKeyColor,
                                textColor = themeTextColor,
                                onPress = { if (popupPreviewEnabled) pressedKeyPreview = transformedKey },
                                onRelease = {
                                    pressedKeyPreview = null
                                    handleKeyPress(key)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.5f))
                    }

                    // Row 3: Shift + Z..M + Backspace
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Shift Key
                        SpecialKeyButton(
                            onClick = {
                                playFeedback()
                                if (isShifted) {
                                    isCapsLock = !isCapsLock
                                    isShifted = isCapsLock
                                } else {
                                    isShifted = true
                                }
                            },
                            modifier = Modifier.weight(1.5f),
                            keyColor = if (isShifted || isCapsLock) themePrimaryColor else Color(0xFFE2E8F0)
                        ) {
                            Icon(
                                imageVector = if (isCapsLock) Icons.Default.KeyboardCapslock else Icons.Default.ArrowUpward,
                                contentDescription = "Shift",
                                tint = if (isShifted || isCapsLock) Color.White else themeTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        row3.forEach { key ->
                            val transformedKey = FontEngine.transformText(
                                if (isShifted || isCapsLock) key else key.lowercase(),
                                activeFontId
                            )
                            KeyButton(
                                label = transformedKey,
                                modifier = Modifier.weight(1f),
                                keyColor = themeKeyColor,
                                textColor = themeTextColor,
                                onPress = { if (popupPreviewEnabled) pressedKeyPreview = transformedKey },
                                onRelease = {
                                    pressedKeyPreview = null
                                    handleKeyPress(key)
                                }
                            )
                        }

                        // Backspace Key
                        SpecialKeyButton(
                            onClick = {
                                playFeedback()
                                onBackspace()
                            },
                            modifier = Modifier.weight(1.5f),
                            keyColor = Color(0xFFCBD5E1)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Backspace",
                                tint = themeTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Bottom Row: Symbols Mode, Cursor Controls, Spacebar, Enter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Switch to Symbols
                        SpecialKeyButton(
                            onClick = {
                                playFeedback()
                                currentTab = KeyboardTab.SYMBOLS
                            },
                            modifier = Modifier.weight(1.3f),
                            keyColor = Color(0xFFE2E8F0)
                        ) {
                            Text("123", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = themeTextColor)
                        }

                        // Left Arrow Cursor
                        SpecialKeyButton(
                            onClick = {
                                playFeedback()
                                onCursorLeft()
                            },
                            modifier = Modifier.weight(1f),
                            keyColor = Color(0xFFF1F5F9)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Left", tint = themeTextColor, modifier = Modifier.size(18.dp))
                        }

                        // Right Arrow Cursor
                        SpecialKeyButton(
                            onClick = {
                                playFeedback()
                                onCursorRight()
                            },
                            modifier = Modifier.weight(1f),
                            keyColor = Color(0xFFF1F5F9)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Right", tint = themeTextColor, modifier = Modifier.size(18.dp))
                        }

                        // Spacebar
                        SpecialKeyButton(
                            onClick = {
                                playFeedback()
                                onSpace()
                            },
                            modifier = Modifier.weight(4f),
                            keyColor = themeKeyColor
                        ) {
                            Text(
                                text = activeFont.name,
                                fontSize = 12.sp,
                                color = themeTextColor.copy(alpha = 0.7f)
                            )
                        }

                        // Enter Key
                        SpecialKeyButton(
                            onClick = {
                                playFeedback()
                                onEnter()
                            },
                            modifier = Modifier.weight(1.5f),
                            keyColor = themePrimaryColor
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                                contentDescription = "Enter",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            KeyboardTab.SYMBOLS -> {
                val symbolRows = listOf(
                    listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
                    listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/"),
                    listOf("*", "\"", "'", ":", ";", "!", "?", ",", ".", "~")
                )

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    symbolRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            row.forEach { sym ->
                                val transformedSym = FontEngine.transformText(sym, activeFontId)
                                KeyButton(
                                    label = transformedSym,
                                    modifier = Modifier.weight(1f),
                                    keyColor = themeKeyColor,
                                    textColor = themeTextColor,
                                    onPress = {},
                                    onRelease = {
                                        playFeedback()
                                        onKeyTyped(transformedSym)
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SpecialKeyButton(
                            onClick = {
                                playFeedback()
                                currentTab = KeyboardTab.QWERTY
                            },
                            modifier = Modifier.weight(1.5f),
                            keyColor = themePrimaryColor
                        ) {
                            Text("ABC", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        SpecialKeyButton(
                            onClick = {
                                playFeedback()
                                onSpace()
                            },
                            modifier = Modifier.weight(5f),
                            keyColor = themeKeyColor
                        ) {
                            Text("Space", fontSize = 12.sp, color = themeTextColor)
                        }

                        SpecialKeyButton(
                            onClick = {
                                playFeedback()
                                onBackspace()
                            },
                            modifier = Modifier.weight(1.5f),
                            keyColor = Color(0xFFCBD5E1)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Backspace", tint = themeTextColor)
                        }
                    }
                }
            }

            KeyboardTab.EMOJI -> {
                Column(modifier = Modifier.height(180.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Emojis", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = themeTextColor)
                        TextButton(onClick = { currentTab = KeyboardTab.QWERTY }) {
                            Text("Back to ABC", color = themePrimaryColor)
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(8),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(emojis) { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(themeKeyColor)
                                    .clickable {
                                        playFeedback()
                                        onKeyTyped(emoji)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            KeyboardTab.CLIPBOARD -> {
                Column(modifier = Modifier.height(180.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Clipboard History", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = themeTextColor)
                        TextButton(onClick = { currentTab = KeyboardTab.QWERTY }) {
                            Text("Back to ABC", color = themePrimaryColor)
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        clipboardHistory.forEach { clip ->
                            Surface(
                                onClick = {
                                    playFeedback()
                                    onKeyTyped(clip)
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = themeKeyColor,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(clip, fontSize = 15.sp, color = themeTextColor)
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Paste",
                                        tint = themePrimaryColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyButton(
    label: String,
    modifier: Modifier = Modifier,
    keyColor: Color,
    textColor: Color,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .shadow(1.5.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(keyColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPress()
                        tryAwaitRelease()
                        onRelease()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SpecialKeyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    keyColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .shadow(1.5.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(keyColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
