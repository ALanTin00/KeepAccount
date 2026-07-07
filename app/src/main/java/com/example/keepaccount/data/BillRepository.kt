package com.example.keepaccount.data

import kotlinx.coroutines.flow.Flow

class BillRepository(
    private val dao: BillRecordDao,
) {
    fun observeRecordsForMonth(
        startMillis: Long,
        endMillis: Long,
        category: Int?,
    ): Flow<List<BillRecordEntity>> =
        dao.observeRecordsForMonthAndCategory(startMillis, endMillis, category)

    fun observeRecordsBetween(
        startMillis: Long,
        endMillis: Long,
    ): Flow<List<BillRecordEntity>> =
        dao.observeRecordsBetween(startMillis, endMillis)

    suspend fun getAllRecords(): List<BillRecordEntity> =
        dao.getAllRecords()

    suspend fun addRecord(
        type: BillType,
        category: Int,
        amountCents: Long,
        note: String,
        occurredAt: Long,
    ) {
        val now = System.currentTimeMillis()
        dao.insert(
            BillRecordEntity(
                id = 0,
                type = type,
                category = category,
                amountCents = amountCents,
                note = note,
                occurredAt = occurredAt,
                createdAt = now,
                updatedAt = now,
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

    suspend fun replaceRecordsBetween(
        startMillis: Long,
        endMillis: Long,
        records: List<BillRecordEntity>,
    ) {
        dao.replaceBetween(startMillis, endMillis, records)
    }

    suspend fun importRecords(records: List<BillRecordEntity>): ImportRecordsResult =
        dao.insertDeduplicated(records)

    suspend fun countRecordsBetween(startMillis: Long, endMillis: Long): Int =
        dao.countBetween(startMillis, endMillis)
}