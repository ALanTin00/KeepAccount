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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.keepaccount.R

@Composable
internal fun BottomNavigation(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
) {
    val selectedColor = Color(0xFF008A4E)
    val unselectedColor = Color(0xFFB0B7C3)
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = selectedColor,
        selectedTextColor = selectedColor,
        unselectedIconColor = unselectedColor,
        unselectedTextColor = unselectedColor,
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
                    tint = if (selectedTab == AppTab.LEDGER) selectedColor else unselectedColor,
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
                    tint = if (selectedTab == AppTab.STATISTICS) selectedColor else unselectedColor,
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
                    tint = if (selectedTab == AppTab.SETTINGS) selectedColor else unselectedColor,
                )
            },
            label = { Text("\u8bbe\u7f6e") },
            colors = itemColors,
        )
    }
}

@Composable
private fun NavigationIcon(@DrawableRes resId: Int, tint: Color) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = Modifier.size(28.dp),
        colorFilter = ColorFilter.tint(tint),
    )
}
