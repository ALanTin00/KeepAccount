package com.example.keepaccount.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keepaccount.data.BillCategory
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.DefaultCategories
import com.example.keepaccount.data.label
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatePickerSheet(
    state: AddBillState,
    onDismiss: () -> Unit,
    onChangeMonth: (Long) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
    ) {
        Column(modifier = Modifier.padding(bottom = 28.dp)) {
            SheetTitle("请选择时间", onDismiss)
            CalendarGrid(
                month = state.calendarMonth,
                selectedDate = state.date,
                onChangeMonth = onChangeMonth,
                onDateSelected = onDateSelected,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteEditorSheet(
    note: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .padding(horizontal = 22.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "‹",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.clickable(onClick = onDismiss),
                )
                Text(
                    text = "佬凤日记",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(32.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = note,
                onValueChange = onValueChange,
                placeholder = { Text("输入备注内容") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                maxLines = 2,
            )
            Text(
                text = "${note.length}/30",
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(52.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(166.dp),
                enabled = note.length <= 30,
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            ) {
                Text("确定")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecordDetailSheet(
    record: BillRecordEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 12.dp),
        ) {
            SheetTitle("账单详情", onDismiss)
            DetailLine(label = "类型", value = record.type.label())
            DetailLine(label = "分类", value = DefaultCategories.nameOf(record.category))
            DetailLine(label = "金额", value = signedCentsText(record))
            DetailLine(label = "日期", value = record.dateTimeText())
            DetailLine(label = "备注", value = record.note.ifBlank { "无" })
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("编辑账单")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE95B5B)),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("删除账单")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            text = { Text("确认删除账单？") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE95B5B)),
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("取消")
                }
            },
        )
    }
}

@Composable
internal fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.width(72.dp),
            color = MutedText,
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.SemiBold,
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Divider),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MonthPickerDialog(
    selectedMonth: YearMonth,
    onDismiss: () -> Unit,
    onChangeMonth: (Long) -> Unit,
    onConfirm: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .padding(horizontal = 18.dp),
        ) {
            Text(
                text = "选择月份",
                modifier = Modifier.padding(top = 16.dp, bottom = 14.dp),
                fontWeight = FontWeight.SemiBold,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Divider),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .padding(horizontal = 28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF7F7F7)),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MonthWheelColumn(
                        items = (1980..2150).map { "${it}年" },
                        selectedIndex = (selectedMonth.year - 1980).coerceIn(0, 170),
                        onSelectedIndex = { index ->
                            val selectedYear = 1980 + index
                            onChangeMonth((selectedYear - selectedMonth.year) * 12L)
                        },
                        modifier = Modifier.weight(1f),
                    )
                    MonthWheelColumn(
                        items = (1..12).map { "${it}月" },
                        selectedIndex = selectedMonth.monthValue - 1,
                        onSelectedIndex = { index ->
                            val selectedMonthValue = index + 1
                            onChangeMonth((selectedMonthValue - selectedMonth.monthValue).toLong())
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 26.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .width(96.dp)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF2F2F2),
                        contentColor = Color.Black,
                    ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("取消")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .width(96.dp)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10C76F)),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("确定")
                }
            }
        }
    }
}

@Composable
internal fun MonthWheelColumn(
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemHeight = 38.dp
    val wheelHeight = itemHeight * 5
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex.coerceIn(items.indices))
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(selectedIndex, items.size) {
        val target = selectedIndex.coerceIn(items.indices)
        if (!listState.isScrollInProgress && centeredWheelIndex(listState) != target) {
            listState.scrollToItem(target)
        }
    }

    LaunchedEffect(listState, items.size) {
        snapshotFlow { listState.isScrollInProgress }.collect { scrolling ->
            if (!scrolling) {
                val centeredIndex = centeredWheelIndex(listState).coerceIn(items.indices)
                if (centeredIndex != selectedIndex) {
                    onSelectedIndex(centeredIndex)
                }
            }
        }
    }

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        modifier = modifier.height(wheelHeight),
        contentPadding = PaddingValues(vertical = itemHeight * 2),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(items.size) { index ->
            val distance = abs(index - centeredWheelIndex(listState)).coerceAtMost(2)
            MonthWheelItem(
                text = items[index],
                alpha = when (distance) {
                    0 -> 1f
                    1 -> 0.42f
                    else -> 0.18f
                },
                scale = when (distance) {
                    0 -> 1.08f
                    1 -> 0.88f
                    else -> 0.78f
                },
                fontWeight = if (distance == 0) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

private fun centeredWheelIndex(state: LazyListState): Int {
    val layoutInfo = state.layoutInfo
    val visibleItems = layoutInfo.visibleItemsInfo
    if (visibleItems.isEmpty()) return state.firstVisibleItemIndex
    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
    return visibleItems.minByOrNull { item ->
        abs((item.offset + item.size / 2) - viewportCenter)
    }?.index ?: state.firstVisibleItemIndex
}

@Composable
internal fun MonthWheelItem(
    text: String,
    alpha: Float,
    scale: Float,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Text(
        text = text,
        modifier = Modifier
            .height(38.dp)
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .wrapContentHeight(Alignment.CenterVertically),
        color = Color(0xFF5F6368),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 17.sp),
        fontWeight = fontWeight,
    )
}

@Composable
internal fun CalendarGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    onChangeMonth: (Long) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { onChangeMonth(-1) }) { Text("上月") }
            Text(
                text = "${month.year}年${month.monthValue}月",
                fontWeight = FontWeight.SemiBold,
            )
            TextButton(onClick = { onChangeMonth(1) }) { Text("下月") }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = MutedText,
                )
            }
        }
        val first = month.atDay(1)
        val blanks = first.dayOfWeek.value % 7
        val days = (1..month.lengthOfMonth()).map { month.atDay(it) }
        val cells: List<LocalDate?> = List(blanks) { null } + days
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    val selected = date == selectedDate
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (selected) BrandGreen else Color(0xFFF9F9F9))
                            .clickable(enabled = date != null) {
                                date?.let(onDateSelected)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = date?.dayOfMonth?.toString().orEmpty(),
                            color = if (selected) Color.White else Color.Black,
                        )
                    }
                }
                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
internal fun CategoryGrid(
    categories: List<BillCategory>,
    selected: Int?,
    onSelected: (Int) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        categories.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEach { category ->
                    val isSelected = selected == category.id
                    Text(
                        text = category.name,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) SoftGreen else Color(0xFFFAFAFA))
                            .clickable { onSelected(category.id) }
                            .wrapContentHeight(),
                        textAlign = TextAlign.Center,
                        color = if (isSelected) BrandGreen else Color.Black,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
internal fun SheetTitle(title: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
    ) {
        Text(
            text = "×",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(onClick = onDismiss)
                .padding(horizontal = 18.dp),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
internal fun SheetCloseOnly(onDismiss: () -> Unit) {
    Text(
        text = "×",
        modifier = Modifier
            .clickable(onClick = onDismiss)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        style = MaterialTheme.typography.headlineSmall,
    )
}
