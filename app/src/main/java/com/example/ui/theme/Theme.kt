package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BabylonGold,
    onPrimary = IraqiNavyDeep,
    primaryContainer = BabylonGoldDark,
    onPrimaryContainer = BabylonGoldLight,
    secondary = SkyAzure,
    onSecondary = IraqiNavyDeep,
    secondaryContainer = IraqiNavyCardLight,
    onSecondaryContainer = SkyAzure,
    tertiary = EmeraldIraqi,
    onTertiary = Color.White,
    background = IraqiNavyDeep,
    onBackground = TextPrimaryDark,
    surface = IraqiNavyCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = IraqiNavyCardLight,
    onSurfaceVariant = TextSecondaryDark,
    outline = IraqiNavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = IraqiPrimaryNavy,
    onPrimary = Color.White,
    primaryContainer = IraqiNavyCardLight,
    onPrimaryContainer = BabylonGoldLight,
    secondary = IraqiSecondaryGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = EmeraldIraqi,
    onTertiary = Color.White,
    background = IraqiSurfaceLight,
    onBackground = TextPrimaryLight,
    surface = IraqiCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = IraqiBorderLight
)

@Composable
fun IraqiTranslatorTheme(
    darkTheme: Boolean = true, // Default to stunning dark theme for Iraqi Translator
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
