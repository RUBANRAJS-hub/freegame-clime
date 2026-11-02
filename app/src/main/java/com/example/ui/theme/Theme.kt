package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkGamingColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberBg,
    primaryContainer = DeepViolet,
    onPrimaryContainer = TextPrimary,
    secondary = ElectricPurple,
    onSecondary = TextPrimary,
    secondaryContainer = CyberCardBg,
    onSecondaryContainer = NeonCyan,
    tertiary = ClaimGreen,
    onTertiary = CyberBg,
    background = CyberBg,
    onBackground = TextPrimary,
    surface = CyberBgSecondary,
    onSurface = TextPrimary,
    surfaceVariant = CyberCardBg,
    onSurfaceVariant = TextSecondary,
    outline = CyberCardBorder,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to Dark Gaming Mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkGamingColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = CyberBg.toArgb()
                window.navigationBarColor = CyberBg.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
