package com.example.keepaccount.data

data class BillCategory(
    val id: Int,
    val name: String,
    val type: BillType,
)

object DefaultCategories {
    const val UNKNOWN_ID = 0

    val expense = listOf(
        BillCategory(1, "餐饮", BillType.EXPENSE),
        BillCategory(2, "交通", BillType.EXPENSE),
        BillCategory(3, "购物", BillType.EXPENSE),
        BillCategory(4, "生活缴费", BillType.EXPENSE),
        BillCategory(5, "医疗", BillType.EXPENSE),
        BillCategory(6, "服饰", BillType.EXPENSE),
        BillCategory(7, "娱乐", BillType.EXPENSE),
        BillCategory(8, "服务", BillType.EXPENSE),
        BillCategory(9, "教育", BillType.EXPENSE),
        BillCategory(10, "运动", BillType.EXPENSE),
        BillCategory(11, "旅行", BillType.EXPENSE),
        BillCategory(12, "宠物", BillType.EXPENSE),
        BillCategory(13, "保险", BillType.EXPENSE),
        BillCategory(14, "公益", BillType.EXPENSE),
    )

    val income = listOf(
        BillCategory(101, "工资", BillType.INCOME),
        BillCategory(102, "奖金", BillType.INCOME),
        BillCategory(103, "退款", BillType.INCOME),
        BillCategory(104, "投资", BillType.INCOME),
        BillCategory(105, "其他", BillType.INCOME),
    )

    val all = expense + income
    private val byId = all.associateBy { it.id }
    private val byName = all.associateBy { it.name }

    fun nameOf(id: Int): String = byId[id]?.name ?: "未知分类"

    fun idOf(name: String): Int = byName[name]?.id ?: UNKNOWN_ID

    fun categoryOf(id: Int): BillCategory? = byId[id]
}
