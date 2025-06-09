package com.example.memeforge.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.memeforge.utils.WindowSizeClass
import com.example.memeforge.utils.getWindowSizeClass


val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    secondaryContainer = DarkInputBackground,
    onSecondaryContainer = DarkTextSecondary,
    outline = DarkDivider
)

val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    background = Background,
    surface = Surface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    secondaryContainer = InputBackground,
    onSecondaryContainer = TextSecondary,
    outline = Divider
)


@Composable
fun MemeForgeTheme(
    darkTheme: Boolean,
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

    val sizeClass = getWindowSizeClass()
    val dynamicTypography = when (sizeClass) {
        WindowSizeClass.COMPACT_SMALL -> getCompactSmallTypography()
        WindowSizeClass.COMPACT_MEDIUM -> getCompactMediumTypography()
        WindowSizeClass.COMPACT -> getCompactTypography()
        WindowSizeClass.MEDIUM -> getMediumTypography()
        WindowSizeClass.LARGE -> getLargeTypography()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = dynamicTypography,
        content = content
    )
}