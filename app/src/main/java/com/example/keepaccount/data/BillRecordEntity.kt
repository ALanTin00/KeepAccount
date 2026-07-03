package com.example.keepaccount.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bill_records")
data class BillRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: BillType,
    val category: String,
    val amountCents: Long,
    val note: String,
    val occurredAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
)
