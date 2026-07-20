package com.example.keepaccount.ui

import android.content.Context
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.keepaccount.R
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.BillType
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
internal fun StatisticsPage(
    state: LedgerUiState,
    onShowMonthPicker: () -> Unit,
    onSwitchMode: (BillType) -> Unit,
    onOpenCategoryDetail: (Int) -> Unit,
    onOpenMonthlyRanking: (YearMonth) -> Unit,
) {
    val context = LocalContext.current
    val records = state.statisticsRecords.filter { it.type == state.statisticsMode }
    val summaries = state.statisticsRecords.categorySummaries(state.statisticsMode)
    val total = records.sumOf { it.amountCents }
    val dailyItems = remember(records, state.statisticsMonth) {
        dailyPoints(records, state.statisticsMonth)
    }
    val monthlyItems = remember(state.statisticsRangeRecords, state.statisticsMonth, state.statisticsMode, context) {
        monthlyPoints(
            records = state.statisticsRangeRecords,
            month = state.statisticsMonth,
            mode = state.statisticsMode,
            context = context,
        )
    }
    var rankingMonth by remember { mutableStateOf(state.statisticsMonth) }
    LaunchedEffect(state.statisticsMonth, state.statisticsMode) {
        rankingMonth = state.statisticsMonth
    }
    val rankingRecords = remember(state.statisticsRangeRecords, state.statisticsMode, rankingMonth) {
        state.statisticsRangeRecords
            .filter { record -> record.type == state.statisticsMode && YearMonth.from(record.localDate()) == rankingMonth }
            .sortedWith(
                compareByDescending<BillRecordEntity> { it.amountCents }
                    .thenByDescending { it.occurredAt },
            )
    }
    val previewRankingRecords = remember(rankingRecords) {
        rankingRecords.take(MONTHLY_RANKING_PREVIEW_LIMIT)
    }
    var selectedDailyDialogDate by remember { mutableStateOf<LocalDate?>(null) }
    val selectedDailyDialogRecords = remember(state.statisticsRecords, state.statisticsMode, selectedDailyDialogDate) {
        selectedDailyDialogDate?.let { date ->
            state.statisticsRecords
                .filter { record -> record.type == state.statisticsMode && record.localDate() == date }
                .sortedByDescending { it.occurredAt }
        }.orEmpty()
    }
    val selectedDailyDialogTotal = remember(selectedDailyDialogRecords) {
        selectedDailyDialogRecords.sumOf { it.amountCents }
    }

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
                    title = stringResource(R.string.statistics_empty_title),
                    subtitle = stringResource(R.string.statistics_empty_subtitle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                )
            }
        } else {
            item {
                StatisticsSectionTitle(stringResource(if (state.statisticsMode == BillType.EXPENSE) R.string.statistics_expense_composition else R.string.statistics_income_composition))
                DonutChart(
                    summaries = summaries,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(donutChartHeight(summaries).dp),
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
                StatisticsSectionTitle(stringResource(R.string.statistics_daily_comparison))
                DailyComparisonChart(
                    points = dailyItems,
                    mode = state.statisticsMode,
                    onTooltipSelected = { point ->
                        if (point.value > 0) {
                            selectedDailyDialogDate = point.date
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .padding(horizontal = 14.dp),
                )
                StatisticsSectionTitle(stringResource(R.string.statistics_monthly_comparison))
                MonthlyComparisonChart(
                    points = monthlyItems,
                    selectedMonth = rankingMonth,
                    mode = state.statisticsMode,
                    onMonthSelected = { rankingMonth = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .padding(horizontal = 14.dp),
                )
                StatisticsSectionTitle(stringResource(R.string.format_ranking_title, rankingMonth.monthValue, localizedBillTypeLabel(state.statisticsMode)))
            }
            itemsIndexed(previewRankingRecords) { index, record ->
                RankingRecordRow(
                    rank = index + 1,
                    record = record,
                )
            }
            item {
                MonthlyRankingFooter(
                    visible = rankingRecords.isNotEmpty(),
                    onClick = { onOpenMonthlyRanking(rankingMonth) },
                )
            }
        }
    }

    selectedDailyDialogDate?.let { selectedDate ->
        DailyRecordDialog(
            date = selectedDate,
            mode = state.statisticsMode,
            records = selectedDailyDialogRecords,
            total = selectedDailyDialogTotal,
            onDismiss = { selectedDailyDialogDate = null },
        )
    }
}

@Composable
private fun MonthlyRankingFooter(
    visible: Boolean,
    onClick: () -> Unit,
) {
    if (!visible) return
    Text(
        text = stringResource(R.string.statistics_all_ranking),
        color = MutedText,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(top = 6.dp, bottom = 10.dp),
        textAlign = TextAlign.Center,
    )
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
            .background(BrandGreen)
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.format_year_month, month.year, month.monthValue),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onShowMonthPicker),
            )
            Spacer(modifier = Modifier.weight(1f))
            ModeButton(
                text = stringResource(R.string.bill_type_expense),
                selected = mode == BillType.EXPENSE,
                onClick = { onSwitchMode(BillType.EXPENSE) },
            )
            Spacer(modifier = Modifier.width(8.dp))
            ModeButton(
                text = stringResource(R.string.bill_type_income),
                selected = mode == BillType.INCOME,
                onClick = { onSwitchMode(BillType.INCOME) },
            )
        }
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = stringResource(R.string.format_total_type, localizedBillTypeLabel(mode)),
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
    val colors = chartColors()
    val context = LocalContext.current
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (summaries.isEmpty()) return@Canvas

            val strokeWidth = 32.dp.toPx()
            val outerRadius = min(size.width * 0.24f, size.height * 0.32f)
            val radius = (outerRadius - strokeWidth / 2f).coerceAtLeast(1f)
            val innerRadius = (outerRadius - strokeWidth).coerceAtLeast(0f)
            val center = Offset(size.width / 2f, size.height / 2f)
            val arcTopLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(radius * 2f, radius * 2f)
            val edgePadding = 8.dp.toPx()
            val labelRadius = outerRadius + 10.dp.toPx()
            val bendGap = 5.dp.toPx()
            val horizontalLength = 8.dp.toPx()
            val textGap = 4.dp.toPx()
            val centerGap = 6.dp.toPx()
            val maxLabelWidth = min(
                100.dp.toPx(),
                (size.width / 2f - outerRadius - edgePadding - bendGap - horizontalLength - textGap)
                    .coerceAtLeast(24.dp.toPx()),
            )
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 11.dp.toPx()
                color = Color(0xFF727875).toArgbInt()
                textAlign = Paint.Align.LEFT
            }
            val lineColor = Color(0xFFD9DEDB)
            val labels = mutableListOf<DonutLabel>()
            var startAngle = -90f

            summaries.forEach { item ->
                val sweep = item.percent * 360f
                val midAngle = startAngle + sweep / 2f
                val radians = midAngle.toRadians()
                val side = if (cos(radians) >= 0f) DonutLabelSide.RIGHT else DonutLabelSide.LEFT
                val labelText = buildDonutLabelText(
                    categoryName = context.localizedCategoryName(item.category),
                    percent = item.percent,
                    maxWidth = maxLabelWidth,
                    paint = textPaint,
                )
                labels += DonutLabel(
                    text = labelText,
                    textWidth = textPaint.measureText(labelText),
                    anchor = Offset(
                        x = center.x + cos(radians) * outerRadius,
                        y = center.y + sin(radians) * outerRadius,
                    ),
                    desiredY = center.y + sin(radians) * labelRadius,
                    side = side,
                )
                startAngle += sweep
            }

            val textHeight = textPaint.descent() - textPaint.ascent()
            val adjustedLabels = adjustDonutLabels(
                labels = labels,
                minY = edgePadding + textHeight / 2f,
                maxY = size.height - edgePadding - textHeight / 2f,
                spacing = textHeight + 5.dp.toPx(),
            )

            fun labelLayout(label: DonutLabel): DonutLabelLayout {
                val verticalDistance = (
                    kotlin.math.abs(label.adjustedY - center.y) - textHeight / 2f
                ).coerceAtLeast(0f)
                val circleHalfWidth = if (verticalDistance < outerRadius) {
                    kotlin.math.sqrt(outerRadius * outerRadius - verticalDistance * verticalDistance)
                } else {
                    0f
                }
                val safeHalfWidth = max(circleHalfWidth, centerGap)
                val sideDirection = if (label.side == DonutLabelSide.RIGHT) 1f else -1f
                val bendPoint = Offset(
                    x = center.x + sideDirection * (safeHalfWidth + bendGap),
                    y = label.adjustedY,
                )
                val lineEnd = Offset(
                    x = bendPoint.x + sideDirection * horizontalLength,
                    y = label.adjustedY,
                )
                val desiredTextX = if (label.side == DonutLabelSide.RIGHT) {
                    lineEnd.x + textGap
                } else {
                    lineEnd.x - textGap - label.textWidth
                }
                val maxTextX = (size.width - edgePadding - label.textWidth).coerceAtLeast(edgePadding)
                return DonutLabelLayout(
                    bendPoint = bendPoint,
                    lineEnd = lineEnd,
                    textX = desiredTextX.coerceIn(edgePadding, maxTextX),
                )
            }

            // Leaders stay behind the donut, so collision routing can never show inside it.
            adjustedLabels.forEach { label ->
                val layout = labelLayout(label)
                drawLine(
                    color = lineColor,
                    start = label.anchor,
                    end = layout.bendPoint,
                    strokeWidth = 1.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = lineColor,
                    start = layout.bendPoint,
                    end = layout.lineEnd,
                    strokeWidth = 1.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }

            startAngle = -90f
            summaries.forEachIndexed { index, item ->
                val sweep = item.percent * 360f
                val gapAngle = min(1.2f, sweep * 0.15f)
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle + gapAngle / 2f,
                    sweepAngle = (sweep - gapAngle).coerceAtLeast(0.01f),
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                )
                startAngle += sweep
            }
            drawCircle(color = Color.White, radius = innerRadius, center = center)

            val baselineOffset = -(textPaint.ascent() + textPaint.descent()) / 2f
            adjustedLabels.forEach { label ->
                val layout = labelLayout(label)
                drawContext.canvas.nativeCanvas.drawText(
                    label.text,
                    layout.textX,
                    label.adjustedY + baselineOffset,
                    textPaint,
                )
            }
        }
        if (summaries.isEmpty()) {
            Text(stringResource(R.string.statistics_no_data), color = MutedText)
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
            text = localizedCategoryName(summary.category),
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
private fun DailyComparisonChart(
    points: List<DailyChartPoint>,
    mode: BillType,
    onTooltipSelected: (DailyChartPoint) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedIndex by remember { mutableStateOf(points.defaultSelectedDailyIndex()) }
    LaunchedEffect(points) {
        selectedIndex = points.defaultSelectedDailyIndex()
    }

    InteractiveBarChart(
        points = points,
        selectedIndex = selectedIndex,
        onSelected = { index ->
            selectedIndex = index
        },
        onTooltipSelected = { index ->
            points.getOrNull(index)?.let(onTooltipSelected)
        },
        mode = mode,
        modifier = modifier,
        tooltipTitle = { point -> context.getString(R.string.format_day_total_type, point.date.monthValue, point.date.dayOfMonth, context.localizedBillTypeLabel(mode)) },
        showTooltip = true,
        showValueLabels = false,
        xLabelStep = max(1, points.size / 6),
        horizontalHitScale = DAILY_BAR_HORIZONTAL_HIT_SCALE,
        barMaxHeightRatio = DAILY_BAR_MAX_HEIGHT_RATIO,
        verticalHitMode = BarVerticalHitMode.MaxBarHeight,
    )
}

@Composable
private fun MonthlyComparisonChart(
    points: List<MonthlyChartPoint>,
    selectedMonth: YearMonth,
    mode: BillType,
    onMonthSelected: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedIndex by remember { mutableStateOf(points.indexOfMonth(selectedMonth)) }
    LaunchedEffect(points, selectedMonth, mode) {
        selectedIndex = points.indexOfMonth(selectedMonth)
    }

    InteractiveBarChart(
        points = points,
        selectedIndex = selectedIndex,
        onSelected = { index ->
            selectedIndex = index
            points.getOrNull(index)?.let { onMonthSelected(it.month) }
        },
        mode = mode,
        modifier = modifier,
        tooltipTitle = { point -> point.label + context.localizedBillTypeLabel(mode) },
        showTooltip = false,
        showValueLabels = true,
        xLabelStep = 1,
        showYAxisLabels = false,
    )
}

@Composable
private fun <T : ChartPoint> InteractiveBarChart(
    points: List<T>,
    selectedIndex: Int?,
    onSelected: (Int) -> Unit,
    onTooltipSelected: ((Int) -> Unit)? = null,
    mode: BillType,
    modifier: Modifier = Modifier,
    tooltipTitle: (T) -> String,
    showTooltip: Boolean,
    showValueLabels: Boolean,
    xLabelStep: Int,
    horizontalHitScale: Float = DEFAULT_BAR_HORIZONTAL_HIT_SCALE,
    barMaxHeightRatio: Float = DEFAULT_BAR_MAX_HEIGHT_RATIO,
    minVerticalHitRatio: Float = MIN_BAR_VERTICAL_HIT_RATIO,
    verticalHitMode: BarVerticalHitMode = BarVerticalHitMode.MinimumRatio,
    showYAxisLabels: Boolean = true,
) {
    val maxValue = points.maxOfOrNull { it.value }?.takeIf { it > 0 } ?: 1L
    val selectedPoint = selectedIndex?.let { points.getOrNull(it) }
    Column(modifier = modifier) {
        if (!showTooltip && selectedPoint != null) {
            Text(
                text = centsText(selectedPoint.value),
                color = BrandGreen,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 22.dp, bottom = 2.dp),
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(points, selectedIndex, maxValue, showTooltip, horizontalHitScale, barMaxHeightRatio, minVerticalHitRatio, verticalHitMode, showYAxisLabels, onTooltipSelected) {
                    detectTapGestures { offset ->
                        val tooltipHitBox = buildChartTooltipHitBox(
                            points = points,
                            selectedIndex = selectedIndex,
                            maxValue = maxValue,
                            width = size.width.toFloat(),
                            height = size.height.toFloat(),
                            density = this,
                            showTooltip = showTooltip,
                            barMaxHeightRatio = barMaxHeightRatio,
                            showYAxisLabels = showYAxisLabels,
                        )
                        if (tooltipHitBox != null && tooltipHitBox.contains(offset)) {
                            onTooltipSelected?.invoke(tooltipHitBox.index)
                            return@detectTapGestures
                        }
                        val hitBoxes = buildBarHitBoxes(
                            points = points,
                            maxValue = maxValue,
                            width = size.width.toFloat(),
                            height = size.height.toFloat(),
                            density = this,
                            showTooltip = showTooltip,
                            horizontalHitScale = horizontalHitScale,
                            barMaxHeightRatio = barMaxHeightRatio,
                            minVerticalHitRatio = minVerticalHitRatio,
                            verticalHitMode = verticalHitMode,
                            showYAxisLabels = showYAxisLabels,
                        )
                        hitBoxes.firstOrNull { it.contains(offset) }?.let { onSelected(it.index) }
                    }
                },
        ) {
            if (points.isEmpty()) return@Canvas

            val leftAxis = if (showYAxisLabels) 42.dp.toPx() else 0f
            val topPadding = if (showTooltip) 56.dp.toPx() else 24.dp.toPx()
            val bottomAxis = 24.dp.toPx()
            val chartWidth = size.width - leftAxis
            val chartHeight = size.height - topPadding - bottomAxis
            val chartTop = topPadding
            val chartBottom = chartTop + chartHeight
            val slotWidth = chartWidth / points.size
            val barWidth = min(18.dp.toPx(), (slotWidth * 0.42f).coerceAtLeast(3.dp.toPx()))
            val clampedBarMaxHeightRatio = barMaxHeightRatio.coerceIn(0f, 1f)
            val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 9.dp.toPx()
                color = MutedText.toArgbInt()
            }
            val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 9.dp.toPx()
                color = BrandGreen.toArgbInt()
                textAlign = Paint.Align.CENTER
            }

            repeat(5) { line ->
                val fraction = line / 4f
                val y = chartTop + chartHeight * fraction
                drawLine(
                    color = Divider,
                    start = Offset(leftAxis, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                )
                if (showYAxisLabels) {
                    val tick = (maxValue * (4 - line) / 4f).roundToLong()
                    axisPaint.textAlign = Paint.Align.RIGHT
                    drawContext.canvas.nativeCanvas.drawText(
                        axisCentsText(tick),
                        leftAxis - 6.dp.toPx(),
                        y + 3.dp.toPx(),
                        axisPaint,
                    )
                }
            }

            points.forEachIndexed { index, point ->
                val ratio = point.value.toFloat() / maxValue.toFloat()
                val height = (chartHeight * clampedBarMaxHeightRatio * ratio)
                    .coerceAtLeast(if (point.value > 0) 3.dp.toPx() else 0f)
                val centerX = leftAxis + slotWidth * index + slotWidth / 2f
                val x = centerX - barWidth / 2f
                val y = chartBottom - height
                val selected = index == selectedIndex
                val hasSelection = selectedIndex != null
                val barColor = when {
                    selected -> BrandGreen
                    hasSelection -> BrandGreen.copy(alpha = 0.22f)
                    else -> Color(0xFFCDEEDD)
                }
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, height),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                )

                if (showValueLabels && point.value > 0) {
                    valuePaint.color = if (selected) BrandGreen.toArgbInt() else BrandGreen.copy(alpha = 0.62f).toArgbInt()
                    drawContext.canvas.nativeCanvas.drawText(
                        centsText(point.value),
                        centerX,
                        (y - 6.dp.toPx()).coerceAtLeast(10.dp.toPx()),
                        valuePaint,
                    )
                }

                val showXLabel = index == 0 || index == points.lastIndex || index % xLabelStep == 0
                if (showXLabel) {
                    axisPaint.textAlign = Paint.Align.CENTER
                    drawContext.canvas.nativeCanvas.drawText(
                        point.label,
                        centerX,
                        size.height - 5.dp.toPx(),
                        axisPaint,
                    )
                }
            }

            if (showTooltip) {
                val tooltipIndex = selectedIndex
                val tooltipPoint = tooltipIndex?.let { points.getOrNull(it) }
                if (tooltipIndex != null && tooltipPoint != null) {
                    val selectedCenterX = leftAxis + slotWidth * tooltipIndex + slotWidth / 2f
                    val selectedHeight = (chartHeight * clampedBarMaxHeightRatio * (tooltipPoint.value.toFloat() / maxValue.toFloat()))
                        .coerceAtLeast(if (tooltipPoint.value > 0) 3.dp.toPx() else 0f)
                    val selectedTop = chartBottom - selectedHeight
                    drawChartTooltip(
                        anchorX = selectedCenterX,
                        anchorY = selectedTop,
                        title = tooltipTitle(tooltipPoint),
                        amount = centsText(tooltipPoint.value),
                        mode = mode,
                        minX = leftAxis,
                        maxX = size.width,
                        fixedTopY = DAILY_TOOLTIP_TOP_PADDING.dp.toPx(),
                        connectorEndY = selectedTop,
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawChartTooltip(
    anchorX: Float,
    anchorY: Float,
    title: String,
    amount: String,
    mode: BillType,
    minX: Float,
    maxX: Float,
    fixedTopY: Float? = null,
    connectorEndY: Float? = null,
) {
    val tooltipWidth = 112.dp.toPx()
    val tooltipHeight = 48.dp.toPx()
    val tooltipX = (anchorX - tooltipWidth / 2f).coerceIn(minX, maxX - tooltipWidth)
    val tooltipY = fixedTopY ?: (anchorY - tooltipHeight - 10.dp.toPx()).coerceAtLeast(4.dp.toPx())
    val pointerX = anchorX.coerceIn(tooltipX + 8.dp.toPx(), tooltipX + tooltipWidth - 8.dp.toPx())
    val tooltipColor = Color(0xFF333333)
    val connectorStartY = tooltipY + tooltipHeight + 7.dp.toPx()
    connectorEndY?.let { endY ->
        if (endY - connectorStartY > 8.dp.toPx()) {
            drawLine(
                color = MutedText.copy(alpha = 0.42f),
                start = Offset(pointerX, connectorStartY),
                end = Offset(pointerX, endY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx())),
            )
        }
    }
    drawRoundRect(
        color = tooltipColor,
        topLeft = Offset(tooltipX, tooltipY),
        size = Size(tooltipWidth, tooltipHeight),
        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
    )
    val triangleTop = tooltipY + tooltipHeight - 1.dp.toPx()
    val triangle = Path().apply {
        moveTo(pointerX - 5.dp.toPx(), triangleTop)
        lineTo(pointerX + 5.dp.toPx(), triangleTop)
        lineTo(pointerX, tooltipY + tooltipHeight + 7.dp.toPx())
        close()
    }
    drawPath(triangle, tooltipColor)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 10.dp.toPx()
        color = Color.White.toArgbInt()
    }
    drawContext.canvas.nativeCanvas.drawText(
        title,
        tooltipX + tooltipWidth / 2f,
        tooltipY + 18.dp.toPx(),
        paint,
    )
    paint.color = (if (mode == BillType.EXPENSE) BrandGreen else Color(0xFF8BC9FF)).toArgbInt()
    paint.textSize = 11.dp.toPx()
    paint.isFakeBoldText = true
    drawContext.canvas.nativeCanvas.drawText(
        amount,
        tooltipX + tooltipWidth / 2f,
        tooltipY + 34.dp.toPx(),
        paint,
    )
}


@Composable
private fun DailyRecordDialog(
    date: LocalDate,
    mode: BillType,
    records: List<BillRecordEntity>,
    total: Long,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp, vertical = 12.dp),
            ) {
                SheetTitle(stringResource(R.string.detail_title), onDismiss)
                DetailLine(label = stringResource(R.string.detail_date), value = stringResource(R.string.format_full_date, date.year, date.monthValue, date.dayOfMonth))
                DetailLine(label = stringResource(R.string.detail_type), value = localizedBillTypeLabel(mode))
                DetailLine(label = stringResource(R.string.detail_amount), value = centsText(total))
                Spacer(modifier = Modifier.height(18.dp))
                if (records.isEmpty()) {
                    EmptyState(
                        title = stringResource(R.string.statistics_daily_empty_title),
                        subtitle = stringResource(R.string.statistics_daily_empty_subtitle, localizedBillTypeLabel(mode)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 330.dp)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        records.forEachIndexed { index, record ->
                            DailyDialogRecordRow(
                                rank = index + 1,
                                record = record,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DailyDialogRecordRow(
    rank: Int,
    record: BillRecordEntity,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = rank.toString(),
            modifier = Modifier.width(22.dp),
            color = MutedText,
            style = MaterialTheme.typography.bodySmall,
        )
        CategoryIcon(
            category = record.category,
            modifier = Modifier.size(44.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = localizedCategoryName(record.category),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = record.timeText(),
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
                if (record.note.isNotBlank()) {
                    Text(
                        text = "  /  ${record.note}",
                        color = MutedText,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        Text(
            text = signedCentsText(record),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun RankingRecordRow(
    rank: Int,
    record: BillRecordEntity,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = rank.toString(),
            modifier = Modifier.width(22.dp),
            color = MutedText,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.width(4.dp))
        CategoryIcon(category = record.category, modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = localizedCategoryName(record.category),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = record.note.ifBlank { record.localizedDateTimeText() },
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = signedCentsText(record),
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = record.localizedDateTimeText(),
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

internal interface ChartPoint {
    val label: String
    val value: Long
}

internal data class DailyChartPoint(
    val date: LocalDate,
    override val label: String,
    override val value: Long,
) : ChartPoint

internal data class MonthlyChartPoint(
    val month: YearMonth,
    override val label: String,
    override val value: Long,
) : ChartPoint

private fun <T : ChartPoint> buildBarHitBoxes(
    points: List<T>,
    maxValue: Long,
    width: Float,
    height: Float,
    density: Density,
    showTooltip: Boolean,
    horizontalHitScale: Float,
    barMaxHeightRatio: Float,
    minVerticalHitRatio: Float,
    verticalHitMode: BarVerticalHitMode,
    showYAxisLabels: Boolean,
): List<BarHitBox> = with(density) {
    if (points.isEmpty()) return@with emptyList()
    val leftAxis = if (showYAxisLabels) 42.dp.toPx() else 0f
    val topPadding = if (showTooltip) 56.dp.toPx() else 24.dp.toPx()
    val bottomAxis = 24.dp.toPx()
    val chartWidth = width - leftAxis
    val chartHeight = height - topPadding - bottomAxis
    if (chartWidth <= 0f || chartHeight <= 0f) return@with emptyList()
    val chartBottom = topPadding + chartHeight
    val slotWidth = chartWidth / points.size
    val barWidth = min(18.dp.toPx(), (slotWidth * 0.42f).coerceAtLeast(3.dp.toPx()))
    val hitWidth = max(barWidth, slotWidth * horizontalHitScale.coerceIn(0f, 1f))
    val clampedBarMaxHeightRatio = barMaxHeightRatio.coerceIn(0f, 1f)
    val maxBarHeight = chartHeight * clampedBarMaxHeightRatio
    points.mapIndexed { index, point ->
        val ratio = point.value.toFloat() / maxValue.toFloat()
        val barHeight = (chartHeight * clampedBarMaxHeightRatio * ratio)
            .coerceAtLeast(if (point.value > 0) 3.dp.toPx() else 0f)
        val hitHeight = if (point.value > 0) {
            when (verticalHitMode) {
                BarVerticalHitMode.MaxBarHeight -> maxBarHeight
                BarVerticalHitMode.MinimumRatio -> max(
                    barHeight,
                    chartHeight * minVerticalHitRatio.coerceIn(0f, 1f),
                )
            }
        } else {
            0f
        }
        val centerX = leftAxis + slotWidth * index + slotWidth / 2f
        BarHitBox(
            index = index,
            left = centerX - hitWidth / 2f,
            top = chartBottom - hitHeight,
            right = centerX + hitWidth / 2f,
            bottom = chartBottom,
        )
    }
}

private enum class BarVerticalHitMode {
    MinimumRatio,
    MaxBarHeight,
}


private fun <T : ChartPoint> buildChartTooltipHitBox(
    points: List<T>,
    selectedIndex: Int?,
    maxValue: Long,
    width: Float,
    height: Float,
    density: Density,
    showTooltip: Boolean,
    barMaxHeightRatio: Float,
    showYAxisLabels: Boolean,
): ChartTooltipHitBox? = with(density) {
    if (!showTooltip || selectedIndex == null || points.isEmpty()) return@with null
    val point = points.getOrNull(selectedIndex) ?: return@with null
    if (point.value <= 0) return@with null
    val leftAxis = if (showYAxisLabels) 42.dp.toPx() else 0f
    val topPadding = 56.dp.toPx()
    val bottomAxis = 24.dp.toPx()
    val chartWidth = width - leftAxis
    val chartHeight = height - topPadding - bottomAxis
    if (chartWidth <= 0f || chartHeight <= 0f) return@with null
    val slotWidth = chartWidth / points.size
    val chartBottom = topPadding + chartHeight
    val selectedCenterX = leftAxis + slotWidth * selectedIndex + slotWidth / 2f
    val selectedHeight = (chartHeight * barMaxHeightRatio.coerceIn(0f, 1f) * (point.value.toFloat() / maxValue.toFloat()))
        .coerceAtLeast(3.dp.toPx())
    val selectedTop = chartBottom - selectedHeight
    val tooltipWidth = 112.dp.toPx()
    val tooltipHeight = 48.dp.toPx()
    val tooltipMaxX = max(leftAxis, width - tooltipWidth)
    val tooltipX = (selectedCenterX - tooltipWidth / 2f).coerceIn(leftAxis, tooltipMaxX)
    val tooltipY = DAILY_TOOLTIP_TOP_PADDING.dp.toPx()
    ChartTooltipHitBox(
        index = selectedIndex,
        left = tooltipX,
        top = tooltipY,
        right = tooltipX + tooltipWidth,
        bottom = min(tooltipY + tooltipHeight + 7.dp.toPx(), selectedTop),
    )
}

private data class BarHitBox(
    val index: Int,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    fun contains(offset: Offset): Boolean =
        offset.x in left..right && offset.y in top..bottom
}

private data class ChartTooltipHitBox(
    val index: Int,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    fun contains(offset: Offset): Boolean =
        offset.x in left..right && offset.y in top..bottom
}

private data class DonutLabel(
    val text: String,
    val textWidth: Float,
    val anchor: Offset,
    val desiredY: Float,
    val side: DonutLabelSide,
    val adjustedY: Float = desiredY,
)

private data class DonutLabelLayout(
    val bendPoint: Offset,
    val lineEnd: Offset,
    val textX: Float,
)

private enum class DonutLabelSide {
    LEFT,
    RIGHT,
}

internal fun dailyPoints(records: List<BillRecordEntity>, month: YearMonth): List<DailyChartPoint> {
    val byDay = records.groupBy { it.localDate().dayOfMonth }
    return (1..month.lengthOfMonth()).map { day ->
        DailyChartPoint(
            date = month.atDay(day),
            label = day.toString(),
            value = byDay[day].orEmpty().sumOf { it.amountCents },
        )
    }
}

internal fun monthlyPoints(
    records: List<BillRecordEntity>,
    month: YearMonth,
    mode: BillType,
    context: Context,
): List<MonthlyChartPoint> {
    val byMonth = records
        .filter { it.type == mode }
        .groupBy { YearMonth.from(it.localDate()) }
        .mapValues { (_, monthRecords) -> monthRecords.sumOf { it.amountCents } }
    return (MONTHLY_COMPARISON_MONTH_COUNT - 1 downTo 0).map { offset ->
        val targetMonth = month.minusMonths(offset.toLong())
        MonthlyChartPoint(
            month = targetMonth,
            label = context.getString(R.string.format_month_label, targetMonth.monthValue),
            value = byMonth[targetMonth] ?: 0L,
        )
    }
}

private fun List<DailyChartPoint>.defaultSelectedDailyIndex(): Int? {
    if (isEmpty()) return null
    val today = LocalDate.now()
    val todayIndex = indexOfFirst { it.date == today && it.value > 0 }
    if (todayIndex >= 0) return todayIndex
    return indices.maxByOrNull { this[it].value }
}

private fun List<MonthlyChartPoint>.indexOfMonth(month: YearMonth): Int? {
    val index = indexOfFirst { it.month == month }
    return if (index >= 0) index else indices.maxByOrNull { this[it].value }
}

private fun donutChartHeight(summaries: List<CategorySummary>): Int {
    var leftCount = 0
    var rightCount = 0
    var startAngle = -90f
    summaries.forEach { summary ->
        val sweep = summary.percent * 360f
        val radians = (startAngle + sweep / 2f).toRadians()
        if (cos(radians) >= 0f) {
            rightCount += 1
        } else {
            leftCount += 1
        }
        startAngle += sweep
    }
    val maxSideCount = max(leftCount, rightCount)
    return (240 + (maxSideCount - 4).coerceAtLeast(0) * 24).coerceAtMost(360)
}

private fun adjustDonutLabels(
    labels: List<DonutLabel>,
    minY: Float,
    maxY: Float,
    spacing: Float,
): List<DonutLabel> =
    labels.groupBy { it.side }
        .flatMap { (_, sideLabels) ->
            val sorted = sideLabels.sortedBy { it.desiredY }
            if (sorted.size <= 1) {
                return@flatMap sorted.map { it.copy(adjustedY = it.desiredY.coerceIn(minY, maxY)) }
            }

            val availableSpacing = ((maxY - minY) / (sorted.size - 1)).coerceAtLeast(0f)
            val actualSpacing = min(spacing, availableSpacing)
            val levels = FloatArray(sorted.size)
            val weights = IntArray(sorted.size)
            var blockCount = 0

            sorted.forEachIndexed { index, label ->
                levels[blockCount] = label.desiredY.coerceIn(minY, maxY) - index * actualSpacing
                weights[blockCount] = 1
                blockCount += 1
                while (blockCount >= 2 && levels[blockCount - 2] > levels[blockCount - 1]) {
                    val leftWeight = weights[blockCount - 2]
                    val rightWeight = weights[blockCount - 1]
                    levels[blockCount - 2] = (
                        levels[blockCount - 2] * leftWeight + levels[blockCount - 1] * rightWeight
                    ) / (leftWeight + rightWeight)
                    weights[blockCount - 2] = leftWeight + rightWeight
                    blockCount -= 1
                }
            }

            val minLevel = minY
            val maxLevel = maxY - (sorted.size - 1) * actualSpacing
            val fittedLevels = FloatArray(sorted.size)
            var itemIndex = 0
            for (blockIndex in 0 until blockCount) {
                val level = levels[blockIndex].coerceIn(minLevel, maxLevel)
                repeat(weights[blockIndex]) {
                    fittedLevels[itemIndex] = level
                    itemIndex += 1
                }
            }

            sorted.mapIndexed { index, label ->
                label.copy(adjustedY = fittedLevels[index] + index * actualSpacing)
            }
        }

private fun buildDonutLabelText(
    categoryName: String,
    percent: Float,
    maxWidth: Float,
    paint: Paint,
): String {
    val percentText = "%.2f%%".format(percent * 100f)
    val suffix = " $percentText"
    val categoryWidth = maxWidth - paint.measureText(suffix)
    if (categoryWidth <= paint.measureText("…")) return percentText
    return ellipsizeDonutText(categoryName, categoryWidth, paint) + suffix
}

private fun ellipsizeDonutText(text: String, maxWidth: Float, paint: Paint): String {
    if (paint.measureText(text) <= maxWidth) return text
    val ellipsis = "…"
    val availableWidth = maxWidth - paint.measureText(ellipsis)
    if (availableWidth <= 0f) return ellipsis
    val characterCount = paint.breakText(text, true, availableWidth, null)
    return text.take(characterCount).trimEnd() + ellipsis
}

private fun chartColors(): List<Color> = listOf(
    BrandGreen,
    Color(0xFF74D29C),
    Color(0xFF9ADCB7),
    Color(0xFFBFEAD0),
    Color(0xFFDDF5E8),
)

private fun Float.toRadians(): Float = (this * PI / 180.0).toFloat()

private fun axisCentsText(cents: Long): String = "¥${(cents / 100.0).roundToInt()}"

private fun Float.roundToLong(): Long = roundToInt().toLong()

private fun Color.toArgbInt(): Int = android.graphics.Color.argb(
    (alpha * 255).roundToInt().coerceIn(0, 255),
    (red * 255).roundToInt().coerceIn(0, 255),
    (green * 255).roundToInt().coerceIn(0, 255),
    (blue * 255).roundToInt().coerceIn(0, 255),
)

private const val MONTHLY_RANKING_PREVIEW_LIMIT = 10
private const val MONTHLY_COMPARISON_MONTH_COUNT = 7
private const val DEFAULT_BAR_HORIZONTAL_HIT_SCALE = 0f
private const val DAILY_BAR_HORIZONTAL_HIT_SCALE = 0.86f
private const val DEFAULT_BAR_MAX_HEIGHT_RATIO = 0.86f
private const val DAILY_BAR_MAX_HEIGHT_RATIO = 0.78f
private const val MIN_BAR_VERTICAL_HIT_RATIO = 0.5f
private const val DAILY_TOOLTIP_TOP_PADDING = 4
