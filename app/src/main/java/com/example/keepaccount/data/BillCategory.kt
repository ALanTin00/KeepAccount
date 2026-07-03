package com.example.keepaccount.data

data class BillCategory(
    val name: String,
    val type: BillType,
)

object DefaultCategories {
    val expense = listOf(
        "餐饮",
        "交通",
        "购物",
        "生活缴费",
        "医疗",
        "服饰",
        "娱乐",
        "服务",
        "教育",
        "运动",
        "旅行",
        "宠物",
        "保险",
        "公益",
    ).map { BillCategory(it, BillType.EXPENSE) }

    val income = listOf(
        "工资",
        "奖金",
        "退款",
        "投资",
        "其他",
    ).map { BillCategory(it, BillType.INCOME) }

    val all = expense + income
}
