package com.example.keepaccount.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keepaccount.R

@Composable
internal fun SettingsPage(
    onOpenBeautyPage: () -> Unit,
    onOpenLanguagePage: () -> Unit,
    onOpenChangePhonePage: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.tab_settings),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF17191C),
        )
        Spacer(modifier = Modifier.height(28.dp))
        SettingsSection(title = stringResource(R.string.settings_section_personalize)) {
            SettingsListItem(
                icon = SettingsIconType.Beauty,
                title = stringResource(R.string.beauty_title),
                subtitle = stringResource(R.string.settings_beauty_subtitle),
                onClick = onOpenBeautyPage,
            )
            SettingsDivider()
            SettingsListItem(
                icon = SettingsIconType.Language,
                title = stringResource(R.string.language_title),
                subtitle = stringResource(R.string.settings_language_subtitle),
                onClick = onOpenLanguagePage,
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        SettingsSection(title = stringResource(R.string.settings_section_transfer)) {
            SettingsListItem(
                icon = SettingsIconType.Transfer,
                title = stringResource(R.string.change_phone_title),
                subtitle = stringResource(R.string.settings_change_phone_subtitle),
                onClick = onOpenChangePhonePage,
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = MutedText,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White),
            content = content,
        )
    }
}

@Composable
private fun SettingsListItem(
    icon: SettingsIconType,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsIconBadge(icon = icon)
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                color = Color(0xFF1F2328),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Text(
            text = "\u203a",
            color = Color(0xFFB6BDC6),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 74.dp)
            .height(1.dp)
            .background(Divider),
    )
}

@Composable
private fun SettingsIconBadge(icon: SettingsIconType) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(SoftGreen),
        contentAlignment = Alignment.Center,
    ) {
        if (icon == SettingsIconType.Language) {
            Text(
                text = "Aa",
                color = BrandGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        } else {
            Canvas(modifier = Modifier.size(24.dp)) {
                when (icon) {
                    SettingsIconType.Beauty -> {
                        drawCircle(BrandGreen, radius = size.minDimension * 0.18f, center = Offset(size.width * 0.35f, size.height * 0.36f))
                        drawCircle(Color(0xFF7DD8A4), radius = size.minDimension * 0.18f, center = Offset(size.width * 0.62f, size.height * 0.38f))
                        drawCircle(Color(0xFF58C7D6), radius = size.minDimension * 0.18f, center = Offset(size.width * 0.47f, size.height * 0.66f))
                    }
                    SettingsIconType.Transfer -> {
                        drawRoundRect(BrandGreen, topLeft = Offset(size.width * 0.28f, size.height * 0.12f), size = Size(size.width * 0.44f, size.height * 0.76f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx(), 5.dp.toPx()), style = Stroke(width = 2.2.dp.toPx()))
                        drawLine(BrandGreen, Offset(size.width * 0.42f, size.height * 0.76f), Offset(size.width * 0.58f, size.height * 0.76f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
                        drawLine(Color(0xFF58C7D6), Offset(size.width * 0.06f, size.height * 0.42f), Offset(size.width * 0.22f, size.height * 0.42f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
                        drawLine(Color(0xFF58C7D6), Offset(size.width * 0.16f, size.height * 0.34f), Offset(size.width * 0.22f, size.height * 0.42f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
                        drawLine(Color(0xFF58C7D6), Offset(size.width * 0.16f, size.height * 0.50f), Offset(size.width * 0.22f, size.height * 0.42f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
                        drawLine(Color(0xFF58C7D6), Offset(size.width * 0.78f, size.height * 0.58f), Offset(size.width * 0.94f, size.height * 0.58f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
                        drawLine(Color(0xFF58C7D6), Offset(size.width * 0.84f, size.height * 0.50f), Offset(size.width * 0.78f, size.height * 0.58f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
                        drawLine(Color(0xFF58C7D6), Offset(size.width * 0.84f, size.height * 0.66f), Offset(size.width * 0.78f, size.height * 0.58f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
                    }
                    SettingsIconType.Language -> Unit
                }
            }
        }
    }
}

private enum class SettingsIconType {
    Beauty,
    Language,
    Transfer,
}
