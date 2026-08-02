package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemeColors
import com.example.ui.theme.FontNovaThemes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeStoreScreen(
    currentTheme: AppThemeColors,
    onSelectTheme: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme Store", fontWeight = FontWeight.Bold, color = currentTheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = currentTheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = currentTheme.background)
            )
        },
        containerColor = currentTheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Select Keyboard Theme",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = currentTheme.onBackground
            )
            Text(
                text = "Customize the look and feel of FontNova Keyboard",
                fontSize = 14.sp,
                color = currentTheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(FontNovaThemes.ALL_THEMES, key = { it.name }) { themePreset ->
                    val isSelected = themePreset.name == currentTheme.name

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = themePreset.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) themePreset.primary else currentTheme.onSurface.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable {
                                onSelectTheme(themePreset.name)
                                Toast
                                    .makeText(context, "Applied ${themePreset.name}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = themePreset.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = themePreset.onSurface
                                    )

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Active",
                                            tint = themePreset.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                // Theme Colors Preview Bar
                                Column {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = themePreset.background,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "𝗙𝗼𝗻𝘁𝗡𝗼𝘃𝗮 𝟭𝟮𝟯",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = themePreset.primary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(themePreset.primary)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(themePreset.secondary)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(themePreset.accent)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(themePreset.background)
                                                .border(1.dp, themePreset.onBackground.copy(alpha = 0.2f), CircleShape)
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
}
