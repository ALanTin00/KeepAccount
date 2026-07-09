package com.example.keepaccount.ui

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepaccount.AppLocaleManager
import com.example.keepaccount.R
import com.example.keepaccount.data.AppDatabase
import com.example.keepaccount.data.BackupException
import com.example.keepaccount.data.BillBackupManager
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.BillRepository
import com.example.keepaccount.data.BillType
import com.example.keepaccount.data.DefaultCategories
import com.example.keepaccount.data.SeedDataFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LedgerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BillRepository(
        AppDatabase.getInstance(application).billRecordDao(),
    )
    private val backupManager = BillBackupManager(application)
    private val zoneId = ZoneId.systemDefault()
    private val preferences = application.getSharedPreferences("keep_account_settings", Context.MODE_PRIVATE)
    private var observeLedgerAllJob: Job? = null
    private var observeStatisticsJob: Job? = null
    private var observeStatisticsRangeJob: Job? = null
    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == KEY_CATEGORY_ICON_THEME) {
            refreshCategoryIconTheme()
        }
    }
    private val _uiState = MutableStateFlow(
        LedgerUiState(
            categoryIconTheme = CategoryIconTheme.fromPreference(
                preferences.getString(KEY_CATEGORY_ICON_THEME, null),
            ),
            backupDirectoryPath = backupManager.backupDirectoryLabel,
            backupFileName = BillBackupManager.BACKUP_FILE_NAME,
        ),
    )
    val uiState: StateFlow<LedgerUiState> = _uiState.asStateFlow()

    init {
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        observeLedgerMonth()
        observeStatisticsMonth()
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectCategoryIconTheme(theme: CategoryIconTheme) {
        preferences.edit()
            .putString(KEY_CATEGORY_ICON_THEME, theme.preferenceValue)
            .apply()
        _uiState.update {
            it.copy(
                categoryIconTheme = theme,
                settingsMessage = localizedString(R.string.settings_category_theme_changed),
            )
        }
    }

    fun refreshCategoryIconTheme() {
        val theme = CategoryIconTheme.fromPreference(
            preferences.getString(KEY_CATEGORY_ICON_THEME, null),
        )
        _uiState.update { state ->
            if (state.categoryIconTheme == theme) {
                state
            } else {
                state.copy(categoryIconTheme = theme)
            }
        }
    }
    fun showTypeFilter() {
        _uiState.update { it.copy(isTypeFilterVisible = true) }
    }

    fun dismissTypeFilter() {
        _uiState.update { it.copy(isTypeFilterVisible = false) }
    }

    fun selectCategoryFilter(category: Int?) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                isTypeFilterVisible = false,
            )
        }
        observeLedgerMonth()
    }

    fun showMonthPicker(target: MonthPickerTarget) {
        _uiState.update {
            it.copy(
                monthPickerTarget = target,
                tempYearMonth = when (target) {
                    MonthPickerTarget.LEDGER -> it.selectedMonth
                    MonthPickerTarget.STATISTICS -> it.statisticsMonth
                },
            )
        }
    }

    fun dismissMonthPicker() {
        _uiState.update { it.copy(monthPickerTarget = null) }
    }

    fun changeTempMonth(delta: Long) {
        _uiState.update { it.copy(tempYearMonth = it.tempYearMonth.plusMonths(delta)) }
    }

    fun confirmMonthPicker() {
        val state = _uiState.value
        _uiState.update {
            when (state.monthPickerTarget) {
                MonthPickerTarget.LEDGER -> it.copy(
                    selectedMonth = state.tempYearMonth,
                    monthPickerTarget = null,
                )

                MonthPickerTarget.STATISTICS -> it.copy(
                    statisticsMonth = state.tempYearMonth,
                    monthPickerTarget = null,
                )

                null -> it.copy(monthPickerTarget = null)
            }
        }
        when (state.monthPickerTarget) {
            MonthPickerTarget.LEDGER -> observeLedgerMonth()
            MonthPickerTarget.STATISTICS -> observeStatisticsMonth()
            null -> Unit
        }
    }

    fun openAddBill() {
        openAddBillForMonth(_uiState.value.selectedMonth)
    }

    fun openAddBillForMonth(month: YearMonth) {
        _uiState.update {
            val today = LocalDate.now()
            it.copy(
                addBillState = AddBillState(
                    date = today,
                    calendarMonth = YearMonth.from(today),
                ),
            )
        }
    }

    fun closeAddBill() {
        _uiState.update { it.copy(addBillState = null) }
    }

    fun updateAddBillType(type: BillType) {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            val defaultCategory = when (type) {
                BillType.EXPENSE, BillType.EXCLUDED -> DefaultCategories.expense.first().id
                BillType.INCOME -> DefaultCategories.income.first().id
            }
            state.copy(
                addBillState = current.copy(
                    type = type,
                    category = defaultCategory,
                ),
            )
        }
    }

    fun updateAddBillCategory(category: Int) {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            state.copy(addBillState = current.copy(category = category))
        }
    }

    fun appendAmountInput(value: String) {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            val next = when {
                value == "." && current.amountInput.contains(".") -> current.amountInput
                value == "." && current.amountInput.isBlank() -> "0."
                current.amountInput == "0" && value != "." -> value
                current.amountInput.contains(".") &&
                    current.amountInput.substringAfter(".").length >= 2 -> current.amountInput
                current.amountInput.length >= 9 -> current.amountInput
                else -> current.amountInput + value
            }
            state.copy(addBillState = current.copy(amountInput = next, errorMessage = null))
        }
    }

    fun deleteAmountInput() {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            state.copy(
                addBillState = current.copy(
                    amountInput = current.amountInput.dropLast(1),
                    errorMessage = null,
                ),
            )
        }
    }

    fun showDatePicker() {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            state.copy(addBillState = current.copy(isDatePickerVisible = true))
        }
    }

    fun dismissDatePicker() {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            state.copy(addBillState = current.copy(isDatePickerVisible = false))
        }
    }

    fun changeAddBillMonth(delta: Long) {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            state.copy(addBillState = current.copy(calendarMonth = current.calendarMonth.plusMonths(delta)))
        }
    }

    fun selectAddBillDate(date: LocalDate) {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            state.copy(
                addBillState = current.copy(
                    date = date,
                    calendarMonth = YearMonth.from(date),
                    isDatePickerVisible = false,
                ),
            )
        }
    }

    fun showNoteEditor() {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            state.copy(addBillState = current.copy(isNoteEditorVisible = true, noteDraft = current.note))
        }
    }

    fun updateNoteDraft(value: String) {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            state.copy(addBillState = current.copy(noteDraft = value.take(30)))
        }
    }

    fun confirmNote() {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            state.copy(
                addBillState = current.copy(
                    note = current.noteDraft,
                    isNoteEditorVisible = false,
                ),
            )
        }
    }

    fun dismissNoteEditor() {
        _uiState.update { state ->
            val current = state.addBillState ?: return@update state
            state.copy(addBillState = current.copy(isNoteEditorVisible = false))
        }
    }

    fun saveAddBill(onSaved: (() -> Unit)? = null) {
        val form = _uiState.value.addBillState ?: return
        val amountCents = parseAmountCents(form.amountInput)
        if (amountCents <= 0) {
            _uiState.update { state ->
                state.copy(addBillState = form.copy(errorMessage = localizedString(R.string.add_bill_invalid_amount)))
            }
            return
        }
        viewModelScope.launch {
            val occurredAt = form.date
                .atTime(LocalTime.now())
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli()
            if (form.editingRecordId == null) {
                repository.addRecord(
                    type = form.type,
                    category = form.category,
                    amountCents = amountCents,
                    note = form.note,
                    occurredAt = occurredAt,
                )
            } else {
                repository.updateRecord(
                    BillRecordEntity(
                        id = form.editingRecordId,
                        type = form.type,
                        category = form.category,
                        amountCents = amountCents,
                        note = form.note,
                        occurredAt = occurredAt,
                        createdAt = form.editingCreatedAt,
                        updatedAt = System.currentTimeMillis(),
                    ),
                )
            }
            _uiState.update { it.copy(addBillState = null) }
            onSaved?.invoke()
        }
    }

    fun switchStatisticsMode(type: BillType) {
        _uiState.update { it.copy(statisticsMode = type) }
    }

    fun openStatisticsMonth(type: BillType, month: YearMonth) {
        _uiState.update {
            it.copy(
                selectedTab = AppTab.STATISTICS,
                statisticsMode = type,
                statisticsMonth = month,
                categoryDetail = null,
            )
        }
        observeStatisticsMonth()
    }

    fun openCategoryDetail(category: Int) {
        _uiState.update {
            it.copy(
                selectedTab = AppTab.STATISTICS,
                categoryDetail = CategoryDetailState(category = category),
            )
        }
    }

    fun openCategoryDetailForMonth(
        category: Int,
        type: BillType,
        month: YearMonth,
    ) {
        _uiState.update {
            it.copy(
                selectedTab = AppTab.STATISTICS,
                statisticsMode = type,
                statisticsMonth = month,
                categoryDetail = CategoryDetailState(category = category),
            )
        }
        observeStatisticsMonth()
    }

    fun closeCategoryDetail() {
        _uiState.update { it.copy(categoryDetail = null) }
    }

    fun setCategoryDetailSort(sort: DetailSort) {
        _uiState.update { state ->
            val detail = state.categoryDetail ?: return@update state
            state.copy(categoryDetail = detail.copy(sort = sort))
        }
    }

    fun openRecordDetail(record: BillRecordEntity) {
        _uiState.update { it.copy(recordDetail = record) }
    }

    fun closeRecordDetail() {
        _uiState.update { it.copy(recordDetail = null) }
    }

    fun editSelectedRecord() {
        val record = _uiState.value.recordDetail ?: return
        val recordDate = record.localDate()
        _uiState.update {
            it.copy(
                recordDetail = null,
                addBillState = AddBillState(
                    editingRecordId = record.id,
                    editingCreatedAt = record.createdAt,
                    type = record.type,
                    category = record.category,
                    amountInput = "%.2f".format(record.amountCents / 100.0),
                    date = recordDate,
                    calendarMonth = YearMonth.from(recordDate),
                    note = record.note,
                    noteDraft = record.note,
                ),
            )
        }
    }

    fun deleteSelectedRecord() {
        val record = _uiState.value.recordDetail ?: return
        viewModelScope.launch {
            repository.deleteRecord(record.id)
            _uiState.update { it.copy(recordDetail = null) }
        }
    }

    fun generate2026FirstHalfData() {
        viewModelScope.launch {
            val start = LocalDate.of(2026, 1, 1).atStartOfDay(zoneId).toInstant().toEpochMilli()
            val end = LocalDate.of(2026, 7, 1).atStartOfDay(zoneId).toInstant().toEpochMilli()
            val records = SeedDataFactory.create2026FirstHalfRecords(zoneId)
            repository.replaceRecordsBetween(start, end, records)
            _uiState.update {
                it.copy(
                    settingsMessage = localizedString(R.string.settings_generated_2026_success, records.size),
                    selectedMonth = YearMonth.of(2026, 6),
                    statisticsMonth = YearMonth.of(2026, 6),
                    selectedCategory = null,
                )
            }
            observeLedgerMonth()
            observeStatisticsMonth()
        }
    }
    fun exportDatabaseData() {
        if (_uiState.value.isBackupWorking) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBackupWorking = true,
                    settingsMessage = localizedString(R.string.settings_exporting_database),
                )
            }
            val message = runCatching {
                val result = backupManager.export(repository.getAllRecords())
                localizedString(R.string.settings_export_database_success, result.recordCount, result.directoryPath, BillBackupManager.BACKUP_FILE_NAME)
            }.getOrElse {
                localizedString(R.string.settings_export_database_failure, it.localizedMessage ?: localizedString(R.string.common_unknown_error))
            }
            _uiState.update {
                it.copy(
                    isBackupWorking = false,
                    settingsMessage = message,
                    backupDirectoryPath = backupManager.backupDirectoryLabel,
                    backupFileName = BillBackupManager.BACKUP_FILE_NAME,
                )
            }
        }
    }

    fun importDatabaseData() {
        if (_uiState.value.isBackupWorking) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBackupWorking = true,
                    settingsMessage = localizedString(R.string.settings_importing_database),
                )
            }
            val message = runCatching {
                val records = backupManager.readRecords()
                val result = repository.importRecords(records)
                observeLedgerMonth()
                observeStatisticsMonth()
                localizedString(R.string.settings_import_database_success, result.importedCount, result.skippedDuplicateCount)
            }.getOrElse {
                val reason = when (it) {
                    is BackupException -> it.message ?: localizedString(R.string.backup_file_invalid)
                    else -> it.localizedMessage ?: localizedString(R.string.common_unknown_error)
                }
                localizedString(R.string.settings_import_database_failure, reason)
            }
            _uiState.update {
                it.copy(
                    isBackupWorking = false,
                    settingsMessage = message,
                    backupDirectoryPath = backupManager.backupDirectoryLabel,
                    backupFileName = BillBackupManager.BACKUP_FILE_NAME,
                )
            }
        }
    }

    fun clearSettingsMessage() {
        _uiState.update { it.copy(settingsMessage = null) }
    }

    private fun observeLedgerMonth() {
        observeLedgerAllJob?.cancel()
        val state = _uiState.value
        val start = state.selectedMonth.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val end = state.selectedMonth.plusMonths(1).atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        observeLedgerAllJob = viewModelScope.launch {
            repository.observeRecordsForMonth(start, end, state.selectedCategory).collectLatest { records ->
                _uiState.update { it.copy(ledgerAllRecords = records) }
            }
        }
    }

    private fun observeStatisticsMonth() {
        observeStatisticsJob?.cancel()
        observeStatisticsRangeJob?.cancel()
        val state = _uiState.value
        val start = state.statisticsMonth.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val end = state.statisticsMonth.plusMonths(1).atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        observeStatisticsJob = viewModelScope.launch {
            repository.observeRecordsForMonth(start, end, null).collectLatest { records ->
                _uiState.update { it.copy(statisticsRecords = records) }
            }
        }
        val rangeStart = state.statisticsMonth.minusMonths(MONTHLY_COMPARISON_MONTH_COUNT - 1L).atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        observeStatisticsRangeJob = viewModelScope.launch {
            repository.observeRecordsBetween(rangeStart, end).collectLatest { records ->
                _uiState.update { it.copy(statisticsRangeRecords = records) }
            }
        }
    }


    private fun localizedString(@StringRes resId: Int, vararg args: Any): String =
        AppLocaleManager.wrap(getApplication<Application>()).getString(resId, *args)
    private fun parseAmountCents(input: String): Long {
        return runCatching {
            BigDecimal(input.ifBlank { "0" })
                .multiply(BigDecimal(100))
                .setScale(0, RoundingMode.HALF_UP)
                .toLong()
        }.getOrDefault(0)
    }

    override fun onCleared() {
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onCleared()
    }
}

data class LedgerUiState(
    val selectedTab: AppTab = AppTab.LEDGER,
    val selectedMonth: YearMonth = YearMonth.now(),
    val statisticsMonth: YearMonth = YearMonth.now(),
    val tempYearMonth: YearMonth = YearMonth.now(),
    val monthPickerTarget: MonthPickerTarget? = null,
    val selectedCategory: Int? = null,
    val isTypeFilterVisible: Boolean = false,
    val ledgerAllRecords: List<BillRecordEntity> = emptyList(),
    val statisticsRecords: List<BillRecordEntity> = emptyList(),
    val statisticsRangeRecords: List<BillRecordEntity> = emptyList(),
    val addBillState: AddBillState? = null,
    val statisticsMode: BillType = BillType.EXPENSE,
    val categoryDetail: CategoryDetailState? = null,
    val recordDetail: BillRecordEntity? = null,
    val settingsMessage: String? = null,
    val categoryIconTheme: CategoryIconTheme = CategoryIconTheme.default,
    val backupDirectoryPath: String = "",
    val backupFileName: String = "",
    val isBackupWorking: Boolean = false,
)

data class AddBillState(
    val editingRecordId: Long? = null,
    val editingCreatedAt: Long = System.currentTimeMillis(),
    val type: BillType = BillType.EXPENSE,
    val category: Int = DefaultCategories.expense.first().id,
    val amountInput: String = "",
    val date: LocalDate = LocalDate.now(),
    val calendarMonth: YearMonth = YearMonth.now(),
    val note: String = "",
    val noteDraft: String = "",
    val errorMessage: String? = null,
    val isDatePickerVisible: Boolean = false,
    val isNoteEditorVisible: Boolean = false,
)

data class CategoryDetailState(
    val category: Int,
    val sort: DetailSort = DetailSort.AMOUNT,
)

enum class AppTab {
    LEDGER,
    STATISTICS,
    SETTINGS,
}

private const val KEY_CATEGORY_ICON_THEME = "category_icon_theme"
private const val MONTHLY_COMPARISON_MONTH_COUNT = 7L

enum class MonthPickerTarget {
    LEDGER,
    STATISTICS,
}

enum class DetailSort {
    AMOUNT,
    TIME,
}

data class DailyGroup(
    val date: LocalDate,
    val records: List<BillRecordEntity>,
    val expenseCents: Long,
    val incomeCents: Long,
)

data class CategorySummary(
    val category: Int,
    val amountCents: Long,
    val percent: Float,
)

fun List<BillRecordEntity>.expenseTotal(): Long =
    filter { it.type == BillType.EXPENSE }.sumOf { it.amountCents }

fun List<BillRecordEntity>.incomeTotal(): Long =
    filter { it.type == BillType.INCOME }.sumOf { it.amountCents }

fun List<BillRecordEntity>.groupsByDay(): List<DailyGroup> =
    groupBy { it.localDate() }
        .toSortedMap(compareByDescending { it })
        .map { (date, records) ->
            DailyGroup(
                date = date,
                records = records.sortedByDescending { it.occurredAt },
                expenseCents = records.expenseTotal(),
                incomeCents = records.incomeTotal(),
            )
        }

fun List<BillRecordEntity>.categorySummaries(type: BillType): List<CategorySummary> {
    val filtered = filter { it.type == type }
    val total = filtered.sumOf { it.amountCents }.takeIf { it > 0 } ?: return emptyList()
    return filtered.groupBy { it.category }
        .map { (category, records) ->
            val amount = records.sumOf { it.amountCents }
            CategorySummary(
                category = category,
                amountCents = amount,
                percent = amount.toFloat() / total.toFloat(),
            )
        }
        .sortedByDescending { it.amountCents }
}

fun BillRecordEntity.localDate(): LocalDate =
    Instant.ofEpochMilli(occurredAt).atZone(ZoneId.systemDefault()).toLocalDate()

fun centsText(cents: Long): String = "¥%.2f".format(cents / 100.0)

fun signedCentsText(record: BillRecordEntity): String {
    val prefix = when (record.type) {
        BillType.EXPENSE -> "-"
        BillType.INCOME -> "+"
        BillType.EXCLUDED -> ""
    }
    return "$prefix${"%.2f".format(record.amountCents / 100.0)}"
}
