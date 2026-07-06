package com.example.keepaccount.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.keepaccount.R
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.DefaultCategories
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun EmptyState(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CategoryIcon(modifier = Modifier.size(52.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(subtitle, color = MutedText, textAlign = TextAlign.Center)
    }
}

internal fun LocalDate.dayTitle(): String {
    val today = LocalDate.now()
    val relative = when (this) {
        today -> " 今天"
        today.minusDays(1) -> " 昨天"
        else -> ""
    }
    val week = listOf("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日")
    return "${monthValue}月${dayOfMonth}日$relative ${week[dayOfWeek.value - 1]}"
}

internal fun BillRecordEntity.timeText(): String =
    java.time.Instant.ofEpochMilli(occurredAt)
        .atZone(java.time.ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("HH:mm", Locale.CHINA))
