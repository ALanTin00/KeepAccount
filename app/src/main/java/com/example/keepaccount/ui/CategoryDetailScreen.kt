package com.example.keepaccount.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.DefaultCategories
import com.example.keepaccount.data.label

@Composable
internal fun CategoryDetailPage(
    state: LedgerUiState,
    detail: CategoryDetailState,
    onBack: () -> Unit,
    onSortSelected: (DetailSort) -> Unit,
) {
    val records = state.statisticsRecords
        .filter { it.category == detail.category && it.type == state.statisticsMode }
        .let { list ->
            when (detail.sort) {
                DetailSort.AMOUNT -> list.sortedByDescending { it.amountCents }
                DetailSort.TIME -> list.sortedByDescending { it.occurredAt }
            }
        }
    val total = records.sumOf { it.amountCents }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "‹",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(8.dp),
            )
        }
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "${state.statisticsMonth.monthValue}月${DefaultCategories.nameOf(detail.category)}共${state.statisticsMode.label()}",
            color = MutedText,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = centsText(total),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(48.dp))
        Row(
            modifier = Modifier.padding(horizontal = 26.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SortChip("按金额", detail.sort == DetailSort.AMOUNT) { onSortSelected(DetailSort.AMOUNT) }
            SortChip("按时间", detail.sort == DetailSort.TIME) { onSortSelected(DetailSort.TIME) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (records.isEmpty()) {
            EmptyState(
                title = "暂无明细",
                subtitle = "这个分类下还没有账单记录",
                modifier = Modifier.weight(1f),
            )
        } else {
            LazyColumn {
                items(records) { record ->
                    DetailRecordRow(record = record)
                }
            }
        }
    }
}

@Composable
internal fun SortChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (selected) BrandGreen else MutedText,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) SoftGreen else Color(0xFFF7F7F7))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
internal fun DetailRecordRow(record: BillRecordEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryIcon(category = record.category)
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = DefaultCategories.nameOf(record.category),
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.SemiBold,
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = signedCentsText(record),
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = record.dateTimeText(),
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
