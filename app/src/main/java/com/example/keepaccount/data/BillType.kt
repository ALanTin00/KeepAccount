package com.example.keepaccount.data

enum class BillType {
    EXPENSE,
    INCOME,
    EXCLUDED,
}

fun BillType.label(): String = when (this) {
    BillType.EXPENSE -> "支出"
    BillType.INCOME -> "入账"
    BillType.EXCLUDED -> "不计入收支"
}
