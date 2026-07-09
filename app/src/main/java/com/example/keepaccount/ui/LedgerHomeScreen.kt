package com.example.keepaccount.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.keepaccount.R
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.DefaultCategories
import java.time.YearMonth

@Composable
internal fun LedgerPage(
    state: LedgerUiState,
    onShowTypeFilter: () -> Unit,
    onShowMonthPicker: () -> Unit,
    onRecordClick: (BillRecordEntity) -> Unit,
) {
    val allRecords = state.ledgerAllRecords
    val groups = allRecords.groupsByDay()
    val categoryLabel = state.selectedCategory?.let { localizedCategoryName(it) } ?: stringResource(R.string.all_types)
    val listState = rememberLazyListState()

    LaunchedEffect(state.selectedMonth) {
        listState.scrollToItem(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg),
    ) {
        LedgerHeader(
            month = state.selectedMonth,
            categoryLabel = categoryLabel,
            totalExpense = allRecords.expenseTotal(),
            totalIncome = allRecords.incomeTotal(),
            onShowTypeFilter = onShowTypeFilter,
            onShowMonthPicker = onShowMonthPicker,
        )

        if (groups.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.ledger_no_bills_title),
                subtitle = stringResource(R.string.ledger_no_bills_subtitle),
                modifier = Modifier.weight(1f),
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 8.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 88.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(groups) { group ->
                    DayGroupCard(
                        group = group,
                        onRecordClick = onRecordClick,
                    )
                }
            }
        }
    }
}

@Composable
internal fun LedgerHeader(
    month: YearMonth,
    categoryLabel: String,
    totalExpense: Long,
    totalIncome: Long,
    onShowTypeFilter: () -> Unit,
    onShowMonthPicker: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandGreen)
            .padding(top = 24.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
        ) {
            Text(
                text = stringResource(R.string.app_ledger_title),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterButton(
                label = categoryLabel,
                onClick = onShowTypeFilter,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, Color.White.copy(alpha = 0.25f))
                .padding(horizontal = 22.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onShowMonthPicker)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.format_year_month, month.year, month.monthValue),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "  ▾",
                    color = Color.White,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.ledger_total_expense, centsText(totalExpense)),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.ledger_total_income, centsText(totalIncome)),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
internal fun MiniProgramCapsule(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(DarkGreen.copy(alpha = 0.35f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("•••", color = Color.White, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(14.dp)
                .background(Color.White.copy(alpha = 0.5f)),
        )
        Text("—", color = Color.White, fontWeight = FontWeight.Bold)
        Text("⊙", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun FilterButton(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("▦", color = Color.White)
    }
}

@Composable
internal fun DayGroupCard(
    group: DailyGroup,
    onRecordClick: (BillRecordEntity) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(6.dp),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = group.date.localizedDayTitle(),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.ledger_day_expense, "%.2f".format(group.expenseCents / 100.0)),
                    color = Color(0xFF8D5A2B),
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = stringResource(R.string.ledger_day_income, "%.2f".format(group.incomeCents / 100.0)),
                    color = Color(0xFF1E6C9C),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            group.records.forEachIndexed { index, record ->
                BillRecordRow(
                    record = record,
                    showDivider = index != group.records.lastIndex,
                    onClick = { onRecordClick(record) },
                )
            }
        }
    }
}

@Composable
internal fun BillRecordRow(
    record: BillRecordEntity,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryIcon(category = record.category)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = localizedCategoryName(record.category),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = record.timeText(),
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
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
        Text(
            text = signedCentsText(record),
            fontWeight = FontWeight.SemiBold,
        )
    }
    if (showDivider) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 70.dp)
                .background(Divider),
        )
    }
}

@Composable
internal fun CategoryIcon(
    category: Int? = null,
    modifier: Modifier = Modifier,
    contentAlpha: Float = 1f,
) {
    val iconResId = categoryIconResId(category, LocalCategoryIconTheme.current)
    Box(
        modifier = modifier.size(34.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (iconResId == null) {
            Text(
                text = "?",
                color = MutedText.copy(alpha = contentAlpha),
                fontWeight = FontWeight.Bold,
            )
        } else {
            Image(
                painter = painterResource(iconResId),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer { alpha = contentAlpha },
                contentScale = ContentScale.Fit,
            )
        }
    }
}
