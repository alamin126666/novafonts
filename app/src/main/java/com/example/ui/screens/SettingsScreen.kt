package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    theme: AppThemeColors,
    keyboardHeight: Float,
    onKeyboardHeightChange: (Float) -> Unit,
    keySoundEnabled: Boolean,
    onKeySoundChange: (Boolean) -> Unit,
    vibrationEnabled: Boolean,
    onVibrationChange: (Boolean) -> Unit,
    fontSizeScale: Float,
    onFontSizeScaleChange: (Float) -> Unit,
    autoCapitalization: Boolean,
    onAutoCapitalizationChange: (Boolean) -> Unit,
    popupKeyPreview: Boolean,
    onPopupKeyPreviewChange: (Boolean) -> Unit,
    onNavigateToThemeStore: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keyboard Settings", fontWeight = FontWeight.Bold, color = theme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = theme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.background)
            )
        },
        containerColor = theme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SECTION: KEYBOARD PREFERENCES
            Text("Typing & Display", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = theme.primary)

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = theme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Key Sound Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = theme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Key Sound", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = theme.onSurface)
                                Text("Audio feedback on key press", fontSize = 12.sp, color = theme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                        Switch(
                            checked = keySoundEnabled,
                            onCheckedChange = onKeySoundChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = theme.primary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = theme.onSurface.copy(alpha = 0.1f))

                    // Vibration Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = theme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Vibration (Haptics)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = theme.onSurface)
                                Text("Vibrate device when tapping keys", fontSize = 12.sp, color = theme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = onVibrationChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = theme.primary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = theme.onSurface.copy(alpha = 0.1f))

                    // Popup Key Preview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, tint = theme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Popup Key Preview", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = theme.onSurface)
                                Text("Show character popups while typing", fontSize = 12.sp, color = theme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                        Switch(
                            checked = popupKeyPreview,
                            onCheckedChange = onPopupKeyPreviewChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = theme.primary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = theme.onSurface.copy(alpha = 0.1f))

                    // Auto Capitalization
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Title, contentDescription = null, tint = theme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Auto Capitalization", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = theme.onSurface)
                                Text("Capitalize start of sentences", fontSize = 12.sp, color = theme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                        Switch(
                            checked = autoCapitalization,
                            onCheckedChange = onAutoCapitalizationChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = theme.primary)
                        )
                    }
                }
            }

            // SECTION: DIMENSIONS & SCALING
            Text("Layout & Size", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = theme.primary)

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = theme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Keyboard Height: ${keyboardHeight.toInt()} dp", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = theme.onSurface)
                    Slider(
                        value = keyboardHeight,
                        onValueChange = onKeyboardHeightChange,
                        valueRange = 200f..340f,
                        colors = SliderDefaults.colors(thumbColor = theme.primary, activeTrackColor = theme.primary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Font Size Scale: ${(fontSizeScale * 100).toInt()}%", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = theme.onSurface)
                    Slider(
                        value = fontSizeScale,
                        onValueChange = onFontSizeScaleChange,
                        valueRange = 0.8f..1.3f,
                        colors = SliderDefaults.colors(thumbColor = theme.primary, activeTrackColor = theme.primary)
                    )
                }
            }

            // SECTION: THEME & BACKUP
            Text("Themes & Data", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = theme.primary)

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = theme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = theme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Keyboard Theme", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = theme.onSurface)
                        }
                        TextButton(onClick = onNavigateToThemeStore) {
                            Text(theme.name, fontWeight = FontWeight.Bold, color = theme.primary)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = theme.onSurface.copy(alpha = 0.1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { Toast.makeText(context, "Settings & Favorites backed up!", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Backup")
                        }

                        OutlinedButton(
                            onClick = { Toast.makeText(context, "Favorites & Settings restored!", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore")
                        }
                    }
                }
            }
        }
    }
}
