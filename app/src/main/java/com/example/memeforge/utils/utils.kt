package com.example.memeforge.utils


import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSizeClass {
    COMPACT_SMALL,    // < 480dp
    COMPACT_MEDIUM,   // 480dp - 599dp
    COMPACT,          // 600dp - 839dp
    MEDIUM,           // 840dp - 1199dp
    LARGE             // >= 1200dp
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun getWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp

    return when {
        width < 360.dp -> WindowSizeClass.COMPACT_SMALL
        width < 480.dp -> WindowSizeClass.COMPACT_MEDIUM
        width < 600.dp -> WindowSizeClass.COMPACT
        width < 840.dp -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.LARGE
    }
}
