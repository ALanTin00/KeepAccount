package com.example.keepaccount.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
internal fun SettingsPage(
    state: LedgerUiState,
    onExportDatabaseData: () -> Unit,
    onImportDatabaseData: () -> Unit,
    onRegenerateSeedData: () -> Unit,
) {
    val context = LocalContext.current
    var pendingAction by remember { mutableStateOf<SettingsDangerAction?>(null) }
    var pendingStorageAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            pendingStorageAction?.invoke()
        }
        pendingStorageAction = null
    }
    fun runWithDownloadPermission(action: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        ) {
            action()
        } else {
            pendingStorageAction = action
            storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text("\u8bbe\u7f6e", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("备份目录", fontWeight = FontWeight.SemiBold)
            Text(
                text = state.backupDirectoryPath,
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Text("备份文件：${state.backupFileName}", color = MutedText, style = MaterialTheme.typography.bodySmall)
            Text(
                text = "操作指引：点击“生成数据库数据”会把备份文件保存到 Download/KeepAccount；换手机时把 keep_account_backup.json 放到新手机同一目录，再点击“读取数据库数据”导入。Android 10 及以上无需权限，Android 9 及以下会申请存储权限；导入完成后页面会自动刷新，无需重启 App。",
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { runWithDownloadPermission(onExportDatabaseData) },
            enabled = !state.isBackupWorking,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(if (state.isBackupWorking) "处理中..." else "生成数据库数据")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { runWithDownloadPermission(onImportDatabaseData) },
            enabled = !state.isBackupWorking,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("读取数据库数据")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { pendingAction = SettingsDangerAction.REGENERATE_SEED },
            enabled = !state.isBackupWorking,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("重新生成 2024/2025 测试数据")
        }
        state.settingsMessage?.let { message ->
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = message,
                color = BrandGreen,
                textAlign = TextAlign.Center,
            )
        }
    }

    pendingAction?.let { action ->
        AlertDialog(
            onDismissRequest = { pendingAction = null },
            title = { Text(action.title) },
            text = { Text(action.message) },
            confirmButton = {
                Button(
                    onClick = {
                        pendingAction = null
                        onRegenerateSeedData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) {
                    Text("取消")
                }
            },
        )
    }
}

internal enum class SettingsDangerAction(
    val title: String,
    val message: String,
) {
    REGENERATE_SEED(
        title = "重新生成测试数据？",
        message = "此操作会先清空当前账单，再写入 2024 和 2025 两年的测试数据。",
    ),
}
