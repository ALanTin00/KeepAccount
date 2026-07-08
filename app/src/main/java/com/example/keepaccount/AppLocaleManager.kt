package com.example.keepaccount

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object AppLocaleManager {
    private const val PREFS_NAME = "keep_account_settings"
    private const val KEY_APP_LANGUAGE = "app_language"

    const val DEFAULT_LANGUAGE_CODE = "en"

    val supportedLanguages = listOf(
        AppLanguage(code = "zh-Hans", localeTag = "zh-CN", labelRes = R.string.language_simplified_chinese),
        AppLanguage(code = "zh-Hant", localeTag = "zh-TW", labelRes = R.string.language_traditional_chinese),
        AppLanguage(code = "en", localeTag = "en", labelRes = R.string.language_english),
        AppLanguage(code = "ja", localeTag = "ja", labelRes = R.string.language_japanese),
        AppLanguage(code = "ko", localeTag = "ko", labelRes = R.string.language_korean),
    )

    fun currentLanguageCode(context: Context): String {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = preferences.getString(KEY_APP_LANGUAGE, null)
        val savedCode = supportedLanguages.firstOrNull { it.code == saved }?.code
        if (savedCode != null) return savedCode

        val detectedCode = detectSystemLanguageCode(context.resources.configuration)
        preferences.edit()
            .putString(KEY_APP_LANGUAGE, detectedCode)
            .apply()
        return detectedCode
    }

    fun saveLanguage(context: Context, code: String) {
        val normalized = supportedLanguages.firstOrNull { it.code == code }?.code ?: DEFAULT_LANGUAGE_CODE
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_APP_LANGUAGE, normalized)
            .apply()
    }

    fun wrap(context: Context): Context {
        val language = supportedLanguages.firstOrNull { it.code == currentLanguageCode(context) }
            ?: supportedLanguages.first { it.code == DEFAULT_LANGUAGE_CODE }
        val locale = Locale.forLanguageTag(language.localeTag)
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun detectSystemLanguageCode(configuration: Configuration): String {
        val locales = configuration.locales
        for (index in 0 until locales.size()) {
            matchSystemLocale(locales[index])?.let { return it }
        }
        return DEFAULT_LANGUAGE_CODE
    }

    private fun matchSystemLocale(locale: Locale): String? =
        when (locale.language.lowercase(Locale.ROOT)) {
            "zh" -> if (isTraditionalChinese(locale)) "zh-Hant" else "zh-Hans"
            "en" -> "en"
            "ja" -> "ja"
            "ko" -> "ko"
            else -> null
        }

    private fun isTraditionalChinese(locale: Locale): Boolean {
        val script = locale.script
        val country = locale.country
        return script.equals("Hant", ignoreCase = true) ||
            country.equals("TW", ignoreCase = true) ||
            country.equals("HK", ignoreCase = true) ||
            country.equals("MO", ignoreCase = true)
    }
}

data class AppLanguage(
    val code: String,
    val localeTag: String,
    val labelRes: Int,
)
