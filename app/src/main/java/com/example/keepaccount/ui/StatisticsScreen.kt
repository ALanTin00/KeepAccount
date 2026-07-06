package com.example.keepaccount.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.BillType
import com.example.keepaccount.data.DefaultCategories
import com.example.keepaccount.data.label
import java.time.YearMonth
import kotlin.math.max

@Composable
internal fun StatisticsPage(
    state: LedgerUiState,
    onShowMonthPicker: () -> Unit,
    onSwitchMode: (BillType) -> Unit,
    onOpenCategoryDetail: (Int) -> Unit,
) {
    val records = state.statisticsRecords.filter { it.type == state.statisticsMode }
    val summaries = state.statisticsRecords.categorySummaries(state.statisticsMode)
    val total = records.sumOf { it.amountCents }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 86.dp),
    ) {
        item {
            StatisticsHeader(
                month = state.statisticsMonth,
                mode = state.statisticsMode,
                total = total,
                onShowMonthPicker = onShowMonthPicker,
                onSwitchMode = onSwitchMode,
            )
        }
        if (records.isEmpty()) {
            item {
                EmptyState(
                    title = "暂无统计数据",
                    subtitle = "添加账单后，这里会自动生成图表",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                )
            }
        } else {
            item {
                StatisticsSectionTitle("支出构成".takeIf { state.statisticsMode == BillType.EXPENSE } ?: "入账构成")
                DonutChart(
                    summaries = summaries,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .padding(horizontal = 18.dp),
                )
            }
            items(summaries) { summary ->
                CategorySummaryRow(
                    summary = summary,
                    total = total,
                    onClick = { onOpenCategoryDetail(summary.category) },
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                StatisticsSectionTitle("每日对比")
                BarChart(
                    points = dailyPoints(records, state.statisticsMonth),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .padding(horizontal = 20.dp),
                )
                StatisticsSectionTitle("月度对比")
                BarChart(
                    points = monthlyPoints(
                        records = state.statisticsRangeRecords,
                        month = state.statisticsMonth,
                        mode = state.statisticsMode,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .padding(horizontal = 20.dp),
                    emphasizeLast = true,
                )
                Text(
                    text = "${state.statisticsMonth.monthValue}月${state.statisticsMode.label()}排行",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    color = MutedText,
                )
            }
        }
    }
}

@Composable
internal fun StatisticsHeader(
    month: YearMonth,
    mode: BillType,
    total: Long,
    onShowMonthPicker: () -> Unit,
    onSwitchMode: (BillType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(330.dp)
            .background(BrandGreen)
            .padding(start = 20.dp, end = 20.dp, top = 28.dp),
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Spacer(modifier = Modifier.height(68.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${month.year}年${month.monthValue}月",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onShowMonthPicker),
            )
            Spacer(modifier = Modifier.weight(1f))
            ModeButton(
                text = "支出",
                selected = mode == BillType.EXPENSE,
                onClick = { onSwitchMode(BillType.EXPENSE) },
            )
            Spacer(modifier = Modifier.width(8.dp))
            ModeButton(
                text = "入账",
                selected = mode == BillType.INCOME,
                onClick = { onSwitchMode(BillType.INCOME) },
            )
        }
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = "共${mode.label()}",
            color = Color.White.copy(alpha = 0.72f),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = centsText(total),
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
internal fun ModeButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) Color.White.copy(alpha = 0.18f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
internal fun StatisticsSectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 20.dp, top = 28.dp, bottom = 16.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
internal fun DonutChart(summaries: List<CategorySummary>, modifier: Modifier = Modifier) {
    val colors = listOf(
        BrandGreen,
        Color(0xFF74D29C),
        Color(0xFF9ADCB7),
        Color(0xFFBFEAD0),
        Color(0xFFDDF5E8),
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(150.dp)) {
            var startAngle = -90f
            summaries.forEachIndexed { index, item ->
                val sweep = item.percent * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = 34.dp.toPx(), cap = StrokeCap.Butt),
                    size = Size(size.width, size.height),
                )
                startAngle += sweep
            }
        }
        if (summaries.isEmpty()) {
            Text("暂无数据", color = MutedText)
        }
    }
}

@Composable
internal fun CategorySummaryRow(
    summary: CategorySummary,
    total: Long,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryIcon(category = summary.category)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = DefaultCategories.nameOf(summary.category),
            modifier = Modifier.width(70.dp),
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "${"%.1f".format(summary.percent * 100)}%",
            modifier = Modifier.width(48.dp),
            color = MutedText,
            style = MaterialTheme.typography.bodySmall,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(SoftGreen),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((summary.amountCents.toFloat() / max(total, 1).toFloat()).coerceIn(0f, 1f))
                    .background(BrandGreen),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = centsText(summary.amountCents),
            fontWeight = FontWeight.SemiBold,
        )
        Text(" 〉", color = MutedText)
    }
}

@Composable
internal fun BarChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier,
    emphasizeLast: Boolean = false,
) {
    val maxValue = points.maxOfOrNull { it.value }?.takeIf { it > 0 } ?: 1L
    val labelStep = max(1, points.size / 6)
    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            val barGap = 6.dp.toPx()
            val availableWidth = size.width
            val barWidth = ((availableWidth - barGap * (points.size - 1)) / points.size)
                .coerceAtLeast(2.dp.toPx())
            repeat(5) { line ->
                val y = size.height * line / 4f
                drawLine(
                    color = Divider,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            points.forEachIndexed { index, point ->
                val ratio = point.value.toFloat() / maxValue.toFloat()
                val height = (size.height * 0.88f * ratio).coerceAtLeast(if (point.value > 0) 3.dp.toPx() else 0f)
                val x = index * (barWidth + barGap)
                val y = size.height - height
                drawRect(
                    color = if (emphasizeLast && index == points.lastIndex) BrandGreen else Color(0xFFCDEEDD),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, height),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            points.forEachIndexed { index, point ->
                val showLabel = index == 0 || index == points.lastIndex || index % labelStep == 0
                Text(
                    text = if (showLabel) point.label else "",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                    maxLines = 1,
                )
            }
        }
    }
}


data class ChartPoint(
    val label: String,
    val value: Long,
)

internal fun dailyPoints(records: List<BillRecordEntity>, month: YearMonth): List<ChartPoint> {
    val byDay = records.groupBy { it.localDate().dayOfMonth }
    return (1..month.lengthOfMonth()).map { day ->
        ChartPoint(
            label = day.toString(),
            value = byDay[day].orEmpty().sumOf { it.amountCents },
        )
    }
}

internal fun monthlyPoints(
    records: List<BillRecordEntity>,
    month: YearMonth,
    mode: BillType,
): List<ChartPoint> {
    val byMonth = records
        .filter { it.type == mode }
        .groupBy { YearMonth.from(it.localDate()) }
        .mapValues { (_, monthRecords) -> monthRecords.sumOf { it.amountCents } }
    return (5 downTo 0).map { offset ->
        val targetMonth = month.minusMonths(offset.toLong())
        ChartPoint(
            label = "${targetMonth.monthValue}月",
            value = byMonth[targetMonth] ?: 0L,
        )
    }
}
