package com.example.keepaccount.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.BillType
import com.example.keepaccount.data.DefaultCategories
import com.example.keepaccount.data.label
import java.time.YearMonth

@Composable
fun MonthlyRankingActivityContent(
    month: YearMonth,
    type: BillType,
    onFinish: () -> Unit,
    viewModel: LedgerViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    SetStatusBarColor(Color.White)

    LaunchedEffect(month, type) {
        viewModel.openStatisticsMonth(type = type, month = month)
    }

    BackHandler(onBack = onFinish)

    CompositionLocalProvider(LocalCategoryIconTheme provides state.categoryIconTheme) {
        MonthlyRankingPage(
            state = state,
            month = month,
            type = type,
            onBack = onFinish,
        )
    }
}

@Composable
internal fun MonthlyRankingPage(
    state: LedgerUiState,
    month: YearMonth,
    type: BillType,
    onBack: () -> Unit,
) {
    var sort by remember { mutableStateOf(DetailSort.AMOUNT) }
    val records = remember(state.statisticsRecords, type, sort) {
        state.statisticsRecords
            .filter { it.type == type }
            .let { list ->
                when (sort) {
                    DetailSort.AMOUNT -> list.sortedWith(
                        compareByDescending<BillRecordEntity> { it.amountCents }
                            .thenByDescending { it.occurredAt },
                    )

                    DetailSort.TIME -> list.sortedByDescending { it.occurredAt }
                }
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
                .padding(top = 28.dp, start = 8.dp, end = 12.dp),
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
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = "${month.monthValue}月共${type.label()}",
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Divider),
        )
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SortChip("按金额", sort == DetailSort.AMOUNT) { sort = DetailSort.AMOUNT }
            SortChip("按时间", sort == DetailSort.TIME) { sort = DetailSort.TIME }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Divider),
        )
        if (records.isEmpty()) {
            EmptyState(
                title = "暂无排行",
                subtitle = "这个月份还没有${type.label()}账单",
                modifier = Modifier.weight(1f),
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
            ) {
                items(records) { record ->
                    FullRankingRecordRow(record = record)
                }
            }
        }
    }
}

@Composable
private fun FullRankingRecordRow(record: BillRecordEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        CategoryIcon(category = record.category, modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = DefaultCategories.nameOf(record.category),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (record.note.isNotBlank()) {
                        Text(
                            text = record.note,
                            color = MutedText,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
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
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Divider),
            )
        }
    }
}
