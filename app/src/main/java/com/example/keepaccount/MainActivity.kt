package com.example.keepaccount

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.keepaccount.data.BillBackupManager
import com.example.keepaccount.ui.LedgerApp
import com.example.keepaccount.ui.theme.KeepAccountTheme

class MainActivity : LocalizedComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BillBackupManager(this).ensureBackupDirectoryExists()
        enableEdgeToEdge()
        setContent {
            KeepAccountTheme {
                LedgerApp(onMoveTaskToBack = { moveTaskToBack(true) })
            }
        }
    }
}
