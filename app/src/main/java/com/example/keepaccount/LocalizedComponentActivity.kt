package com.example.keepaccount

import android.content.Context
import androidx.activity.ComponentActivity

open class LocalizedComponentActivity : ComponentActivity() {
    private var languageCodeWhenCreated: String? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLocaleManager.wrap(newBase))
    }

    override fun onResume() {
        super.onResume()
        val currentCode = AppLocaleManager.currentLanguageCode(this)
        if (languageCodeWhenCreated == null) {
            languageCodeWhenCreated = currentCode
        } else if (languageCodeWhenCreated != currentCode) {
            languageCodeWhenCreated = currentCode
            recreate()
        }
    }
}
