package com.example.keepaccount.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepaccount.R

@Composable
fun BeautyActivityContent(
    onFinish: () -> Unit,
    viewModel: LedgerViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    SetStatusBarColor(Color.White)
    BackHandler(onBack = onFinish)

    CompositionLocalProvider(LocalCategoryIconTheme provides state.categoryIconTheme) {
        BeautyPage(
            state = state,
            onBack = onFinish,
            onSelectTheme = viewModel::selectCategoryIconTheme,
        )
    }
}

@Composable
internal fun BeautyPage(
    state: LedgerUiState,
    onBack: () -> Unit,
    onSelectTheme: (CategoryIconTheme) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "‹",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
            )
            Text(
                text = stringResource(R.string.beauty_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.width(40.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        CategoryIconThemeSwitcher(
            selectedTheme = state.categoryIconTheme,
            onSelectTheme = onSelectTheme,
        )
        state.settingsMessage?.let { message ->
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = message,
                color = BrandGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CategoryIconThemeSwitcher(
    selectedTheme: CategoryIconTheme,
    onSelectTheme: (CategoryIconTheme) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(stringResource(R.string.beauty_category_icons), fontWeight = FontWeight.SemiBold)
        CategoryIconTheme.values().forEach { theme ->
            CategoryIconThemeButton(
                theme = theme,
                selected = selectedTheme == theme,
                onClick = { onSelectTheme(theme) },
            )
        }
    }
}

@Composable
private fun CategoryIconThemeButton(
    theme: CategoryIconTheme,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) BrandGreen else Color(0xFFF4F4F4),
            contentColor = if (selected) Color.White else BrandGreen,
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(theme.previewIconResId),
                contentDescription = null,
                modifier = Modifier.size(34.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.common_switch),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
            )
            if (selected) {
                Text(stringResource(R.string.common_selected), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
