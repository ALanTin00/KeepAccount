package com.example.keepaccount.ui

import android.app.Activity
import android.os.Build
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
internal fun SetStatusBarColor(color: Color) {
    val context = LocalContext.current
    val view = LocalView.current
    if (view.isInEditMode) {
        return
    }

    val activity = context as? Activity ?: return
    DisposableEffect(activity, view, color) {
        val window = activity.window
        val previousStatusBarColor = window.setLegacyStatusBarColor(color.toArgb())
        val controller = WindowCompat.getInsetsController(window, view)
        val previousLightStatusBars = controller.isAppearanceLightStatusBars

        controller.isAppearanceLightStatusBars = color.luminance() > 0.5f

        onDispose {
            window.restoreLegacyStatusBarColor(previousStatusBarColor)
            controller.isAppearanceLightStatusBars = previousLightStatusBars
        }
    }
}

@Suppress("DEPRECATION")
private fun Window.setLegacyStatusBarColor(color: Int): Int? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) return null
    return statusBarColor.also { statusBarColor = color }
}

@Suppress("DEPRECATION")
private fun Window.restoreLegacyStatusBarColor(previousColor: Int?) {
    if (previousColor != null) {
        statusBarColor = previousColor
    }
}
