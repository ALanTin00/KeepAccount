package com.example.keepaccount.data

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object SeedDataFactory {
    fun createRecordsFor2024And2025(zoneId: ZoneId = ZoneId.systemDefault()): List<BillRecordEntity> {
        val expenseCategories = DefaultCategories.expense
        val incomeCategories = DefaultCategories.income
        val records = mutableListOf<BillRecordEntity>()
        var date = LocalDate.of(2024, 1, 1)
        val end = LocalDate.of(2025, 12, 31)
        var dayIndex = 0

        while (!date.isAfter(end)) {
            val primaryExpense = expenseCategories[dayIndex % expenseCategories.size]
            val secondaryExpense = expenseCategories[(dayIndex * 3 + 5) % expenseCategories.size]

            records += seedRecord(
                type = BillType.EXPENSE,
                category = primaryExpense.id,
                amountCents = expenseAmount(dayIndex, 0),
                note = "日常${primaryExpense.name}",
                date = date,
                time = LocalTime.of(8 + dayIndex % 12, 10 + dayIndex % 45),
                zoneId = zoneId,
            )

            records += seedRecord(
                type = BillType.EXPENSE,
                category = secondaryExpense.id,
                amountCents = expenseAmount(dayIndex, 1),
                note = "补充${secondaryExpense.name}",
                date = date,
                time = LocalTime.of(18 + dayIndex % 4, 5 + dayIndex % 50),
                zoneId = zoneId,
            )

            if (date.dayOfMonth == 5) {
                val category = incomeCategories[dayIndex % incomeCategories.size]
                records += seedRecord(
                    type = BillType.INCOME,
                    category = category.id,
                    amountCents = incomeAmount(dayIndex),
                    note = "月度${category.name}",
                    date = date,
                    time = LocalTime.of(9, 30),
                    zoneId = zoneId,
                )
            }

            if (dayIndex % 19 == 0) {
                records += seedRecord(
                    type = BillType.EXCLUDED,
                    category = expenseCategories[(dayIndex + 2) % expenseCategories.size].id,
                    amountCents = 1200L + (dayIndex % 7) * 350L,
                    note = "不计入收支",
                    date = date,
                    time = LocalTime.of(12, 0),
                    zoneId = zoneId,
                )
            }

            date = date.plusDays(1)
            dayIndex++
        }

        return records
    }

    private fun seedRecord(
        type: BillType,
        category: Int,
        amountCents: Long,
        note: String,
        date: LocalDate,
        time: LocalTime,
        zoneId: ZoneId,
    ): BillRecordEntity {
        val occurredAt = date.atTime(time).atZone(zoneId).toInstant().toEpochMilli()
        return BillRecordEntity(
            id = 0,
            type = type,
            category = category,
            amountCents = amountCents,
            note = note,
            occurredAt = occurredAt,
            createdAt = occurredAt,
            updatedAt = occurredAt,
        )
    }

    private fun expenseAmount(dayIndex: Int, variant: Int): Long {
        val base = 600L + ((dayIndex * 37 + variant * 53) % 9600)
        return base + if (variant == 0) 0L else 350L
    }

    private fun incomeAmount(dayIndex: Int): Long {
        return 380000L + (dayIndex % 12) * 45000L
    }
}
