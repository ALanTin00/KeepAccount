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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import com.example.keepaccount.R

@Composable
internal fun SettingsPage(
    state: LedgerUiState,
    onExportDatabaseData: () -> Unit,
    onImportDatabaseData: () -> Unit,
    onOpenBeautyPage: () -> Unit,
    onOpenLanguagePage: () -> Unit,
) {
    val context = LocalContext.current
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
        Text(stringResource(R.string.tab_settings), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.settings_backup_directory), fontWeight = FontWeight.SemiBold)
            Text(
                text = state.backupDirectoryPath,
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(stringResource(R.string.settings_backup_file, state.backupFileName), color = MutedText, style = MaterialTheme.typography.bodySmall)
            Text(
                text = stringResource(R.string.settings_backup_guide),
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onOpenBeautyPage,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(stringResource(R.string.beauty_title))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onOpenLanguagePage,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(stringResource(R.string.language_title))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { runWithDownloadPermission(onExportDatabaseData) },
            enabled = !state.isBackupWorking,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(if (state.isBackupWorking) stringResource(R.string.common_processing) else stringResource(R.string.settings_export_database))
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
            Text(stringResource(R.string.settings_import_database))
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
}
