package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnableKeyboardScreen(
    theme: AppThemeColors,
    onContinueToHome: () -> Unit,
    onOpenSandbox: () -> Unit
) {
    val context = LocalContext.current
    var isEnabledStepCompleted by remember { mutableStateOf(false) }
    var isSelectedStepCompleted by remember { mutableStateOf(false) }

    val imm = remember { context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager }

    fun checkKeyboardStatus() {
        imm?.let { manager ->
            val enabledList = manager.enabledInputMethodList
            val isEnabled = enabledList.any { it.packageName == context.packageName }
            isEnabledStepCompleted = isEnabled
        }
    }

    LaunchedEffect(Unit) {
        checkKeyboardStatus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keyboard Setup", fontWeight = FontWeight.Bold, color = theme.onBackground) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.background)
            )
        },
        containerColor = theme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(theme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Keyboard",
                        tint = theme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Enable FontNova Keyboard",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Follow these 2 simple steps to use stylish fonts in all your favorite apps.",
                    fontSize = 14.sp,
                    color = theme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // STEP 1 CARD: Enable Keyboard
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = theme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isEnabledStepCompleted) Color(0xFF22C55E) else theme.primary
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isEnabledStepCompleted) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = Color.White)
                            } else {
                                Text("1", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "1. Enable Keyboard",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = theme.onSurface
                            )
                            Text(
                                text = "Turn on FontNova in system settings",
                                fontSize = 13.sp,
                                color = theme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Button(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                                checkKeyboardStatus()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                        ) {
                            Text("Enable")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // STEP 2 CARD: Select Keyboard
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = theme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelectedStepCompleted) Color(0xFF22C55E) else theme.secondary
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelectedStepCompleted) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = Color.White)
                            } else {
                                Text("2", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "2. Select Keyboard",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = theme.onSurface
                            )
                            Text(
                                text = "Set FontNova as active input method",
                                fontSize = 13.sp,
                                color = theme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Button(
                            onClick = {
                                imm?.showInputMethodPicker()
                                isSelectedStepCompleted = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = theme.secondary)
                        ) {
                            Text("Select")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sandbox Test Button
                OutlinedButton(
                    onClick = onOpenSandbox,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.TouchApp, contentDescription = "Sandbox", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Try Interactive Keyboard Sandbox")
                }
            }

            // Bottom Continue Button
            Button(
                onClick = onContinueToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
            ) {
                Text("Done - Open Font Nova", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
