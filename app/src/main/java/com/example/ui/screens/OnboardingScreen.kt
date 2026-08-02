package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemeColors

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val sampleText: String
)

@Composable
fun OnboardingScreen(
    theme: AppThemeColors,
    onFinishOnboarding: () -> Unit
) {
    val pages = remember {
        listOf(
            OnboardingPage(
                title = "Write Stylish Text",
                description = "Transform your plain text into 50+ unique Unicode styles instantly while typing.",
                icon = Icons.Default.TextFields,
                sampleText = "𝗙𝗼𝗻𝘁𝗡𝗼𝘃𝗮 𝘚𝘵𝘺𝘭𝘪𝘴𝘩 𝒯ℯ𝓍𝓉"
            ),
            OnboardingPage(
                title = "Beautiful Unicode Numbers",
                description = "Type stylish Unicode numbers (𝟭𝟮𝟯, 𝟜𝟝𝟞, ①②③) with zero latency in any input box.",
                icon = Icons.Default.Numbers,
                sampleText = "𝟬𝟭𝟮𝟯𝟰𝟱𝟲𝟳𝟴𝟵  ①②③④⑤"
            ),
            OnboardingPage(
                title = "Works in Most Apps",
                description = "Use FontNova directly in WhatsApp, Instagram, Messenger, Telegram, and TikTok.",
                icon = Icons.Default.Apps,
                sampleText = "꧁༺ Compatible Everywhere ༻꧂"
            )
        )
    }

    var currentPageIndex by remember { mutableIntStateOf(0) }
    val currentPage = pages[currentPageIndex]

    Scaffold(
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
            // Header Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (currentPageIndex < pages.size - 1) {
                    TextButton(onClick = onFinishOnboarding) {
                        Text("Skip", color = theme.primary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Main Content Slider
            AnimatedContent(
                targetState = currentPageIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboarding_slider"
            ) { index ->
                val page = pages[index]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(theme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = page.icon,
                            contentDescription = page.title,
                            tint = theme.primary,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = page.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = page.description,
                        fontSize = 15.sp,
                        color = theme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = theme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = page.sampleText,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Page Indicator Dots & Next / Get Started Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    pages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (index == currentPageIndex) 28.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentPageIndex) theme.primary else theme.primary.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentPageIndex < pages.size - 1) {
                            currentPageIndex++
                        } else {
                            onFinishOnboarding()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                ) {
                    Text(
                        text = if (currentPageIndex == pages.size - 1) "Get Started" else "Next",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
