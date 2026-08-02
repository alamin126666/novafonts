package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.FavoriteFontEntity
import com.example.fonts.FontEngine
import com.example.ui.theme.AppThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    theme: AppThemeColors,
    favoriteFonts: List<FavoriteFontEntity>,
    inputText: String,
    onToggleFavorite: (String, String, Boolean) -> Unit,
    onSelectActiveFont: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorite Fonts", fontWeight = FontWeight.Bold, color = theme.onBackground) },
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
        if (favoriteFonts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "No Favorites",
                        tint = theme.primary.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No Favorite Fonts Yet", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = theme.onBackground)
                    Text("Tap the star icon on any font to save it here", fontSize = 14.sp, color = theme.onBackground.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteFonts, key = { it.fontId }) { fav ->
                    val fontStyle = FontEngine.ALL_FONTS.firstOrNull { it.id == fav.fontId }
                    if (fontStyle != null) {
                        val sample = if (inputText.isBlank()) fontStyle.transform(fontStyle.sampleText)
                        else fontStyle.transform(inputText)

                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = theme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(fontStyle.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = theme.onSurface)
                                    IconButton(
                                        onClick = { onToggleFavorite(fav.fontId, fav.fontName, true) }
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = "Remove", tint = androidx.compose.ui.graphics.Color(0xFFEAB308))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(sample, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = theme.primary)

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            onSelectActiveFont(fontStyle.id)
                                            Toast.makeText(context, "Activated ${fontStyle.name}", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                                    ) {
                                        Text("Use Font")
                                    }

                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(sample))
                                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier
                                            .background(theme.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp))
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = theme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
