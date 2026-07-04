package com.example.keepaccount.ui

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.view.WindowCompat
import com.example.keepaccount.AddBillActivity
import com.example.keepaccount.CategoryDetailActivity
import com.example.keepaccount.R
import com.example.keepaccount.data.BillCategory
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.BillType
import com.example.keepaccount.data.DefaultCategories
import com.example.keepaccount.data.label
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.max

private val BrandGreen = Color(0xFF40BF78)
private val DarkGreen = Color(0xFF155D38)
private val PageBg = Color(0xFFF3F4F3)
private val SoftGreen = Color(0xFFE6F6EE)
private val MutedText = Color(0xFF8C8C8C)
private val Divider = Color(0xFFE9E9E9)
private val ConfirmDisabledGray = Color(0xFFE0E0E0)

@Composable
private fun SetStatusBarColor(color: Color) {
    val context = LocalContext.current
    val view = LocalView.current
    if (view.isInEditMode) {
        return
    }

    val activity = context as? Activity ?: return
    DisposableEffect(activity, view, color) {
        val window = activity.window
        val previousStatusBarColor = window.statusBarColor
        val controller = WindowCompat.getInsetsController(window, view)
        val previousLightStatusBars = controller.isAppearanceLightStatusBars

        window.statusBarColor = color.toArgb()
        controller.isAppearanceLightStatusBars = color.luminance() > 0.5f

        onDispose {
            window.statusBarColor = previousStatusBarColor
            controller.isAppearanceLightStatusBars = previousLightStatusBars
        }
    }
}

@Composable
fun LedgerApp(
    viewModel: LedgerViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshLedgerRecords()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    KeepAccountScreen(
        state = state,
        onTabSelected = viewModel::selectTab,
        onShowTypeFilter = viewModel::showTypeFilter,
        onDismissTypeFilter = viewModel::dismissTypeFilter,
        onCategoryFilterSelected = viewModel::selectCategoryFilter,
        onShowMonthPicker = viewModel::showMonthPicker,
        onDismissMonthPicker = viewModel::dismissMonthPicker,
        onChangeTempMonth = viewModel::changeTempMonth,
        onConfirmMonthPicker = viewModel::confirmMonthPicker,
        onOpenAddBill = { context.startActivity(AddBillActivity.createIntent(context, state.selectedMonth)) },
        onCloseAddBill = viewModel::closeAddBill,
        onUpdateAddBillType = viewModel::updateAddBillType,
        onUpdateAddBillCategory = viewModel::updateAddBillCategory,
        onAppendAmount = viewModel::appendAmountInput,
        onDeleteAmount = viewModel::deleteAmountInput,
        onSaveAddBill = viewModel::saveAddBill,
        onShowDatePicker = viewModel::showDatePicker,
        onDismissDatePicker = viewModel::dismissDatePicker,
        onChangeAddBillMonth = viewModel::changeAddBillMonth,
        onSelectAddBillDate = viewModel::selectAddBillDate,
        onShowNoteEditor = viewModel::showNoteEditor,
        onUpdateNoteDraft = viewModel::updateNoteDraft,
        onConfirmNote = viewModel::confirmNote,
        onDismissNoteEditor = viewModel::dismissNoteEditor,
        onSwitchStatisticsMode = viewModel::switchStatisticsMode,
        onOpenCategoryDetail = { category ->
            context.startActivity(
                CategoryDetailActivity.createIntent(
                    context = context,
                    category = category,
                    type = state.statisticsMode,
                    month = state.statisticsMonth,
                ),
            )
        },
        onCloseCategoryDetail = viewModel::closeCategoryDetail,
        onSetCategoryDetailSort = viewModel::setCategoryDetailSort,
        onOpenRecordDetail = viewModel::openRecordDetail,
        onCloseRecordDetail = viewModel::closeRecordDetail,
        onDeleteRecord = viewModel::deleteSelectedRecord,
        onEditRecord = viewModel::editSelectedRecord,
        onLoadMoreLedger = viewModel::loadMoreLedgerRecords,
        onRegenerateSeedData = viewModel::regenerateSeedData,
    )
}

@Composable
fun AddBillActivityContent(
    initialMonth: YearMonth,
    onFinish: () -> Unit,
    viewModel: LedgerViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    SetStatusBarColor(Color.White)

    LaunchedEffect(initialMonth) {
        viewModel.openAddBillForMonth(initialMonth)
    }

    BackHandler(onBack = onFinish)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        state.addBillState?.let { addState ->
            AddBillPage(
                state = addState,
                onDismiss = onFinish,
                onTypeSelected = viewModel::updateAddBillType,
                onCategorySelected = viewModel::updateAddBillCategory,
                onAppendAmount = viewModel::appendAmountInput,
                onDeleteAmount = viewModel::deleteAmountInput,
                onSave = { viewModel.saveAddBill(onSaved = onFinish) },
                onShowDatePicker = viewModel::showDatePicker,
                onShowNoteEditor = viewModel::showNoteEditor,
            )
            if (addState.isDatePickerVisible) {
                DatePickerSheet(
                    state = addState,
                    onDismiss = viewModel::dismissDatePicker,
                    onChangeMonth = viewModel::changeAddBillMonth,
                    onDateSelected = viewModel::selectAddBillDate,
                )
            }
            if (addState.isNoteEditorVisible) {
                NoteEditorSheet(
                    note = addState.noteDraft,
                    onValueChange = viewModel::updateNoteDraft,
                    onDismiss = viewModel::dismissNoteEditor,
                    onConfirm = viewModel::confirmNote,
                )
            }
        }
    }
}

@Composable
fun CategoryDetailActivityContent(
    category: Int,
    type: BillType,
    month: YearMonth,
    onFinish: () -> Unit,
    viewModel: LedgerViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val detail = state.categoryDetail ?: CategoryDetailState(category = category)
    SetStatusBarColor(Color.White)

    LaunchedEffect(category, type, month) {
        viewModel.openCategoryDetailForMonth(
            category = category,
            type = type,
            month = month,
        )
    }

    BackHandler(onBack = onFinish)

    CategoryDetailPage(
        state = state,
        detail = detail,
        onBack = onFinish,
        onSortSelected = viewModel::setCategoryDetailSort,
    )
}

@Composable
private fun KeepAccountScreen(
    state: LedgerUiState,
    onTabSelected: (AppTab) -> Unit,
    onShowTypeFilter: () -> Unit,
    onDismissTypeFilter: () -> Unit,
    onCategoryFilterSelected: (Int?) -> Unit,
    onShowMonthPicker: (MonthPickerTarget) -> Unit,
    onDismissMonthPicker: () -> Unit,
    onChangeTempMonth: (Long) -> Unit,
    onConfirmMonthPicker: () -> Unit,
    onOpenAddBill: () -> Unit,
    onCloseAddBill: () -> Unit,
    onUpdateAddBillType: (BillType) -> Unit,
    onUpdateAddBillCategory: (Int) -> Unit,
    onAppendAmount: (String) -> Unit,
    onDeleteAmount: () -> Unit,
    onSaveAddBill: () -> Unit,
    onShowDatePicker: () -> Unit,
    onDismissDatePicker: () -> Unit,
    onChangeAddBillMonth: (Long) -> Unit,
    onSelectAddBillDate: (LocalDate) -> Unit,
    onShowNoteEditor: () -> Unit,
    onUpdateNoteDraft: (String) -> Unit,
    onConfirmNote: () -> Unit,
    onDismissNoteEditor: () -> Unit,
    onSwitchStatisticsMode: (BillType) -> Unit,
    onOpenCategoryDetail: (Int) -> Unit,
    onCloseCategoryDetail: () -> Unit,
    onSetCategoryDetailSort: (DetailSort) -> Unit,
    onOpenRecordDetail: (BillRecordEntity) -> Unit,
    onCloseRecordDetail: () -> Unit,
    onDeleteRecord: () -> Unit,
    onEditRecord: () -> Unit,
    onLoadMoreLedger: () -> Unit,
    onRegenerateSeedData: () -> Unit,
) {
    val detail = state.categoryDetail
    val statusBarColor = when {
        detail != null -> Color.White
        state.selectedTab == AppTab.SETTINGS -> Color.White
        else -> BrandGreen
    }
    SetStatusBarColor(statusBarColor)

    Scaffold(
        containerColor = statusBarColor,
        bottomBar = {
            if (detail == null) {
                BottomNavigation(
                    selectedTab = state.selectedTab,
                    onTabSelected = onTabSelected,
                )
            }
        },
        floatingActionButton = {
            if (state.selectedTab == AppTab.LEDGER && detail == null) {
                FloatingActionButton(
                    onClick = onOpenAddBill,
                    containerColor = Color.White,
                    contentColor = BrandGreen,
                    shape = RoundedCornerShape(28.dp),
                ) {
                    Text(
                        text = "记一笔",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (detail != null) {
                CategoryDetailPage(
                    state = state,
                    detail = detail,
                    onBack = onCloseCategoryDetail,
                    onSortSelected = onSetCategoryDetailSort,
                )
            } else {
                when (state.selectedTab) {
                    AppTab.LEDGER -> LedgerPage(
                        state = state,
                        onShowTypeFilter = onShowTypeFilter,
                        onShowMonthPicker = { onShowMonthPicker(MonthPickerTarget.LEDGER) },
                        onRecordClick = onOpenRecordDetail,
                        onLoadMore = onLoadMoreLedger,
                    )

                    AppTab.STATISTICS -> StatisticsPage(
                        state = state,
                        onShowMonthPicker = { onShowMonthPicker(MonthPickerTarget.STATISTICS) },
                        onSwitchMode = onSwitchStatisticsMode,
                        onOpenCategoryDetail = onOpenCategoryDetail,
                    )

                    AppTab.SETTINGS -> SettingsPage(
                        message = state.settingsMessage,
                        onRegenerateSeedData = onRegenerateSeedData,
                    )
                }
            }
        }
    }

    if (state.isTypeFilterVisible) {
        TypeFilterSheet(
            selectedCategory = state.selectedCategory,
            onDismiss = onDismissTypeFilter,
            onCategorySelected = onCategoryFilterSelected,
        )
    }

    if (state.monthPickerTarget != null) {
        MonthPickerDialog(
            selectedMonth = state.tempYearMonth,
            onDismiss = onDismissMonthPicker,
            onChangeMonth = onChangeTempMonth,
            onConfirm = onConfirmMonthPicker,
        )
    }

    state.addBillState?.let { addState ->
        AddBillSheet(
            state = addState,
            onDismiss = onCloseAddBill,
            onTypeSelected = onUpdateAddBillType,
            onCategorySelected = onUpdateAddBillCategory,
            onAppendAmount = onAppendAmount,
            onDeleteAmount = onDeleteAmount,
            onSave = onSaveAddBill,
            onShowDatePicker = onShowDatePicker,
            onShowNoteEditor = onShowNoteEditor,
        )
        if (addState.isDatePickerVisible) {
            DatePickerSheet(
                state = addState,
                onDismiss = onDismissDatePicker,
                onChangeMonth = onChangeAddBillMonth,
                onDateSelected = onSelectAddBillDate,
            )
        }
        if (addState.isNoteEditorVisible) {
            NoteEditorSheet(
                note = addState.noteDraft,
                onValueChange = onUpdateNoteDraft,
                onDismiss = onDismissNoteEditor,
                onConfirm = onConfirmNote,
            )
        }
    }

    state.recordDetail?.let { record ->
        RecordDetailSheet(
            record = record,
            onDismiss = onCloseRecordDetail,
            onEdit = onEditRecord,
            onDelete = onDeleteRecord,
        )
    }
}

@Composable
private fun LedgerPage(
    state: LedgerUiState,
    onShowTypeFilter: () -> Unit,
    onShowMonthPicker: () -> Unit,
    onRecordClick: (BillRecordEntity) -> Unit,
    onLoadMore: () -> Unit,
) {
    val allRecords = state.ledgerAllRecords
    val groups = state.ledgerRecords.groupsByDay()
    val categoryLabel = state.selectedCategory?.let(DefaultCategories::nameOf) ?: "全部类型"
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore, state.hasMoreLedgerRecords, state.isLedgerLoadingMore) {
        if (shouldLoadMore && state.hasMoreLedgerRecords && !state.isLedgerLoadingMore) {
            onLoadMore()
        }
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
                title = "暂无账单",
                subtitle = "点击右下角记一笔，开始记录今天的收支",
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
                item {
                    Text(
                        text = when {
                            state.isLedgerLoadingMore -> "加载中..."
                            state.hasMoreLedgerRecords -> "继续上拉加载"
                            else -> "没有更多了"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        color = MutedText,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun LedgerHeader(
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
                text = "记账本",
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
                    text = "${month.year}年${month.monthValue}月",
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
                text = "总支出${centsText(totalExpense)}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "总入账${centsText(totalIncome)}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun MiniProgramCapsule(modifier: Modifier = Modifier) {
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
private fun FilterButton(label: String, onClick: () -> Unit) {
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
private fun DayGroupCard(
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
                    text = group.date.dayTitle(),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "出 ${"%.2f".format(group.expenseCents / 100.0)}",
                    color = Color(0xFF8D5A2B),
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "入 ${"%.2f".format(group.incomeCents / 100.0)}",
                    color = Color(0xFF1E6C9C),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            group.records.forEach { record ->
                BillRecordRow(
                    record = record,
                    onClick = { onRecordClick(record) },
                )
            }
        }
    }
}

@Composable
private fun BillRecordRow(
    record: BillRecordEntity,
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
                text = DefaultCategories.nameOf(record.category),
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 70.dp)
            .background(Divider),
    )
}

@Composable
private fun CategoryIcon(
    category: Int? = null,
    modifier: Modifier = Modifier,
    contentAlpha: Float = 1f,
) {
    val iconResId = categoryIconResId(category)
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

@DrawableRes
private fun categoryIconResId(category: Int?): Int? = when (category) {
    DefaultCategories.UNKNOWN_ID -> R.drawable.category_superhero
    1 -> R.drawable.category_superhero
    2 -> R.drawable.category_astronaut
    3 -> R.drawable.category_firefighter
    4 -> R.drawable.category_spaceman
    5 -> R.drawable.category_lab_technician
    6 -> R.drawable.category_ninja
    7 -> R.drawable.category_cowboy
    8 -> R.drawable.category_magician
    9 -> R.drawable.category_wizard
    10 -> R.drawable.category_musician
    11 -> R.drawable.category_scientist
    12 -> R.drawable.category_sailor
    13 -> R.drawable.category_pirate
    14 -> R.drawable.category_pilot
    101 -> R.drawable.category_superhero
    102 -> R.drawable.category_magician
    103 -> R.drawable.category_wizard
    104 -> R.drawable.category_scientist
    105 -> R.drawable.category_cowboy
    else -> null
}

@Composable
private fun StatisticsPage(
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
private fun StatisticsHeader(
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
                text = "${month.year}年${month.monthValue}月  ▣",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onShowMonthPicker),
            )
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
private fun ModeButton(text: String, selected: Boolean, onClick: () -> Unit) {
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
private fun StatisticsSectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 20.dp, top = 28.dp, bottom = 16.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun DonutChart(summaries: List<CategorySummary>, modifier: Modifier = Modifier) {
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
private fun CategorySummaryRow(
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
private fun BarChart(
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

@Composable
private fun CategoryDetailPage(
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
private fun SortChip(text: String, selected: Boolean, onClick: () -> Unit) {
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
private fun DetailRecordRow(record: BillRecordEntity) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeFilterSheet(
    selectedCategory: Int?,
    onDismiss: () -> Unit,
    onCategorySelected: (Int?) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        ) {
            SheetTitle("请选择类型", onDismiss)
            Text(
                text = "全部类型",
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (selectedCategory == null) BrandGreen else Color(0xFFF7F7F7))
                    .clickable { onCategorySelected(null) }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                color = if (selectedCategory == null) Color.White else Color.Black,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "支出",
                color = MutedText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            CategoryGrid(
                categories = DefaultCategories.expense,
                selected = selectedCategory,
                onSelected = { onCategorySelected(it) },
            )
            Text(
                text = "入账",
                color = MutedText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            CategoryGrid(
                categories = DefaultCategories.income,
                selected = selectedCategory,
                onSelected = { onCategorySelected(it) },
            )
        }
    }
}

@Composable
private fun AddBillPage(
    state: AddBillState,
    onDismiss: () -> Unit,
    onTypeSelected: (BillType) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onAppendAmount: (String) -> Unit,
    onDeleteAmount: () -> Unit,
    onSave: () -> Unit,
    onShowDatePicker: () -> Unit,
    onShowNoteEditor: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(bottom = 12.dp),
    ) {
        AddBillFormContent(
            state = state,
            onDismiss = onDismiss,
            onTypeSelected = onTypeSelected,
            onCategorySelected = onCategorySelected,
            onAppendAmount = onAppendAmount,
            onDeleteAmount = onDeleteAmount,
            onSave = onSave,
            onShowDatePicker = onShowDatePicker,
            onShowNoteEditor = onShowNoteEditor,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBillSheet(
    state: AddBillState,
    onDismiss: () -> Unit,
    onTypeSelected: (BillType) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onAppendAmount: (String) -> Unit,
    onDeleteAmount: () -> Unit,
    onSave: () -> Unit,
    onShowDatePicker: () -> Unit,
    onShowNoteEditor: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        ) {
            AddBillFormContent(
                state = state,
                onDismiss = onDismiss,
                onTypeSelected = onTypeSelected,
                onCategorySelected = onCategorySelected,
                onAppendAmount = onAppendAmount,
                onDeleteAmount = onDeleteAmount,
                onSave = onSave,
                onShowDatePicker = onShowDatePicker,
                onShowNoteEditor = onShowNoteEditor,
            )
        }
    }
}

@Composable
private fun AddBillFormContent(
    state: AddBillState,
    onDismiss: () -> Unit,
    onTypeSelected: (BillType) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onAppendAmount: (String) -> Unit,
    onDeleteAmount: () -> Unit,
    onSave: () -> Unit,
    onShowDatePicker: () -> Unit,
    onShowNoteEditor: () -> Unit,
) {
    SheetCloseOnly(onDismiss)
    Text(
        text = if (state.editingRecordId == null) "记一笔" else "编辑账单",
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold,
    )
    Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(BillType.EXPENSE, BillType.INCOME, BillType.EXCLUDED).forEach { type ->
            ModeSegment(
                text = type.label(),
                selected = state.type == type,
                onClick = { onTypeSelected(type) },
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = state.date.format(DateTimeFormatter.ofPattern("M月d日")) + " ▾",
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF5F5F5))
                .clickable(onClick = onShowDatePicker)
                .padding(horizontal = 8.dp, vertical = 7.dp),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
    Text(
        text = "¥ ${state.amountInput.ifBlank { "0" }}",
        modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
    )
    val categories = when (state.type) {
        BillType.INCOME -> DefaultCategories.income
        BillType.EXPENSE, BillType.EXCLUDED -> DefaultCategories.expense
    }
    IconCategoryGrid(
        categories = categories,
        selected = state.category,
        onSelected = onCategorySelected,
    )
    Text(
        text = if (state.note.isBlank()) "添加备注" else "备注：${state.note}",
        color = BrandGreen,
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .clickable(onClick = onShowNoteEditor),
    )
    state.errorMessage?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 18.dp),
        )
    }
    val isAmountValid = state.amountInput.toDoubleOrNull()?.let { it > 0.0 } == true
    NumberPad(
        onAppend = onAppendAmount,
        onDelete = onDeleteAmount,
        onConfirm = onSave,
        confirmEnabled = isAmountValid,
        confirmText = if (state.editingRecordId == null) "确定" else "保存",
    )
}
@Composable
private fun ModeSegment(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (selected) BrandGreen else MutedText,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) SoftGreen else Color(0xFFF7F7F7))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Clip,
    )
}

@Composable
private fun IconCategoryGrid(
    categories: List<BillCategory>,
    selected: Int,
    onSelected: (Int) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        categories.chunked(6).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                rowItems.forEach { category ->
                    val isSelected = selected == category.id
                    Column(
                        modifier = Modifier
                            .width(52.dp)
                            .clickable { onSelected(category.id) }
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CategoryIcon(
                            category = category.id,
                            contentAlpha = if (isSelected) 1f else 0.42f,
                            modifier = Modifier
                                .size(34.dp),
                        )
                        Text(
                            text = category.name,
                            color = if (isSelected) BrandGreen else MutedText,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                repeat(6 - rowItems.size) {
                    Spacer(modifier = Modifier.width(52.dp))
                }
            }
        }
    }
}

@Composable
private fun NumberPad(
    onAppend: (String) -> Unit,
    onDelete: () -> Unit,
    onConfirm: () -> Unit,
    confirmEnabled: Boolean = true,
    confirmText: String = "确定",
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 8.dp),
    ) {
        Column(modifier = Modifier.weight(3f)) {
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { key ->
                        NumberKey(
                            text = key,
                            modifier = Modifier.weight(1f),
                            onClick = { onAppend(key) },
                        )
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                NumberKey(
                    text = "0",
                    modifier = Modifier.weight(2f),
                    onClick = { onAppend("0") },
                )
                NumberKey(
                    text = ".",
                    modifier = Modifier.weight(1f),
                    onClick = { onAppend(".") },
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            NumberKey(
                text = "⌫",
                modifier = Modifier.fillMaxWidth(),
                onClick = onDelete,
            )
            Button(
                onClick = onConfirm,
                enabled = confirmEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(174.dp)
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = Color.White,
                    disabledContainerColor = ConfirmDisabledGray,
                    disabledContentColor = Color.White,
                ),
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(confirmText)
            }
        }
    }
}
@Composable
private fun NumberKey(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(54.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFFBFBFB))
            .clickable(enabled = text.isNotBlank(), onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = MaterialTheme.typography.titleLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerSheet(
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
private fun NoteEditorSheet(
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
                    text = "添加备注",
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
private fun RecordDetailSheet(
    record: BillRecordEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
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
                onClick = onDelete,
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
}

@Composable
private fun DetailLine(label: String, value: String) {
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
private fun MonthPickerDialog(
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
                    .height(160.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF7F7F7)),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MonthWheelColumn(
                        previousText = "${selectedMonth.minusYears(1).year}年",
                        selectedText = "${selectedMonth.year}年",
                        nextText = "${selectedMonth.plusYears(1).year}年",
                        onPrevious = { onChangeMonth(-12) },
                        onNext = { onChangeMonth(12) },
                        modifier = Modifier.weight(1f),
                    )
                    MonthWheelColumn(
                        previousText = "${selectedMonth.minusMonths(1).monthValue}月",
                        selectedText = "${selectedMonth.monthValue}月",
                        nextText = "${selectedMonth.plusMonths(1).monthValue}月",
                        onPrevious = { onChangeMonth(-1) },
                        onNext = { onChangeMonth(1) },
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
private fun MonthWheelColumn(
    previousText: String,
    selectedText: String,
    nextText: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dragStepPx = 36f
    var dragOffset by remember { mutableStateOf(0f) }

    Column(
        modifier = modifier
            .height(150.dp)
            .pointerInput(onPrevious, onNext) {
                detectVerticalDragGestures(
                    onDragCancel = { dragOffset = 0f },
                    onDragEnd = { dragOffset = 0f },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                        when {
                            dragOffset >= dragStepPx -> {
                                onPrevious()
                                dragOffset = 0f
                            }

                            dragOffset <= -dragStepPx -> {
                                onNext()
                                dragOffset = 0f
                            }
                        }
                    },
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        MonthWheelItem(
            text = previousText,
            selected = false,
            onClick = onPrevious,
        )
        MonthWheelItem(
            text = selectedText,
            selected = true,
            fontWeight = FontWeight.SemiBold,
        )
        MonthWheelItem(
            text = nextText,
            selected = false,
            onClick = onNext,
        )
    }
}

@Composable
private fun MonthWheelItem(
    text: String,
    selected: Boolean,
    fontWeight: FontWeight = FontWeight.Normal,
    onClick: (() -> Unit)? = null,
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.18f else 0.88f,
        animationSpec = tween(durationMillis = 180),
        label = "monthWheelItemScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.35f,
        animationSpec = tween(durationMillis = 180),
        label = "monthWheelItemAlpha",
    )

    Text(
        text = text,
        modifier = Modifier
            .height(46.dp)
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .then(
                if (onClick == null) {
                    Modifier
                } else {
                    Modifier.clickable(onClick = onClick)
                },
            )
            .wrapContentHeight(Alignment.CenterVertically),
        color = if (selected) Color.Black else MutedText,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = fontWeight,
    )
}

@Composable
private fun CalendarGrid(
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
private fun CategoryGrid(
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
private fun SheetTitle(title: String, onDismiss: () -> Unit) {
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
private fun SheetCloseOnly(onDismiss: () -> Unit) {
    Text(
        text = "×",
        modifier = Modifier
            .clickable(onClick = onDismiss)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
private fun BottomNavigation(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = selectedTab == AppTab.LEDGER,
            onClick = { onTabSelected(AppTab.LEDGER) },
            icon = { Text("▣") },
            label = { Text("明细") },
        )
        NavigationBarItem(
            selected = selectedTab == AppTab.STATISTICS,
            onClick = { onTabSelected(AppTab.STATISTICS) },
            icon = { Text("◔") },
            label = { Text("统计") },
        )
        NavigationBarItem(
            selected = selectedTab == AppTab.SETTINGS,
            onClick = { onTabSelected(AppTab.SETTINGS) },
            icon = { Text("⚙") },
            label = { Text("设置") },
        )
    }
}

@Composable
private fun SettingsPage(
    message: String?,
    onRegenerateSeedData: () -> Unit,
) {
    var pendingAction by remember { mutableStateOf<SettingsDangerAction?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(68.dp))
        CategoryIcon(modifier = Modifier.size(54.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("设置", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("管理本地 Room 账单数据", color = MutedText)
        Spacer(modifier = Modifier.height(34.dp))
        Button(
            onClick = { pendingAction = SettingsDangerAction.REGENERATE_SEED },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("重新生成 2024/2025 测试数据")
        }
        if (message != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = message,
                color = BrandGreen,
                textAlign = TextAlign.Center,
            )
        }
    }

    pendingAction?.let { action ->
        AlertDialog(
            onDismissRequest = { pendingAction = null },
            title = { Text(action.title) },
            text = { Text(action.message) },
            confirmButton = {
                Button(
                    onClick = {
                        pendingAction = null
                        onRegenerateSeedData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) {
                    Text("取消")
                }
            },
        )
    }
}

private enum class SettingsDangerAction(
    val title: String,
    val message: String,
) {
    REGENERATE_SEED(
        title = "重新生成测试数据？",
        message = "此操作会先清空当前账单，再写入 2024 和 2025 两年的测试数据。",
    ),
}

@Composable
private fun EmptyState(title: String, subtitle: String, modifier: Modifier = Modifier) {
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

private fun LocalDate.dayTitle(): String {
    val today = LocalDate.now()
    val relative = when (this) {
        today -> " 今天"
        today.minusDays(1) -> " 昨天"
        else -> ""
    }
    val week = listOf("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日")
    return "${monthValue}月${dayOfMonth}日$relative ${week[dayOfWeek.value - 1]}"
}

private fun BillRecordEntity.timeText(): String =
    java.time.Instant.ofEpochMilli(occurredAt)
        .atZone(java.time.ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("HH:mm", Locale.CHINA))

data class ChartPoint(
    val label: String,
    val value: Long,
)

private fun dailyPoints(records: List<BillRecordEntity>, month: YearMonth): List<ChartPoint> {
    val byDay = records.groupBy { it.localDate().dayOfMonth }
    return (1..month.lengthOfMonth()).map { day ->
        ChartPoint(
            label = day.toString(),
            value = byDay[day].orEmpty().sumOf { it.amountCents },
        )
    }
}

private fun monthlyPoints(
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
