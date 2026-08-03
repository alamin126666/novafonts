package com.example.keyboard

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardCapslock
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fonts.FontEngine
import kotlinx.coroutines.delay

// ─── Theme ────────────────────────────────────────────────────────────────────
private object KbTheme {
    val bg        = Color(0xFF0A0C16)
    val keyBg     = Color(0xFF1B1F30)
    val fnKeyBg   = Color(0xFF10121E)
    val accent    = Color(0xFF6366F1)
    val accentAlt = Color(0xFF8B5CF6)
    val keyText   = Color(0xFFE8EAF6)
    val subText   = Color(0xFF64748B)
    val pressed   = Color(0xFF2D3148)
    val chipSel   = Color(0xFF6366F1)
    val chipUnsel = Color(0xFF1B1F30)
}

enum class KeyboardTab { QWERTY, SYMBOLS, EMOJI, CLIPBOARD }

// ─── Main Composable ──────────────────────────────────────────────────────────
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
    // Legacy params kept for API compatibility (not used in new dark theme)
    vibrationEnabled: Boolean = true,
    keySoundEnabled: Boolean = true,
    popupPreviewEnabled: Boolean = false,
    themePrimaryColor: Color = KbTheme.accent,
    themeBackgroundColor: Color = KbTheme.bg,
    themeKeyColor: Color = KbTheme.keyBg,
    themeTextColor: Color = KbTheme.keyText,
    modifier: Modifier = Modifier
) {
    val context      = LocalContext.current
    val vibrator     = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager }

    var isShifted           by remember { mutableStateOf(false) }
    var isCapsLock          by remember { mutableStateOf(false) }
    var currentTab          by remember { mutableStateOf(KeyboardTab.QWERTY) }
    var isPressingBackspace by remember { mutableStateOf(false) }

    // ── Long-press backspace: 380ms initial → 45ms repeat ────────────────────
    LaunchedEffect(isPressingBackspace) {
        if (isPressingBackspace) {
            delay(380L)
            while (isPressingBackspace) {
                onBackspace()
                delay(45L)
            }
        }
    }

    @Suppress("DEPRECATION")
    fun vibrateShort() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator?.vibrate(12)
        }
    }

    fun playFeedback() {
        vibrateShort()
        audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.35f)
    }

    fun handleKey(raw: String) {
        playFeedback()
        val char = if (isShifted || isCapsLock) raw.uppercase() else raw.lowercase()
        onKeyTyped(FontEngine.transformText(char, activeFontId))
        if (isShifted && !isCapsLock) isShifted = false
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(KbTheme.bg)
            .padding(horizontal = 5.dp, vertical = 5.dp)
    ) {
        // ── TOP: Font chip carousel + Tab icons ───────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FontEngine.ALL_FONTS.take(18).forEach { font ->
                    val sel = font.id == activeFontId
                    val chipBg by animateColorAsState(
                        targetValue = if (sel) KbTheme.chipSel else KbTheme.chipUnsel,
                        animationSpec = tween(durationMillis = 150),
                        label = "chip"
                    )
                    Surface(
                        onClick = { playFeedback(); onFontSelected(font.id) },
                        shape = RoundedCornerShape(10.dp),
                        color = chipBg,
                        shadowElevation = if (sel) 4.dp else 1.dp,
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = font.sampleText,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            fontSize = 12.sp,
                            color = if (sel) Color.White else KbTheme.subText,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            TabIconBtn(icon = Icons.Default.Mood, active = currentTab == KeyboardTab.EMOJI) {
                playFeedback()
                currentTab = if (currentTab == KeyboardTab.EMOJI) KeyboardTab.QWERTY else KeyboardTab.EMOJI
            }
            TabIconBtn(icon = Icons.Default.ContentPaste, active = currentTab == KeyboardTab.CLIPBOARD) {
                playFeedback()
                currentTab = if (currentTab == KeyboardTab.CLIPBOARD) KeyboardTab.QWERTY else KeyboardTab.CLIPBOARD
            }
        }

        // ── KEYBOARD BODY ─────────────────────────────────────────────────────
        when (currentTab) {

            // ── QWERTY ────────────────────────────────────────────────────────
            KeyboardTab.QWERTY -> {
                val r1 = listOf("Q","W","E","R","T","Y","U","I","O","P")
                val r2 = listOf("A","S","D","F","G","H","J","K","L")
                val r3 = listOf("Z","X","C","V","B","N","M")

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        r1.forEach { k ->
                            LetterKey(
                                label = FontEngine.transformText(if (isShifted || isCapsLock) k else k.lowercase(), activeFontId),
                                modifier = Modifier.weight(1f),
                                onClick = { handleKey(k) }
                            )
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Spacer(Modifier.weight(0.5f))
                        r2.forEach { k ->
                            LetterKey(
                                label = FontEngine.transformText(if (isShifted || isCapsLock) k else k.lowercase(), activeFontId),
                                modifier = Modifier.weight(1f),
                                onClick = { handleKey(k) }
                            )
                        }
                        Spacer(Modifier.weight(0.5f))
                    }
                    // Row 3: Shift + letters + Backspace
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FnKey(
                            modifier = Modifier.weight(1.5f),
                            bgColor = if (isShifted || isCapsLock) KbTheme.accent else KbTheme.fnKeyBg,
                            onClick = {
                                playFeedback()
                                if (isShifted) { isCapsLock = !isCapsLock; isShifted = isCapsLock }
                                else isShifted = true
                            }
                        ) {
                            Icon(
                                imageVector = if (isCapsLock) Icons.Default.KeyboardCapslock else Icons.Default.ArrowUpward,
                                contentDescription = "Shift",
                                tint = if (isShifted || isCapsLock) Color.White else KbTheme.subText,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        r3.forEach { k ->
                            LetterKey(
                                label = FontEngine.transformText(if (isShifted || isCapsLock) k else k.lowercase(), activeFontId),
                                modifier = Modifier.weight(1f),
                                onClick = { handleKey(k) }
                            )
                        }
                        BackspaceKey(
                            modifier = Modifier.weight(1.5f),
                            isLongPressing = isPressingBackspace,
                            onPointerDown = { isPressingBackspace = true; playFeedback(); onBackspace() },
                            onPointerUp = { isPressingBackspace = false }
                        )
                    }
                    // Bottom row
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FnKey(Modifier.weight(1.4f), KbTheme.fnKeyBg, { playFeedback(); currentTab = KeyboardTab.SYMBOLS }) {
                            Icon(Icons.Default.Apps, "123", tint = KbTheme.subText, modifier = Modifier.size(20.dp))
                        }
                        FnKey(Modifier.weight(1f), KbTheme.fnKeyBg, { playFeedback(); onCursorLeft() }) {
                            Icon(Icons.Default.KeyboardArrowLeft, "Left", tint = KbTheme.subText, modifier = Modifier.size(22.dp))
                        }
                        FnKey(Modifier.weight(4f), KbTheme.keyBg, { playFeedback(); onSpace() }) {
                            Box(
                                Modifier
                                    .fillMaxWidth(0.45f)
                                    .height(2.dp)
                                    .background(KbTheme.subText.copy(alpha = 0.4f), RoundedCornerShape(1.dp))
                            )
                        }
                        FnKey(Modifier.weight(1f), KbTheme.fnKeyBg, { playFeedback(); onCursorRight() }) {
                            Icon(Icons.Default.KeyboardArrowRight, "Right", tint = KbTheme.subText, modifier = Modifier.size(22.dp))
                        }
                        FnKey(Modifier.weight(1.4f), KbTheme.accent, { playFeedback(); onEnter() }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardReturn, "Enter", tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }

            // ── SYMBOLS ───────────────────────────────────────────────────────
            KeyboardTab.SYMBOLS -> {
                val symRows = listOf(
                    listOf("1","2","3","4","5","6","7","8","9","0"),
                    listOf("@","#","$","%","&","-","+","(",")","/"),
                    listOf("*","\"","'",":",";","!","?",",",".","~")
                )
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    symRows.forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            row.forEach { sym ->
                                LetterKey(label = sym, modifier = Modifier.weight(1f)) {
                                    playFeedback(); onKeyTyped(sym)
                                }
                            }
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FnKey(Modifier.weight(1.5f), KbTheme.accent, { playFeedback(); currentTab = KeyboardTab.QWERTY }) {
                            Icon(Icons.Default.KeyboardArrowUp, "ABC", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        FnKey(Modifier.weight(1f), KbTheme.fnKeyBg, { playFeedback(); onCursorLeft() }) {
                            Icon(Icons.Default.KeyboardArrowLeft, "Left", tint = KbTheme.subText, modifier = Modifier.size(22.dp))
                        }
                        FnKey(Modifier.weight(3.5f), KbTheme.keyBg, { playFeedback(); onSpace() }) {
                            Box(
                                Modifier
                                    .fillMaxWidth(0.4f)
                                    .height(2.dp)
                                    .background(KbTheme.subText.copy(alpha = 0.4f), RoundedCornerShape(1.dp))
                            )
                        }
                        FnKey(Modifier.weight(1f), KbTheme.fnKeyBg, { playFeedback(); onEnter() }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardReturn, "Enter", tint = KbTheme.accent, modifier = Modifier.size(22.dp))
                        }
                        BackspaceKey(
                            modifier = Modifier.weight(1.5f),
                            isLongPressing = isPressingBackspace,
                            onPointerDown = { isPressingBackspace = true; playFeedback(); onBackspace() },
                            onPointerUp = { isPressingBackspace = false }
                        )
                    }
                }
            }

            // ── EMOJI ─────────────────────────────────────────────────────────
            KeyboardTab.EMOJI -> {
                val emojis = listOf(
                    "😊","😂","🥰","😍","✨","🔥","❤️","👍","🙏","🎉",
                    "😎","🥳","💯","💖","🌸","⭐","🙌","👑","🚀","💡",
                    "🎯","🎵","⚡","🍀","💎","🏆","🌟","🤩","😜","🤔",
                    "😌","😇","🤝","💪","🌈","🦋","🎨","📱","🎁","🫶",
                    "🥹","😤","🫠","🤯","🥺"
                )
                Column(modifier = Modifier.height(190.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Mood, null, tint = KbTheme.accent, modifier = Modifier.size(20.dp))
                        IconButton(
                            onClick = { currentTab = KeyboardTab.QWERTY },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(KbTheme.fnKeyBg)
                        ) {
                            Icon(Icons.Default.Apps, "Back", tint = KbTheme.subText, modifier = Modifier.size(17.dp))
                        }
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(9),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(emojis) { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(KbTheme.keyBg)
                                    .pointerInput(Unit) {
                                        awaitEachGesture {
                                            awaitFirstDown(requireUnconsumed = false)
                                            waitForUpOrCancellation()
                                            onKeyTyped(emoji)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 19.sp)
                            }
                        }
                    }
                }
            }

            // ── CLIPBOARD ─────────────────────────────────────────────────────
            KeyboardTab.CLIPBOARD -> {
                val clips = remember {
                    listOf("𝗙𝗼𝗻𝘁𝗡𝗼𝘃𝗮 𝟭𝟮𝟯", "★彡 FontNova 彡★", "𝔻𝕠𝕦𝕓𝕝𝕖 𝕊𝕥𝕣𝕚𝕜𝕖", "𝓈𝒸𝓇𝒾𝓅𝓉 𝓉ℯ𝓍𝓉")
                }
                Column(modifier = Modifier.height(190.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ContentPaste, null, tint = KbTheme.accent, modifier = Modifier.size(20.dp))
                        IconButton(
                            onClick = { currentTab = KeyboardTab.QWERTY },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(KbTheme.fnKeyBg)
                        ) {
                            Icon(Icons.Default.Apps, "Back", tint = KbTheme.subText, modifier = Modifier.size(17.dp))
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        clips.forEach { clip ->
                            Surface(
                                onClick = { playFeedback(); onKeyTyped(clip) },
                                shape = RoundedCornerShape(10.dp),
                                color = KbTheme.keyBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(clip, fontSize = 14.sp, color = KbTheme.keyText, modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ContentCopy, null, tint = KbTheme.accent, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Key Components ───────────────────────────────────────────────────────────

@Composable
private fun LetterKey(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.91f else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "lscale"
    )
    val bg by animateColorAsState(
        targetValue = if (pressed) KbTheme.pressed else KbTheme.keyBg,
        animationSpec = tween(durationMillis = 80),
        label = "lbg"
    )
    Box(
        modifier = modifier
            .height(46.dp)
            .scale(scale)
            .shadow(2.dp, RoundedCornerShape(10.dp), clip = false)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    pressed = true
                    waitForUpOrCancellation()
                    pressed = false
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = KbTheme.keyText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FnKey(
    modifier: Modifier = Modifier,
    bgColor: Color = KbTheme.fnKeyBg,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.91f else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "fscale"
    )
    Box(
        modifier = modifier
            .height(46.dp)
            .scale(scale)
            .shadow(2.dp, RoundedCornerShape(10.dp), clip = false)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    pressed = true
                    waitForUpOrCancellation()
                    pressed = false
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun BackspaceKey(
    modifier: Modifier = Modifier,
    isLongPressing: Boolean,
    onPointerDown: () -> Unit,
    onPointerUp: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (isLongPressing) KbTheme.accentAlt else KbTheme.fnKeyBg,
        animationSpec = tween(durationMillis = 150),
        label = "bsbg"
    )
    Box(
        modifier = modifier
            .height(46.dp)
            .shadow(2.dp, RoundedCornerShape(10.dp), clip = false)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    onPointerDown()
                    waitForUpOrCancellation()
                    onPointerUp()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "Backspace",
            tint = if (isLongPressing) Color.White else KbTheme.subText,
            modifier = Modifier.size(21.dp)
        )
    }
}

@Composable
private fun TabIconBtn(
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (active) KbTheme.accent else KbTheme.fnKeyBg,
        animationSpec = tween(durationMillis = 150),
        label = "tabBg"
    )
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(bg)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    waitForUpOrCancellation()
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (active) Color.White else KbTheme.subText,
            modifier = Modifier.size(18.dp)
        )
    }
}
