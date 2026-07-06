package com.example.keepaccount.ui

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun BottomNavigation(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = selectedTab == AppTab.LEDGER,
            onClick = { onTabSelected(AppTab.LEDGER) },
            icon = { Text("▣") },
            label = { Text("明细") },
        )
        NavigationBarItem(
            selected = selectedTab == AppTab.STATISTICS,
            onClick = { onTabSelected(AppTab.STATISTICS) },
            icon = { Text("◔") },
            label = { Text("统计") },
        )
        NavigationBarItem(
            selected = selectedTab == AppTab.SETTINGS,
            onClick = { onTabSelected(AppTab.SETTINGS) },
            icon = { Text("⚙") },
            label = { Text("设置") },
        )
    }
}
