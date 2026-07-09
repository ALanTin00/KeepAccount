package com.example.keepaccount.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            onClick = onExportDatabaseData,
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
            onClick = onImportDatabaseData,
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
