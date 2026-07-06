package com.example.keepaccount.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepaccount.AddBillActivity
import com.example.keepaccount.CategoryDetailActivity
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.BillType
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun LedgerApp(
    viewModel: LedgerViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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
        onExportDatabaseData = viewModel::exportDatabaseData,
        onImportDatabaseData = viewModel::importDatabaseData,
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
    onExportDatabaseData: () -> Unit,
    onImportDatabaseData: () -> Unit,
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
                    )

                    AppTab.STATISTICS -> StatisticsPage(
                        state = state,
                        onShowMonthPicker = { onShowMonthPicker(MonthPickerTarget.STATISTICS) },
                        onSwitchMode = onSwitchStatisticsMode,
                        onOpenCategoryDetail = onOpenCategoryDetail,
                    )

                    AppTab.SETTINGS -> SettingsPage(
                        state = state,
                        onExportDatabaseData = onExportDatabaseData,
                        onImportDatabaseData = onImportDatabaseData,
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
