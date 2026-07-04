package com.example.keepaccount

import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.BillType
import com.example.keepaccount.ui.categorySummaries
import com.example.keepaccount.ui.expenseTotal
import com.example.keepaccount.ui.groupsByDay
import com.example.keepaccount.ui.incomeTotal
import com.example.keepaccount.ui.signedCentsText
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class LedgerCalculationsTest {
    private val zoneId: ZoneId = ZoneId.systemDefault()

    @Test
    fun totals_ignoreExcludedRecords() {
        val records = listOf(
            record(type = BillType.EXPENSE, category = "餐饮", amountCents = 1_200),
            record(type = BillType.INCOME, category = "工资", amountCents = 5_000),
            record(type = BillType.EXCLUDED, category = "购物", amountCents = 9_900),
        )

        assertEquals(1_200, records.expenseTotal())
        assertEquals(5_000, records.incomeTotal())
    }

    @Test
    fun groupsByDay_sortsDaysAndRecordsNewestFirst() {
        val olderDay = LocalDate.of(2025, 5, 1)
        val newerDay = LocalDate.of(2025, 5, 2)
        val records = listOf(
            record(id = 1, occurredAt = millis(olderDay, 20, 0)),
            record(id = 2, occurredAt = millis(newerDay, 8, 0)),
            record(id = 3, occurredAt = millis(newerDay, 21, 0)),
        )

        val groups = records.groupsByDay()

        assertEquals(listOf(newerDay, olderDay), groups.map { it.date })
        assertEquals(listOf(3L, 2L), groups.first().records.map { it.id })
    }

    @Test
    fun categorySummaries_calculatesPercentAndSortsByAmount() {
        val records = listOf(
            record(type = BillType.EXPENSE, category = "餐饮", amountCents = 2_000),
            record(type = BillType.EXPENSE, category = "交通", amountCents = 500),
            record(type = BillType.EXPENSE, category = "餐饮", amountCents = 1_000),
            record(type = BillType.INCOME, category = "工资", amountCents = 8_000),
            record(type = BillType.EXCLUDED, category = "购物", amountCents = 4_000),
        )

        val summaries = records.categorySummaries(BillType.EXPENSE)

        assertEquals("餐饮", summaries[0].category)
        assertEquals(3_000, summaries[0].amountCents)
        assertEquals(3_000f / 3_500f, summaries[0].percent)
        assertEquals("交通", summaries[1].category)
        assertEquals(500, summaries[1].amountCents)
    }

    @Test
    fun signedCentsText_matchesBillType() {
        assertEquals("-12.30", signedCentsText(record(type = BillType.EXPENSE, amountCents = 1_230)))
        assertEquals("+12.30", signedCentsText(record(type = BillType.INCOME, amountCents = 1_230)))
        assertEquals("12.30", signedCentsText(record(type = BillType.EXCLUDED, amountCents = 1_230)))
    }

    private fun record(
        id: Long = 1,
        type: BillType = BillType.EXPENSE,
        category: String = "餐饮",
        amountCents: Long = 1_000,
        occurredAt: Long = millis(LocalDate.of(2025, 1, 1), 12, 0),
    ): BillRecordEntity =
        BillRecordEntity(
            id = id,
            type = type,
            category = category,
            amountCents = amountCents,
            note = "",
            occurredAt = occurredAt,
            createdAt = occurredAt,
            updatedAt = occurredAt,
        )

    private fun millis(date: LocalDate, hour: Int, minute: Int): Long =
        date.atTime(LocalTime.of(hour, minute)).atZone(zoneId).toInstant().toEpochMilli()
}
