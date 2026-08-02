package com.example.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.FontNovaThemes
import com.example.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object EnableKeyboard : Screen("enable_keyboard")
    object Home : Screen("home", "Fonts", Icons.Default.TextFields)
    object ThemeStore : Screen("theme_store", "Themes", Icons.Default.Palette)
    object History : Screen("history", "Clipboard", Icons.Default.History)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Star)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Sandbox : Screen("sandbox")
}

val BOTTOM_NAV_SCREENS = listOf(
    Screen.Home,
    Screen.ThemeStore,
    Screen.History,
    Screen.Favorites,
    Screen.Settings
)

@Composable
fun FontNovaAppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteFonts by viewModel.favoriteFonts.collectAsStateWithLifecycle()
    val recentTexts by viewModel.recentTexts.collectAsStateWithLifecycle()

    val currentTheme = FontNovaThemes.getThemeByName(uiState.activeThemeName)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in BOTTOM_NAV_SCREENS.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    color = currentTheme.background,
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp
                ) {
                    NavigationBar(
                        containerColor = currentTheme.surface,
                        tonalElevation = 0.dp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        BOTTOM_NAV_SCREENS.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon!!,
                                        contentDescription = screen.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = currentTheme.primary,
                                    selectedTextColor = currentTheme.primary,
                                    indicatorColor = currentTheme.primary.copy(alpha = 0.15f),
                                    unselectedIconColor = currentTheme.onSurface.copy(alpha = 0.6f),
                                    unselectedTextColor = currentTheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = currentTheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    theme = currentTheme,
                    onNavigateNext = {
                        if (uiState.onboardingCompleted) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    theme = currentTheme,
                    onFinishOnboarding = {
                        viewModel.completeOnboarding()
                        navController.navigate(Screen.EnableKeyboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.EnableKeyboard.route) {
                EnableKeyboardScreen(
                    theme = currentTheme,
                    onContinueToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.EnableKeyboard.route) { inclusive = true }
                        }
                    },
                    onOpenSandbox = {
                        navController.navigate(Screen.Sandbox.route)
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    theme = currentTheme,
                    inputText = uiState.inputText,
                    onInputTextChange = { viewModel.setInputText(it) },
                    activeFontId = uiState.activeFontId,
                    onSelectActiveFont = { viewModel.setActiveFont(it) },
                    selectedCategory = uiState.selectedCategory,
                    onSelectCategory = { viewModel.setCategory(it) },
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    favoriteFonts = favoriteFonts,
                    onToggleFavorite = { fontId, fontName, isFav ->
                        viewModel.toggleFavorite(fontId, fontName, isFav)
                    },
                    onSaveRecentText = { orig, trans, fontName ->
                        viewModel.saveRecentText(orig, trans, fontName)
                    },
                    onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToThemeStore = { navController.navigate(Screen.ThemeStore.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToSandbox = { navController.navigate(Screen.Sandbox.route) },
                    onNavigateToEnable = { navController.navigate(Screen.EnableKeyboard.route) }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    theme = currentTheme,
                    favoriteFonts = favoriteFonts,
                    inputText = uiState.inputText,
                    onToggleFavorite = { fontId, fontName, isFav ->
                        viewModel.toggleFavorite(fontId, fontName, isFav)
                    },
                    onSelectActiveFont = { viewModel.setActiveFont(it) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    theme = currentTheme,
                    recentTexts = recentTexts,
                    onDeleteRecent = { viewModel.deleteRecentText(it) },
                    onClearAll = { viewModel.clearHistory() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ThemeStore.route) {
                ThemeStoreScreen(
                    currentTheme = currentTheme,
                    onSelectTheme = { viewModel.setTheme(it) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    theme = currentTheme,
                    keyboardHeight = uiState.keyboardHeight,
                    onKeyboardHeightChange = { viewModel.setKeyboardHeight(it) },
                    keySoundEnabled = uiState.keySoundEnabled,
                    onKeySoundChange = { viewModel.setKeySoundEnabled(it) },
                    vibrationEnabled = uiState.vibrationEnabled,
                    onVibrationChange = { viewModel.setVibrationEnabled(it) },
                    fontSizeScale = uiState.fontSizeScale,
                    onFontSizeScaleChange = { viewModel.setFontSizeScale(it) },
                    autoCapitalization = uiState.autoCapitalization,
                    onAutoCapitalizationChange = { viewModel.setAutoCapitalization(it) },
                    popupKeyPreview = uiState.popupKeyPreview,
                    onPopupKeyPreviewChange = { viewModel.setPopupKeyPreview(it) },
                    onNavigateToThemeStore = { navController.navigate(Screen.ThemeStore.route) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Sandbox.route) {
                KeyboardSandboxScreen(
                    theme = currentTheme,
                    activeFontId = uiState.activeFontId,
                    onSelectActiveFont = { viewModel.setActiveFont(it) },
                    vibrationEnabled = uiState.vibrationEnabled,
                    keySoundEnabled = uiState.keySoundEnabled,
                    popupKeyPreview = uiState.popupKeyPreview,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

