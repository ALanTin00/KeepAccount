package com.example.keepaccount.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.keepaccount.R
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.BillType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
internal fun localizedBillTypeLabel(type: BillType): String =
    stringResource(type.stringResId())

internal fun Context.localizedBillTypeLabel(type: BillType): String =
    getString(type.stringResId())

@Composable
internal fun localizedCategoryName(id: Int): String =
    stringResource(categoryStringResId(id))

internal fun Context.localizedCategoryName(id: Int): String =
    getString(categoryStringResId(id))

@Composable
internal fun BillRecordEntity.localizedDateTimeText(): String {
    val context = LocalContext.current
    return context.localizedDateTimeText(occurredAt)
}

internal fun Context.localizedDateTimeText(epochMillis: Long): String {
    val dateTime = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault())
    val time = dateTime.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
    return getString(
        R.string.format_month_day_time,
        dateTime.monthValue,
        dateTime.dayOfMonth,
        time,
    )
}

@Composable
internal fun LocalDate.localizedDayTitle(): String {
    val context = LocalContext.current
    val today = LocalDate.now()
    val relative = when (this) {
        today -> context.getString(R.string.relative_today)
        today.minusDays(1) -> context.getString(R.string.relative_yesterday)
        else -> ""
    }.let { if (it.isBlank()) "" else " $it" }
    val weekday = context.getString(weekdayStringResId(dayOfWeek.value))
    return context.getString(R.string.format_day_title, monthValue, dayOfMonth, relative, weekday)
}

internal fun BillType.stringResId(): Int = when (this) {
    BillType.EXPENSE -> R.string.bill_type_expense
    BillType.INCOME -> R.string.bill_type_income
    BillType.EXCLUDED -> R.string.bill_type_excluded
}

internal fun categoryStringResId(id: Int): Int = when (id) {
    1 -> R.string.category_dining
    2 -> R.string.category_traffic
    3 -> R.string.category_shopping
    4 -> R.string.category_utilities
    5 -> R.string.category_medical
    6 -> R.string.category_clothing
    7 -> R.string.category_entertainment
    8 -> R.string.category_service
    9 -> R.string.category_education
    10 -> R.string.category_sports
    11 -> R.string.category_travel
    12 -> R.string.category_pets
    13 -> R.string.category_insurance
    14 -> R.string.category_charity
    101 -> R.string.category_salary
    102 -> R.string.category_bonus
    103 -> R.string.category_refund
    104 -> R.string.category_investment
    105 -> R.string.category_other
    else -> R.string.category_unknown
}

private fun weekdayStringResId(value: Int): Int = when (value) {
    1 -> R.string.weekday_monday
    2 -> R.string.weekday_tuesday
    3 -> R.string.weekday_wednesday
    4 -> R.string.weekday_thursday
    5 -> R.string.weekday_friday
    6 -> R.string.weekday_saturday
    else -> R.string.weekday_sunday
}
