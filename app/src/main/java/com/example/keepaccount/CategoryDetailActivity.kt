package com.example.keepaccount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.keepaccount.data.BillType
import com.example.keepaccount.ui.CategoryDetailActivityContent
import com.example.keepaccount.ui.theme.KeepAccountTheme
import java.time.YearMonth

class CategoryDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialMonth = YearMonth.of(
            intent.getIntExtra(EXTRA_YEAR, YearMonth.now().year),
            intent.getIntExtra(EXTRA_MONTH, YearMonth.now().monthValue).coerceIn(1, 12),
        )
        val category = intent.getIntExtra(EXTRA_CATEGORY, 0)
        val type = runCatching {
            BillType.valueOf(intent.getStringExtra(EXTRA_TYPE).orEmpty())
        }.getOrDefault(BillType.EXPENSE)

        setContent {
            KeepAccountTheme {
                CategoryDetailActivityContent(
                    category = category,
                    type = type,
                    month = initialMonth,
                    onFinish = ::finish,
                )
            }
        }
    }

    companion object {
        private const val EXTRA_CATEGORY = "extra_category"
        private const val EXTRA_TYPE = "extra_type"
        private const val EXTRA_YEAR = "extra_year"
        private const val EXTRA_MONTH = "extra_month"

        fun createIntent(
            context: Context,
            category: Int,
            type: BillType,
            month: YearMonth,
        ): Intent =
            Intent(context, CategoryDetailActivity::class.java)
                .putExtra(EXTRA_CATEGORY, category)
                .putExtra(EXTRA_TYPE, type.name)
                .putExtra(EXTRA_YEAR, month.year)
                .putExtra(EXTRA_MONTH, month.monthValue)
    }
}
