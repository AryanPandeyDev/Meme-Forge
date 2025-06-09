package com.example.memeforge.colordiscription

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color

data class ColorDescription(
    val color : Color,
    var selected : MutableState<Boolean>
)
