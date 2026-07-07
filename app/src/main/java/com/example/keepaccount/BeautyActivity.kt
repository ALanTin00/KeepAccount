package com.example.keepaccount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.keepaccount.ui.BeautyActivityContent
import com.example.keepaccount.ui.theme.KeepAccountTheme

class BeautyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KeepAccountTheme {
                BeautyActivityContent(onFinish = ::finish)
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, BeautyActivity::class.java)
    }
}
