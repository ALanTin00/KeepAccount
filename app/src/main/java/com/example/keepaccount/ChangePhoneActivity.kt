package com.example.keepaccount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.keepaccount.ui.ChangePhoneActivityContent
import com.example.keepaccount.ui.theme.KeepAccountTheme

class ChangePhoneActivity : LocalizedComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KeepAccountTheme {
                ChangePhoneActivityContent(onFinish = ::finish)
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, ChangePhoneActivity::class.java)
    }
}
