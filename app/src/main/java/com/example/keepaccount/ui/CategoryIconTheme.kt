package com.example.keepaccount.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.compositionLocalOf
import com.example.keepaccount.R
import com.example.keepaccount.data.DefaultCategories

enum class CategoryIconTheme(
    val preferenceValue: String,
    @DrawableRes val previewIconResId: Int,
) {
    ROLE(
        preferenceValue = "role",
        previewIconResId = R.drawable.category_role_dining,
    ),
    ACTION(
        preferenceValue = "action",
        previewIconResId = R.drawable.category_action_dining,
    ),
    FOOD(
        preferenceValue = "food",
        previewIconResId = R.drawable.category_food_dining,
    );

    companion object {
        val default: CategoryIconTheme = ROLE

        fun fromPreference(value: String?): CategoryIconTheme =
            values().firstOrNull { it.preferenceValue == value } ?: default
    }
}

internal val LocalCategoryIconTheme = compositionLocalOf { CategoryIconTheme.default }

@DrawableRes
internal fun categoryIconResId(
    category: Int?,
    theme: CategoryIconTheme,
): Int? = when (theme) {
    CategoryIconTheme.ROLE -> when (category) {
        DefaultCategories.UNKNOWN_ID -> R.drawable.category_role_dining
        1 -> R.drawable.category_role_dining
        2 -> R.drawable.category_role_traffic
        3 -> R.drawable.category_role_shopping
        4 -> R.drawable.category_role_utilities
        5 -> R.drawable.category_role_medical
        6 -> R.drawable.category_role_clothing
        7 -> R.drawable.category_role_entertainment
        8 -> R.drawable.category_role_service
        9 -> R.drawable.category_role_education
        10 -> R.drawable.category_role_sports
        11 -> R.drawable.category_role_travel
        12 -> R.drawable.category_role_pets
        13 -> R.drawable.category_role_insurance
        14 -> R.drawable.category_role_charity
        101 -> R.drawable.category_role_salary
        102 -> R.drawable.category_role_bonus
        103 -> R.drawable.category_role_refund
        104 -> R.drawable.category_role_investment
        105 -> R.drawable.category_role_other
        else -> null
    }

    CategoryIconTheme.ACTION -> when (category) {
        DefaultCategories.UNKNOWN_ID -> R.drawable.category_action_dining
        1 -> R.drawable.category_action_dining
        2 -> R.drawable.category_action_traffic
        3 -> R.drawable.category_action_shopping
        4 -> R.drawable.category_action_utilities
        5 -> R.drawable.category_action_medical
        6 -> R.drawable.category_action_clothing
        7 -> R.drawable.category_action_entertainment
        8 -> R.drawable.category_action_service
        9 -> R.drawable.category_action_education
        10 -> R.drawable.category_action_sports
        11 -> R.drawable.category_action_travel
        12 -> R.drawable.category_action_pets
        13 -> R.drawable.category_action_insurance
        14 -> R.drawable.category_action_charity
        101 -> R.drawable.category_action_salary
        102 -> R.drawable.category_action_bonus
        103 -> R.drawable.category_action_refund
        104 -> R.drawable.category_action_investment
        105 -> R.drawable.category_action_other
        else -> null
    }

    CategoryIconTheme.FOOD -> when (category) {
        DefaultCategories.UNKNOWN_ID -> R.drawable.category_food_dining
        1 -> R.drawable.category_food_dining
        2 -> R.drawable.category_food_traffic
        3 -> R.drawable.category_food_shopping
        4 -> R.drawable.category_food_utilities
        5 -> R.drawable.category_food_medical
        6 -> R.drawable.category_food_clothing
        7 -> R.drawable.category_food_entertainment
        8 -> R.drawable.category_food_service
        9 -> R.drawable.category_food_education
        10 -> R.drawable.category_food_sports
        11 -> R.drawable.category_food_travel
        12 -> R.drawable.category_food_pets
        13 -> R.drawable.category_food_insurance
        14 -> R.drawable.category_food_charity
        101 -> R.drawable.category_food_salary
        102 -> R.drawable.category_food_bonus
        103 -> R.drawable.category_food_refund
        104 -> R.drawable.category_food_investment
        105 -> R.drawable.category_food_other
        else -> null
    }
}