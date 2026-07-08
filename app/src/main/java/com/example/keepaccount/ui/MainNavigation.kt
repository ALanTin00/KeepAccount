package com.example.keepaccount.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keepaccount.R

@Composable
internal fun BottomNavigation(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
) {
    val selectedColor = Color(0xFF008A4E)
    val unselectedColor = Color(0xFFB0B7C3)

    Column(
        modifier = Modifier
            .background(Color.White)
            .navigationBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE8E8E8)),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomNavigationItem(
                selected = selectedTab == AppTab.LEDGER,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                selectedIcon = R.drawable.nav_ledger_selected,
                unselectedIcon = R.drawable.nav_ledger_unselected,
                label = stringResource(R.string.tab_ledger),
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(AppTab.LEDGER) },
            )
            BottomNavigationItem(
                selected = selectedTab == AppTab.STATISTICS,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                selectedIcon = R.drawable.nav_statistics_selected,
                unselectedIcon = R.drawable.nav_statistics_unselected,
                label = stringResource(R.string.tab_statistics),
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(AppTab.STATISTICS) },
            )
            BottomNavigationItem(
                selected = selectedTab == AppTab.SETTINGS,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                selectedIcon = R.drawable.nav_settings_selected,
                unselectedIcon = R.drawable.nav_settings_unselected,
                label = stringResource(R.string.tab_settings),
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(AppTab.SETTINGS) },
            )
        }
    }
}

@Composable
private fun BottomNavigationItem(
    selected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    @DrawableRes selectedIcon: Int,
    @DrawableRes unselectedIcon: Int,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val color = if (selected) selectedColor else unselectedColor
    Column(
        modifier = modifier
            .height(60.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        NavigationIcon(
            resId = if (selected) selectedIcon else unselectedIcon,
            tint = color,
        )
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            lineHeight = 12.sp,
        )
    }
}

@Composable
private fun NavigationIcon(@DrawableRes resId: Int, tint: Color) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        colorFilter = ColorFilter.tint(tint),
    )
}