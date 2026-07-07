package com.example.keepaccount.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BillRecordDao {
    @Insert
    suspend fun insert(record: BillRecordEntity): Long

    @Insert
    suspend fun insertAll(records: List<BillRecordEntity>)

    @Update
    suspend fun update(record: BillRecordEntity)

    @Query("DELETE FROM bill_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM bill_records")
    suspend fun deleteAll()

    @Query("DELETE FROM bill_records WHERE occurredAt >= :startMillis AND occurredAt < :endMillis")
    suspend fun deleteBetween(startMillis: Long, endMillis: Long)

    @Query("SELECT COUNT(*) FROM bill_records WHERE occurredAt >= :startMillis AND occurredAt < :endMillis")
    suspend fun countBetween(startMillis: Long, endMillis: Long): Int

    @Query("SELECT * FROM bill_records ORDER BY occurredAt DESC, id DESC")
    suspend fun getAllRecords(): List<BillRecordEntity>

    @Transaction
    suspend fun insertDeduplicated(records: List<BillRecordEntity>): ImportRecordsResult {
        if (records.isEmpty()) {
            return ImportRecordsResult(importedCount = 0, skippedDuplicateCount = 0)
        }

        val existingKeys = getAllRecords().map { it.contentKey() }.toMutableSet()
        val newRecords = records.filter { existingKeys.add(it.contentKey()) }
        if (newRecords.isNotEmpty()) {
            insertAll(newRecords)
        }
        return ImportRecordsResult(
            importedCount = newRecords.size,
            skippedDuplicateCount = records.size - newRecords.size,
        )
    }

    @Transaction
    suspend fun replaceBetween(
        startMillis: Long,
        endMillis: Long,
        records: List<BillRecordEntity>,
    ) {
        deleteBetween(startMillis, endMillis)
        if (records.isNotEmpty()) {
            insertAll(records)
        }
    }

    @Query(
        """
        SELECT * FROM bill_records
        WHERE occurredAt >= :startMillis AND occurredAt < :endMillis
        ORDER BY occurredAt DESC, id DESC
        """,
    )
    fun observeRecordsForMonth(startMillis: Long, endMillis: Long): Flow<List<BillRecordEntity>>

    @Query(
        """
        SELECT * FROM bill_records
        WHERE occurredAt >= :startMillis AND occurredAt < :endMillis
            AND (:category IS NULL OR category = :category)
        ORDER BY occurredAt DESC, id DESC
        """,
    )
    fun observeRecordsForMonthAndCategory(
        startMillis: Long,
        endMillis: Long,
        category: Int?,
    ): Flow<List<BillRecordEntity>>

    @Query(
        """
        SELECT * FROM bill_records
        WHERE occurredAt >= :startMillis AND occurredAt < :endMillis
        ORDER BY occurredAt ASC, id ASC
        """,
    )
    fun observeRecordsBetween(startMillis: Long, endMillis: Long): Flow<List<BillRecordEntity>>
}

data class ImportRecordsResult(
    val importedCount: Int,
    val skippedDuplicateCount: Int,
)

private fun BillRecordEntity.contentKey(): String =
    listOf(type.name, category, amountCents, occurredAt, note, createdAt).joinToString(separator = "|")