package com.example.keepaccount

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.keepaccount.data.AppDatabase
import com.example.keepaccount.data.BillRecordEntity
import com.example.keepaccount.data.BillRepository
import com.example.keepaccount.data.BillType
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BillRepositoryInstrumentedTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: BillRepository
    private val zoneId: ZoneId = ZoneId.systemDefault()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = BillRepository(database.billRecordDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun savedRecords_canBeReadAgainFromRoom() = runBlocking {
        repository.addRecord(
            type = BillType.EXPENSE,
            category = 1,
            amountCents = 1_200,
            note = "午餐",
            occurredAt = millis(LocalDate.of(2025, 6, 8), 12, 0),
        )

        val records = repository.observeRecordsForMonth(
            startMillis = monthStart(YearMonth.of(2025, 6)),
            endMillis = monthEnd(YearMonth.of(2025, 6)),
            category = null,
        ).first()

        assertEquals(1, records.size)
        assertEquals(1, records.single().category)
        assertEquals(1_200, records.single().amountCents)
    }

    @Test
    fun monthAndCategoryFilter_returnOnlyMatchingRecords() = runBlocking {
        repository.addRecords(
            listOf(
                record(category = 1, occurredAt = millis(LocalDate.of(2025, 6, 1), 9, 0)),
                record(category = 2, occurredAt = millis(LocalDate.of(2025, 6, 2), 9, 0)),
                record(category = 1, occurredAt = millis(LocalDate.of(2025, 7, 1), 9, 0)),
            ),
        )

        val records = repository.observeRecordsForMonth(
            startMillis = monthStart(YearMonth.of(2025, 6)),
            endMillis = monthEnd(YearMonth.of(2025, 6)),
            category = 1,
        ).first()

        assertEquals(1, records.size)
        assertEquals(1, records.single().category)
        assertEquals(LocalDate.of(2025, 6, 1), records.single().localDateForTest())
    }

    @Test
    fun pagedQuery_loadsTwentyRecordsPerPageInNewestOrder() = runBlocking {
        val records = (1..25).map { day ->
            record(
                id = 0,
                amountCents = day * 100L,
                occurredAt = millis(LocalDate.of(2025, 6, day), 10, 0),
            )
        }
        repository.addRecords(records)

        val firstPage = repository.getRecordsPageForMonth(
            startMillis = monthStart(YearMonth.of(2025, 6)),
            endMillis = monthEnd(YearMonth.of(2025, 6)),
            category = null,
            limit = 20,
            offset = 0,
        )
        val secondPage = repository.getRecordsPageForMonth(
            startMillis = monthStart(YearMonth.of(2025, 6)),
            endMillis = monthEnd(YearMonth.of(2025, 6)),
            category = null,
            limit = 20,
            offset = 20,
        )

        assertEquals(20, firstPage.size)
        assertEquals(5, secondPage.size)
        assertEquals(LocalDate.of(2025, 6, 25), firstPage.first().localDateForTest())
        assertEquals(LocalDate.of(2025, 6, 6), firstPage.last().localDateForTest())
        assertEquals(LocalDate.of(2025, 6, 5), secondPage.first().localDateForTest())
    }

    private fun record(
        id: Long = 0,
        type: BillType = BillType.EXPENSE,
        category: Int = 1,
        amountCents: Long = 1_000,
        occurredAt: Long,
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

    private fun monthStart(month: YearMonth): Long =
        month.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()

    private fun monthEnd(month: YearMonth): Long =
        month.plusMonths(1).atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()

    private fun millis(date: LocalDate, hour: Int, minute: Int): Long =
        date.atTime(LocalTime.of(hour, minute)).atZone(zoneId).toInstant().toEpochMilli()

    private fun BillRecordEntity.localDateForTest(): LocalDate =
        java.time.Instant.ofEpochMilli(occurredAt).atZone(zoneId).toLocalDate()
}
