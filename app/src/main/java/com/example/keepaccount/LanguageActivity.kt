package com.example.keepaccount

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.keepaccount.ui.BrandGreen
import com.example.keepaccount.ui.MutedText
import com.example.keepaccount.ui.SetStatusBarColor
import com.example.keepaccount.ui.SoftGreen
import com.example.keepaccount.ui.theme.KeepAccountTheme

class LanguageActivity : LocalizedComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KeepAccountTheme {
                LanguageActivityContent(onFinish = ::finish)
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, LanguageActivity::class.java)
    }
}

@Composable
private fun LanguageActivityContent(onFinish: () -> Unit) {
    SetStatusBarColor(Color.White)
    BackHandler(onBack = onFinish)
    val context = LocalContext.current
    var selectedCode by remember { mutableStateOf(AppLocaleManager.currentLanguageCode(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "‹",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .clickable(onClick = onFinish)
                    .padding(end = 16.dp),
            )
            Text(
                text = stringResource(R.string.language_title),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(40.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        AppLocaleManager.supportedLanguages.forEach { language ->
            LanguageRow(
                language = language,
                selected = selectedCode == language.code,
                onClick = {
                    selectedCode = language.code
                    AppLocaleManager.saveLanguage(context, language.code)
                    context.findActivity()?.recreate()
                },
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.language_no_restart_needed),
            color = MutedText,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun LanguageRow(
    language: AppLanguage,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) SoftGreen else Color(0xFFF7F7F7))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(language.labelRes),
            modifier = Modifier.weight(1f),
            color = if (selected) BrandGreen else Color(0xFF202124),
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
        if (selected) {
            Text(
                text = "✓",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(BrandGreen),
            )
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
