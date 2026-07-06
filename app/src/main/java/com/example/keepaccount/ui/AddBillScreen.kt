package com.example.keepaccount.ui

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.keepaccount.data.BillCategory
import com.example.keepaccount.data.BillType
import com.example.keepaccount.data.DefaultCategories
import com.example.keepaccount.data.label
import java.time.format.DateTimeFormatter

@Composable
internal fun AddBillPage(
    state: AddBillState,
    onDismiss: () -> Unit,
    onTypeSelected: (BillType) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onAppendAmount: (String) -> Unit,
    onDeleteAmount: () -> Unit,
    onSave: () -> Unit,
    onShowDatePicker: () -> Unit,
    onShowNoteEditor: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(bottom = 12.dp),
    ) {
        AddBillFormContent(
            state = state,
            onDismiss = onDismiss,
            onTypeSelected = onTypeSelected,
            onCategorySelected = onCategorySelected,
            onAppendAmount = onAppendAmount,
            onDeleteAmount = onDeleteAmount,
            onSave = onSave,
            onShowDatePicker = onShowDatePicker,
            onShowNoteEditor = onShowNoteEditor,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddBillSheet(
    state: AddBillState,
    onDismiss: () -> Unit,
    onTypeSelected: (BillType) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onAppendAmount: (String) -> Unit,
    onDeleteAmount: () -> Unit,
    onSave: () -> Unit,
    onShowDatePicker: () -> Unit,
    onShowNoteEditor: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        ) {
            AddBillFormContent(
                state = state,
                onDismiss = onDismiss,
                onTypeSelected = onTypeSelected,
                onCategorySelected = onCategorySelected,
                onAppendAmount = onAppendAmount,
                onDeleteAmount = onDeleteAmount,
                onSave = onSave,
                onShowDatePicker = onShowDatePicker,
                onShowNoteEditor = onShowNoteEditor,
            )
        }
    }
}

@Composable
internal fun AddBillFormContent(
    state: AddBillState,
    onDismiss: () -> Unit,
    onTypeSelected: (BillType) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onAppendAmount: (String) -> Unit,
    onDeleteAmount: () -> Unit,
    onSave: () -> Unit,
    onShowDatePicker: () -> Unit,
    onShowNoteEditor: () -> Unit,
) {
    SheetCloseOnly(onDismiss)
    Text(
        text = if (state.editingRecordId == null) "记一笔" else "编辑账单",
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold,
    )
    Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(BillType.EXPENSE, BillType.INCOME, BillType.EXCLUDED).forEach { type ->
            ModeSegment(
                text = type.label(),
                selected = state.type == type,
                onClick = { onTypeSelected(type) },
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = state.date.format(DateTimeFormatter.ofPattern("M月d日")) + " ▾",
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF5F5F5))
                .clickable(onClick = onShowDatePicker)
                .padding(horizontal = 8.dp, vertical = 7.dp),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
    Text(
        text = "¥ ${state.amountInput.ifBlank { "0" }}",
        modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
    )
    val categories = when (state.type) {
        BillType.INCOME -> DefaultCategories.income
        BillType.EXPENSE, BillType.EXCLUDED -> DefaultCategories.expense
    }
    IconCategoryGrid(
        categories = categories,
        selected = state.category,
        onSelected = onCategorySelected,
    )
    Text(
        text = if (state.note.isBlank()) "添加备注" else "备注：${state.note}",
        color = BrandGreen,
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .clickable(onClick = onShowNoteEditor),
    )
    state.errorMessage?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 18.dp),
        )
    }
    val isAmountValid = state.amountInput.toDoubleOrNull()?.let { it > 0.0 } == true
    NumberPad(
        onAppend = onAppendAmount,
        onDelete = onDeleteAmount,
        onConfirm = onSave,
        confirmEnabled = isAmountValid,
        confirmText = if (state.editingRecordId == null) "确定" else "保存",
    )
}
@Composable
internal fun ModeSegment(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (selected) BrandGreen else MutedText,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) SoftGreen else Color(0xFFF7F7F7))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Clip,
    )
}

@Composable
internal fun IconCategoryGrid(
    categories: List<BillCategory>,
    selected: Int,
    onSelected: (Int) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        categories.chunked(6).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                rowItems.forEach { category ->
                    val isSelected = selected == category.id
                    Column(
                        modifier = Modifier
                            .width(52.dp)
                            .clickable { onSelected(category.id) }
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CategoryIcon(
                            category = category.id,
                            contentAlpha = if (isSelected) 1f else 0.42f,
                            modifier = Modifier
                                .size(34.dp),
                        )
                        Text(
                            text = category.name,
                            color = if (isSelected) BrandGreen else MutedText,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                repeat(6 - rowItems.size) {
                    Spacer(modifier = Modifier.width(52.dp))
                }
            }
        }
    }
}

@Composable
internal fun NumberPad(
    onAppend: (String) -> Unit,
    onDelete: () -> Unit,
    onConfirm: () -> Unit,
    confirmEnabled: Boolean = true,
    confirmText: String = "确定",
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 8.dp),
    ) {
        Column(modifier = Modifier.weight(3f)) {
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { key ->
                        NumberKey(
                            text = key,
                            modifier = Modifier.weight(1f),
                            onClick = { onAppend(key) },
                        )
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                NumberKey(
                    text = "0",
                    modifier = Modifier.weight(2f),
                    onClick = { onAppend("0") },
                )
                NumberKey(
                    text = ".",
                    modifier = Modifier.weight(1f),
                    onClick = { onAppend(".") },
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            NumberKey(
                text = "⌫",
                modifier = Modifier.fillMaxWidth(),
                onClick = onDelete,
            )
            Button(
                onClick = onConfirm,
                enabled = confirmEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(174.dp)
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = Color.White,
                    disabledContainerColor = ConfirmDisabledGray,
                    disabledContentColor = Color.White,
                ),
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(confirmText)
            }
        }
    }
}
@Composable
internal fun NumberKey(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(54.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFFBFBFB))
            .clickable(enabled = text.isNotBlank(), onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = MaterialTheme.typography.titleLarge)
    }
}
