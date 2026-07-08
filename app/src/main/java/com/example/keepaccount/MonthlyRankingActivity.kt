package com.example.keepaccount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.keepaccount.data.BillType
import com.example.keepaccount.ui.MonthlyRankingActivityContent
import com.example.keepaccount.ui.theme.KeepAccountTheme
import java.time.YearMonth

class MonthlyRankingActivity : LocalizedComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val now = YearMonth.now()
        val month = YearMonth.of(
            intent.getIntExtra(EXTRA_YEAR, now.year),
            intent.getIntExtra(EXTRA_MONTH, now.monthValue).coerceIn(1, 12),
        )
        val type = runCatching {
            BillType.valueOf(intent.getStringExtra(EXTRA_TYPE).orEmpty())
        }.getOrDefault(BillType.EXPENSE)

        setContent {
            KeepAccountTheme {
                MonthlyRankingActivityContent(
                    month = month,
                    type = type,
                    onFinish = ::finish,
                )
            }
        }
    }

    companion object {
        private const val EXTRA_TYPE = "extra_type"
        private const val EXTRA_YEAR = "extra_year"
        private const val EXTRA_MONTH = "extra_month"

        fun createIntent(
            context: Context,
            month: YearMonth,
            type: BillType,
        ): Intent =
            Intent(context, MonthlyRankingActivity::class.java)
                .putExtra(EXTRA_TYPE, type.name)
                .putExtra(EXTRA_YEAR, month.year)
                .putExtra(EXTRA_MONTH, month.monthValue)
    }
}
