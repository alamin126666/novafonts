package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.database.FavoriteFontEntity
import com.example.fonts.FontEngine
import com.example.model.FontCategory
import com.example.model.FontStyle
import com.example.ui.theme.AppThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    theme: AppThemeColors,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    activeFontId: String,
    onSelectActiveFont: (String) -> Unit,
    selectedCategory: FontCategory,
    onSelectCategory: (FontCategory) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    favoriteFonts: List<FavoriteFontEntity>,
    onToggleFavorite: (String, String, Boolean) -> Unit,
    onSaveRecentText: (String, String, String) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToThemeStore: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSandbox: () -> Unit,
    onNavigateToEnable: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showShareMenu by remember { mutableStateOf(false) }

    val activeFont = remember(activeFontId) {
        FontEngine.ALL_FONTS.firstOrNull { it.id == activeFontId } ?: FontEngine.ALL_FONTS.first()
    }

    val livePreviewText = remember(inputText, activeFontId) {
        if (inputText.isBlank()) "Type something above..."
        else FontEngine.transformText(inputText, activeFontId)
    }

    val displayedFonts = remember(selectedCategory, searchQuery) {
        var filtered = FontEngine.getFontsByCategory(selectedCategory)
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.displayName.contains(searchQuery, ignoreCase = true) ||
                it.sampleText.contains(searchQuery, ignoreCase = true)
            }
        }
        filtered
    }

    fun copyToClipboard(text: String, fontName: String) {
        clipboardManager.setText(AnnotatedString(text))
        onSaveRecentText(inputText.ifBlank { "Sample" }, text, fontName)
        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun shareText(text: String, packageName: String? = null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            if (packageName != null) {
                setPackage(packageName)
            }
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Share via"))
        } catch (e: Exception) {
            Toast.makeText(context, "App not installed", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(theme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = "FontNova",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "FontNova",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = theme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSandbox,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(Icons.Default.Keyboard, contentDescription = "Test Keyboard", tint = theme.primary)
                    }
                    IconButton(
                        onClick = onNavigateToThemeStore,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = "Themes", tint = theme.onBackground.copy(alpha = 0.7f))
                    }
                    IconButton(
                        onClick = onNavigateToFavorites,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "Favorites", tint = theme.onBackground.copy(alpha = 0.7f))
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = theme.onBackground.copy(alpha = 0.7f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.background)
            )
        },
        containerColor = theme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Enable Keyboard Banner
            item {
                Surface(
                    onClick = onNavigateToEnable,
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(theme.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TouchApp, contentDescription = "Setup", tint = theme.primary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Enable FontNova Keyboard", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = theme.onSurface)
                                Text("Tap to activate in Android settings", fontSize = 12.sp, color = theme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = theme.primary)
                    }
                }
            }

            // SLEEK PREVIEW INPUT SECTION CARD
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PREVIEW INPUT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary,
                                letterSpacing = 1.5.sp
                            )
                            AssistChip(
                                onClick = { },
                                label = { Text(activeFont.name, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = theme.primary.copy(alpha = 0.12f),
                                    labelColor = theme.primary
                                ),
                                shape = RoundedCornerShape(50)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Input Box
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = onInputTextChange,
                            placeholder = { Text("Type something here...", color = theme.onSurface.copy(alpha = 0.4f)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = theme.primary.copy(alpha = 0.5f),
                                unfocusedBorderColor = Color(0xFFF1F5F9),
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            ),
                            trailingIcon = {
                                if (inputText.isNotEmpty()) {
                                    IconButton(onClick = { onInputTextChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = theme.onSurface.copy(alpha = 0.5f))
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        Spacer(modifier = Modifier.height(16.dp))

                        // Live Result Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Live Result", fontSize = 12.sp, color = theme.onSurface.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = livePreviewText,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // One-Tap Copy Action Box
                            Surface(
                                onClick = { copyToClipboard(livePreviewText, activeFont.name) },
                                shape = RoundedCornerShape(16.dp),
                                color = theme.primary.copy(alpha = 0.12f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = theme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        // Share Drawer
                        AnimatedVisibility(visible = showShareMenu) {
                            Column(modifier = Modifier.padding(top = 16.dp)) {
                                Text("Share directly to:", fontSize = 12.sp, color = theme.onSurface.copy(alpha = 0.6f))
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    ShareIconButton("WhatsApp", Color(0xFF25D366)) { shareText(livePreviewText, "com.whatsapp") }
                                    ShareIconButton("Telegram", Color(0xFF0088CC)) { shareText(livePreviewText, "org.telegram.messenger") }
                                    ShareIconButton("Facebook", Color(0xFF1877F2)) { shareText(livePreviewText, "com.facebook.katana") }
                                    ShareIconButton("Messenger", Color(0xFF0084FF)) { shareText(livePreviewText, "com.facebook.orca") }
                                    ShareIconButton("Instagram", Color(0xFFE4405F)) { shareText(livePreviewText, "com.instagram.android") }
                                }
                            }
                        }
                    }
                }
            }

            // SEARCH BAR
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search 50+ unicode fonts...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = theme.onSurface.copy(alpha = 0.5f)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.primary,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            // CATEGORIES ROW (SLEEK PILL CHIPS)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FontCategory.entries.forEach { category ->
                        val isSelected = category == selectedCategory
                        Surface(
                            onClick = { onSelectCategory(category) },
                            shape = CircleShape,
                            color = if (isSelected) theme.primary else Color.White,
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = category.displayName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // SLEEK FONT CARDS LIST
            items(displayedFonts, key = { it.id }) { font ->
                val isFav = favoriteFonts.any { it.fontId == font.id }
                val isCurrentActive = font.id == activeFontId
                val transformedSample = remember(inputText, font.id) {
                    if (inputText.isBlank()) font.transform(font.sampleText)
                    else font.transform(inputText)
                }

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(18.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = font.name.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (isFav) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Favorite",
                                        tint = Color(0xFFEAB308),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = transformedSample,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { onToggleFavorite(font.id, font.name, isFav) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF8FAFC))
                            ) {
                                Icon(
                                    imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFav) Color(0xFFEAB308) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { copyToClipboard(transformedSample, font.name) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(theme.primary.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = theme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Button(
                                onClick = { onSelectActiveFont(font.id) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCurrentActive) theme.primary else Color(0xFF0F172A),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (isCurrentActive) "ACTIVE" else "USE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareIconButton(label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color)
        ) {
            Icon(Icons.Default.Send, contentDescription = label, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp)
    }
}
