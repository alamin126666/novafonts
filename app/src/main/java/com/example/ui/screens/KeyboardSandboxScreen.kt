package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.FontNovaKeyboardView
import com.example.ui.theme.AppThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardSandboxScreen(
    theme: AppThemeColors,
    activeFontId: String,
    onSelectActiveFont: (String) -> Unit,
    vibrationEnabled: Boolean,
    keySoundEnabled: Boolean,
    popupKeyPreview: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var typedDocument by remember { mutableStateOf("𝗙𝗼𝗻𝘁𝗡𝗼𝘃𝗮 𝘒𝘦𝘺𝘣𝘰𝘢𝘳𝘥 𝘚𝘢𝘯𝘥𝘣𝘰𝘹 ") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keyboard Typing Sandbox", fontWeight = FontWeight.Bold, color = theme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = theme.onBackground)
                    }
                },
                actions = {
                    IconButton(onClick = { typedDocument = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = theme.onBackground)
                    }
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(typedDocument))
                            Toast.makeText(context, "Copied sandbox text!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = theme.primary)
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
        ) {
            // Typing Canvas Area
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "LIVE TYPING CANVAS:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (typedDocument.isEmpty()) "Tap keys below to start typing stylish text..." else typedDocument,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (typedDocument.isEmpty()) theme.onSurface.copy(alpha = 0.4f) else theme.onSurface,
                        lineHeight = 28.sp
                    )
                }
            }

            // Keyboard Container
            FontNovaKeyboardView(
                activeFontId = activeFontId,
                onFontSelected = onSelectActiveFont,
                onKeyTyped = { text ->
                    typedDocument += text
                },
                onBackspace = {
                    if (typedDocument.isNotEmpty()) {
                        typedDocument = typedDocument.dropLast(1)
                    }
                },
                onEnter = {
                    typedDocument += "\n"
                },
                onSpace = {
                    typedDocument += " "
                },
                vibrationEnabled = vibrationEnabled,
                keySoundEnabled = keySoundEnabled,
                popupPreviewEnabled = popupKeyPreview,
                themePrimaryColor = theme.primary,
                themeBackgroundColor = theme.surface,
                themeKeyColor = theme.background,
                themeTextColor = theme.onSurface
            )
        }
    }
}
