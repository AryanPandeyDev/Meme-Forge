package com.example.memeforge.design

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memeforge.utils.WindowSizeClass


data class ResponsiveDimensions(
    val contentPadding: Dp,
    val iconSize: Dp,
    val buttonHeight: Dp,
    val cardPadding: Dp,
    val sectionSpacing: Dp,
    val itemSpacing: Dp,
    val cornerRadius: Dp,
    val borderWidth: Dp,
    val cardElevation: Dp
)

fun getResponsiveDimensions(sizeClass: WindowSizeClass): ResponsiveDimensions {
    return when (sizeClass) {
        WindowSizeClass.COMPACT_SMALL -> ResponsiveDimensions(
            contentPadding = 20.dp,
            iconSize = 80.dp,
            buttonHeight = 44.dp,
            cardPadding = 16.dp,
            sectionSpacing = 20.dp,
            itemSpacing = 12.dp,
            cornerRadius = 12.dp,
            borderWidth = 1.5.dp,
            cardElevation = 6.dp
        )

        WindowSizeClass.COMPACT_MEDIUM -> ResponsiveDimensions(
            contentPadding = 24.dp,
            iconSize = 100.dp,
            buttonHeight = 50.dp,
            cardPadding = 22.dp,
            sectionSpacing = 24.dp,
            itemSpacing = 14.dp,
            cornerRadius = 22.dp,
            borderWidth = 1.5.dp,
            cardElevation = 7.dp
        )

        WindowSizeClass.COMPACT -> ResponsiveDimensions(
            contentPadding = 32.dp,
            iconSize = 10.dp,
            buttonHeight = 72.dp,
            cardPadding = 30.dp,
            sectionSpacing = 32.dp,
            itemSpacing = 24.dp,
            cornerRadius = 24.dp,
            borderWidth = 2.dp,
            cardElevation = 8.dp
        )

        WindowSizeClass.MEDIUM -> ResponsiveDimensions(
            contentPadding = 48.dp,
            iconSize = 140.dp,
            buttonHeight = 64.dp,
            cardPadding = 28.dp,
            sectionSpacing = 40.dp,
            itemSpacing = 20.dp,
            cornerRadius = 28.dp,
            borderWidth = 2.dp,
            cardElevation = 10.dp
        )

        WindowSizeClass.LARGE -> ResponsiveDimensions(
            contentPadding = 64.dp,
            iconSize = 160.dp,
            buttonHeight = 72.dp,
            cardPadding = 32.dp,
            sectionSpacing = 48.dp,
            itemSpacing = 30.dp,
            cornerRadius = 32.dp,
            borderWidth = 2.5.dp,
            cardElevation = 12.dp
        )
    }
}

