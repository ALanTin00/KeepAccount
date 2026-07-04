package com.example.keepaccount.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
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

    @Query("SELECT COUNT(*) FROM bill_records WHERE occurredAt >= :startMillis AND occurredAt < :endMillis")
    suspend fun countBetween(startMillis: Long, endMillis: Long): Int

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
            AND (:category IS NULL OR category = :category)
        ORDER BY occurredAt DESC, id DESC
        LIMIT :limit OFFSET :offset
        """,
    )
    suspend fun getRecordsPageForMonthAndCategory(
        startMillis: Long,
        endMillis: Long,
        category: Int?,
        limit: Int,
        offset: Int,
    ): List<BillRecordEntity>

    @Query(
        """
        SELECT * FROM bill_records
        WHERE occurredAt >= :startMillis AND occurredAt < :endMillis
        ORDER BY occurredAt ASC, id ASC
        """,
    )
    fun observeRecordsBetween(startMillis: Long, endMillis: Long): Flow<List<BillRecordEntity>>
}
