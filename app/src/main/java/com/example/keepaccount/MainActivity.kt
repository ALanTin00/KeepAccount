package com.example.keepaccount

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.keepaccount.ui.LedgerApp
import com.example.keepaccount.ui.theme.KeepAccountTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KeepAccountTheme {
                LedgerApp()
            }
        }
    }
}
