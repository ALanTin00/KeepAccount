package com.example.keepaccount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.keepaccount.ui.AddBillActivityContent
import com.example.keepaccount.ui.theme.KeepAccountTheme
import java.time.YearMonth

class AddBillActivity : LocalizedComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialMonth = YearMonth.of(
            intent.getIntExtra(EXTRA_YEAR, YearMonth.now().year),
            intent.getIntExtra(EXTRA_MONTH, YearMonth.now().monthValue).coerceIn(1, 12),
        )

        setContent {
            KeepAccountTheme {
                AddBillActivityContent(
                    initialMonth = initialMonth,
                    onFinish = ::finish,
                )
            }
        }
    }

    companion object {
        private const val EXTRA_YEAR = "extra_year"
        private const val EXTRA_MONTH = "extra_month"

        fun createIntent(context: Context, month: YearMonth): Intent =
            Intent(context, AddBillActivity::class.java)
                .putExtra(EXTRA_YEAR, month.year)
                .putExtra(EXTRA_MONTH, month.monthValue)
    }
}
