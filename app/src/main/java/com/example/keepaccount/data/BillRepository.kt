package com.example.keepaccount.data

import kotlinx.coroutines.flow.Flow

class BillRepository(
    private val dao: BillRecordDao,
) {
    fun observeRecordsForMonth(
        startMillis: Long,
        endMillis: Long,
        category: String?,
    ): Flow<List<BillRecordEntity>> =
        dao.observeRecordsForMonthAndCategory(startMillis, endMillis, category)

    suspend fun getRecordsPageForMonth(
        startMillis: Long,
        endMillis: Long,
        category: String?,
        limit: Int,
        offset: Int,
    ): List<BillRecordEntity> =
        dao.getRecordsPageForMonthAndCategory(startMillis, endMillis, category, limit, offset)

    fun observeRecordsBetween(
        startMillis: Long,
        endMillis: Long,
    ): Flow<List<BillRecordEntity>> =
        dao.observeRecordsBetween(startMillis, endMillis)

    suspend fun addRecord(
        type: BillType,
        category: String,
        amountCents: Long,
        note: String,
        occurredAt: Long,
    ) {
        dao.insert(
            BillRecordEntity(
                id = 0,
                type = type,
                category = category,
                amountCents = amountCents,
                note = note,
                occurredAt = occurredAt,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun deleteRecord(id: Long) {
        dao.deleteById(id)
    }

    suspend fun deleteAllRecords() {
        dao.deleteAll()
    }

    suspend fun updateRecord(record: BillRecordEntity) {
        dao.update(record)
    }

    suspend fun addRecords(records: List<BillRecordEntity>) {
        dao.insertAll(records)
    }

    suspend fun countRecordsBetween(startMillis: Long, endMillis: Long): Int =
        dao.countBetween(startMillis, endMillis)
}
