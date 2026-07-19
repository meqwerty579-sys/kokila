package com.example.ui.designsystem

import androidx.compose.runtime.mutableStateOf

object UiThemeManager {
    var darkTheme = mutableStateOf(true)
    var amoledMode = mutableStateOf(false)
    var dynamicColor = mutableStateOf(false)
}
