package com.example.keepaccount.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.keepaccount.R

@Composable
internal fun BottomNavigation(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedTextColor = Color(0xFF155D38),
        unselectedTextColor = Color(0xFF6B7280),
        indicatorColor = Color.Transparent,
    )

    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = selectedTab == AppTab.LEDGER,
            onClick = { onTabSelected(AppTab.LEDGER) },
            icon = {
                NavigationIcon(
                    resId = if (selectedTab == AppTab.LEDGER) {
                        R.drawable.nav_ledger_selected
                    } else {
                        R.drawable.nav_ledger_unselected
                    },
                )
            },
            label = { Text("\u660e\u7ec6") },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = selectedTab == AppTab.STATISTICS,
            onClick = { onTabSelected(AppTab.STATISTICS) },
            icon = {
                NavigationIcon(
                    resId = if (selectedTab == AppTab.STATISTICS) {
                        R.drawable.nav_statistics_selected
                    } else {
                        R.drawable.nav_statistics_unselected
                    },
                )
            },
            label = { Text("\u7edf\u8ba1") },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = selectedTab == AppTab.SETTINGS,
            onClick = { onTabSelected(AppTab.SETTINGS) },
            icon = {
                NavigationIcon(
                    resId = if (selectedTab == AppTab.SETTINGS) {
                        R.drawable.nav_settings_selected
                    } else {
                        R.drawable.nav_settings_unselected
                    },
                )
            },
            label = { Text("\u8bbe\u7f6e") },
            colors = itemColors,
        )
    }
}

@Composable
private fun NavigationIcon(@DrawableRes resId: Int) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = Modifier.size(28.dp),
    )
}
