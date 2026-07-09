package com.example.keepaccount.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepaccount.R

@Composable
fun ChangePhoneActivityContent(
    onFinish: () -> Unit,
    viewModel: LedgerViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    SetStatusBarColor(PageBg)
    BackHandler(onBack = onFinish)

    ChangePhonePage(
        state = state,
        onBack = onFinish,
        onExportDatabaseData = viewModel::exportDatabaseData,
        onImportDatabaseData = viewModel::importDatabaseData,
    )
}

@Composable
internal fun ChangePhonePage(
    state: LedgerUiState,
    onBack: () -> Unit,
    onExportDatabaseData: () -> Unit,
    onImportDatabaseData: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "\u2039",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(end = 16.dp),
            )
            Text(
                text = stringResource(R.string.change_phone_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color(0xFF17191C),
            )
            Spacer(modifier = Modifier.width(40.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            ChangePhoneIntroCard()
            Spacer(modifier = Modifier.height(16.dp))

            ChangePhoneActionCard(
                badgeText = "1",
                title = stringResource(R.string.change_phone_old_phone_title),
                subtitle = stringResource(R.string.change_phone_old_phone_subtitle),
                buttonText = if (state.isBackupWorking) stringResource(R.string.common_processing) else stringResource(R.string.settings_export_database),
                enabled = !state.isBackupWorking,
                onClick = onExportDatabaseData,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ChangePhoneActionCard(
                badgeText = "2",
                title = stringResource(R.string.change_phone_new_phone_title),
                subtitle = stringResource(R.string.change_phone_new_phone_subtitle),
                buttonText = stringResource(R.string.settings_import_database),
                enabled = !state.isBackupWorking,
                onClick = onImportDatabaseData,
            )

            Spacer(modifier = Modifier.height(14.dp))
            ChangePhoneResultCard(message = state.settingsMessage ?: stringResource(R.string.change_phone_result_placeholder), isPlaceholder = state.settingsMessage == null)

            Spacer(modifier = Modifier.height(18.dp))
            ChangePhoneInfoCard(state = state)
            Spacer(modifier = Modifier.height(18.dp))
            ChangePhoneGuideCard(guide = stringResource(R.string.settings_backup_guide))
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ChangePhoneIntroCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SoftGreen)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.change_phone_intro_title),
            color = DarkGreen,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.change_phone_intro_subtitle),
            color = Color(0xFF537362),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun ChangePhoneActionCard(
    badgeText: String,
    title: String,
    subtitle: String,
    buttonText: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SoftGreen),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = badgeText,
                    color = BrandGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color(0xFF1F2328),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .height(36.dp)
                        .verticalScroll(rememberScrollState()),
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(buttonText, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ChangePhoneResultCard(message: String, isPlaceholder: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SoftGreen)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = if (isPlaceholder) Color(0xFF6B8C79) else BrandGreen,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        )
    }
}

@Composable
private fun ChangePhoneInfoCard(state: LedgerUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.change_phone_file_location_title),
            color = Color(0xFF1F2328),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        ChangePhonePathBlock(
            label = stringResource(R.string.settings_backup_directory),
            value = state.backupDirectoryPath,
        )
        ChangePhonePathBlock(
            label = stringResource(R.string.settings_backup_file, state.backupFileName),
            value = state.backupFileName,
        )
    }
}

@Composable
private fun ChangePhonePathBlock(
    label: String,
    value: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF7F8F7))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            color = MutedText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = value,
            color = Color(0xFF2A2D31),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun ChangePhoneGuideCard(guide: String) {
    val steps = guide.lines().map { it.trim() }.filter { it.isNotEmpty() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.change_phone_guide_title),
            color = Color(0xFF1F2328),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(SoftGreen),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (index + 1).toString(),
                        color = BrandGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = step.removeStepPrefix(index + 1),
                    color = Color(0xFF4C5358),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private fun String.removeStepPrefix(step: Int): String {
    val prefixes = listOf("$step、", "$step.", "$step ")
    return prefixes.fold(this) { text, prefix ->
        if (text.startsWith(prefix)) text.removePrefix(prefix).trim() else text
    }
}