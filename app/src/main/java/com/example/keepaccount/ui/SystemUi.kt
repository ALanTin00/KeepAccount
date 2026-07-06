package com.example.keepaccount.ui

import android.app.Activity
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
        val previousStatusBarColor = window.statusBarColor
        val controller = WindowCompat.getInsetsController(window, view)
        val previousLightStatusBars = controller.isAppearanceLightStatusBars

        window.statusBarColor = color.toArgb()
        controller.isAppearanceLightStatusBars = color.luminance() > 0.5f

        onDispose {
            window.statusBarColor = previousStatusBarColor
            controller.isAppearanceLightStatusBars = previousLightStatusBars
        }
    }
}
