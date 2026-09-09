package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.BillEntity
import com.example.data.model.BillFrequency
import com.example.data.model.ExpenseCategory

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditBillDialog(
    bill: BillEntity?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        title: String,
        amount: Double,
        category: ExpenseCategory,
        frequency: BillFrequency,
        dueDay: Int,
        reminder: Boolean,
        colorHex: Long
    ) -> Unit,
    onDelete: (BillEntity) -> Unit
) {
    val isEdit = bill != null
    var title by remember { mutableStateOf(bill?.title ?: "") }
    var amountString by remember {
        mutableStateOf(bill?.amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(
            runCatching { ExpenseCategory.valueOf(bill?.category ?: "") }.getOrDefault(ExpenseCategory.BILLS)
        )
    }
    var selectedFrequency by remember {
        mutableStateOf(
            runCatching { BillFrequency.valueOf(bill?.frequency ?: "") }.getOrDefault(BillFrequency.MONTHLY)
        )
    }
    var dueDayString by remember { mutableStateOf(bill?.dueDayOfMonth?.toString() ?: "1") }
    var reminderEnabled by remember { mutableStateOf(bill?.reminderEnabled ?: true) }
    var colorHex by remember { mutableStateOf(bill?.colorHex ?: 0xFF8B5CF6) }

    val colors = remember {
        listOf(0xFF8B5CF6, 0xFF3B82F6, 0xFF10B981, 0xFFF59E0B, 0xFFEC4899, 0xFFEF4444)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEdit) "Edit Bill / Sub" else "Add Bill / Subscription",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                if (isEdit) {
                    IconButton(
                        onClick = { onDelete(bill) },
                        modifier = Modifier.testTag("delete_bill_button")
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete Bill",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Bill / Service Name *") },
                    placeholder = { Text("e.g. Netflix, Electricity, Rent, Spotify") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("bill_title_input")
                )

                OutlinedTextField(
                    value = amountString,
                    onValueChange = { amountString = it },
                    label = { Text("Bill Amount ($currencySymbol) *") },
                    placeholder = { Text("50.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("bill_amount_input")
                )

                Text(
                    text = "Billing Frequency",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BillFrequency.entries.forEach { freq ->
                        FilterChip(
                            selected = selectedFrequency == freq,
                            onClick = { selectedFrequency = freq },
                            label = { Text(freq.displayName) }
                        )
                    }
                }

                OutlinedTextField(
                    value = dueDayString,
                    onValueChange = { dueDayString = it },
                    label = { Text("Due Day of Month (1 - 31)") },
                    placeholder = { Text("15") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Color Accent",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colors.forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(col))
                                .clickable { colorHex = col }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (colorHex == col) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Due Date Notification", fontWeight = FontWeight.Bold)
                        Text("Send local reminder 1 day prior", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountString.toDoubleOrNull() ?: 0.0
                    val dueDay = (dueDayString.toIntOrNull() ?: 1).coerceIn(1, 31)
                    if (title.isNotBlank() && amt > 0) {
                        onSave(
                            bill?.id ?: 0L,
                            title,
                            amt,
                            selectedCategory,
                            selectedFrequency,
                            dueDay,
                            reminderEnabled,
                            colorHex
                        )
                    }
                },
                enabled = title.isNotBlank() && (amountString.toDoubleOrNull() ?: 0.0) > 0,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_bill_button")
            ) {
                Text(if (isEdit) "Save Changes" else "Create Bill")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
