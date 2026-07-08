package com.example.keepaccount.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.keepaccount.R
import com.example.keepaccount.data.DefaultCategories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TypeFilterSheet(
    selectedCategory: Int?,
    onDismiss: () -> Unit,
    onCategorySelected: (Int?) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        ) {
            SheetTitle(stringResource(R.string.select_type), onDismiss)
            Text(
                text = stringResource(R.string.all_types),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (selectedCategory == null) BrandGreen else Color(0xFFF7F7F7))
                    .clickable { onCategorySelected(null) }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                color = if (selectedCategory == null) Color.White else Color.Black,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.bill_type_expense),
                color = MutedText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            CategoryGrid(
                categories = DefaultCategories.expense,
                selected = selectedCategory,
                onSelected = { onCategorySelected(it) },
            )
            Text(
                text = stringResource(R.string.bill_type_income),
                color = MutedText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            CategoryGrid(
                categories = DefaultCategories.income,
                selected = selectedCategory,
                onSelected = { onCategorySelected(it) },
            )
        }
    }
}
