package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseEntity
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionType
import com.example.ui.components.getIconForExpenseCategory
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoseRed
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditExpenseDialog(
    expense: ExpenseEntity?,
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        title: String,
        amount: Double,
        type: TransactionType,
        category: ExpenseCategory,
        paymentMethod: PaymentMethod,
        date: LocalDate,
        note: String
    ) -> Unit,
    onDelete: (ExpenseEntity) -> Unit
) {
    val isEdit = expense != null
    var title by remember { mutableStateOf(expense?.title ?: "") }
    var amountString by remember { mutableStateOf(expense?.amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
    var transactionType by remember {
        mutableStateOf(
            runCatching { TransactionType.valueOf(expense?.type ?: "") }.getOrDefault(TransactionType.EXPENSE)
        )
    }
    var selectedCategory by remember {
        mutableStateOf(
            runCatching { ExpenseCategory.valueOf(expense?.category ?: "") }.getOrDefault(
                if (transactionType == TransactionType.INCOME) ExpenseCategory.SALARY else ExpenseCategory.FOOD
            )
        )
    }
    var selectedPaymentMethod by remember {
        mutableStateOf(
            runCatching { PaymentMethod.valueOf(expense?.paymentMethod ?: "") }.getOrDefault(PaymentMethod.CARD)
        )
    }
    var note by remember { mutableStateOf(expense?.note ?: "") }

    val expenseCategories = remember {
        listOf(
            ExpenseCategory.FOOD,
            ExpenseCategory.GROCERIES,
            ExpenseCategory.TRANSPORT,
            ExpenseCategory.BILLS,
            ExpenseCategory.SHOPPING,
            ExpenseCategory.ENTERTAINMENT,
            ExpenseCategory.HEALTH,
            ExpenseCategory.EDUCATION,
            ExpenseCategory.OTHER
        )
    }

    val incomeCategories = remember {
        listOf(
            ExpenseCategory.SALARY,
            ExpenseCategory.INVESTMENT,
            ExpenseCategory.OTHER
        )
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
                    text = if (isEdit) "Edit Transaction" else "Add Transaction",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                if (isEdit) {
                    IconButton(
                        onClick = { onDelete(expense) },
                        modifier = Modifier.testTag("delete_expense_button")
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete Transaction",
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
                // Type Switcher: Expense vs Income
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (transactionType == TransactionType.EXPENSE) RoseRed else Color.Transparent)
                            .clickable {
                                transactionType = TransactionType.EXPENSE
                                if (selectedCategory == ExpenseCategory.SALARY || selectedCategory == ExpenseCategory.INVESTMENT) {
                                    selectedCategory = ExpenseCategory.FOOD
                                }
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Expense",
                            fontWeight = FontWeight.Bold,
                            color = if (transactionType == TransactionType.EXPENSE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (transactionType == TransactionType.INCOME) EmeraldGreen else Color.Transparent)
                            .clickable {
                                transactionType = TransactionType.INCOME
                                selectedCategory = ExpenseCategory.SALARY
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Income",
                            fontWeight = FontWeight.Bold,
                            color = if (transactionType == TransactionType.INCOME) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Amount Field
                OutlinedTextField(
                    value = amountString,
                    onValueChange = { amountString = it },
                    label = { Text("Amount ($) *") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("expense_amount_input")
                )

                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Description / Title *") },
                    placeholder = { Text("e.g. Lunch at Cafe, Uber ride") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("expense_title_input")
                )

                // Category Chips
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categoriesToDisplay = if (transactionType == TransactionType.EXPENSE) expenseCategories else incomeCategories
                    categoriesToDisplay.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.displayName) },
                            leadingIcon = {
                                Icon(
                                    getIconForExpenseCategory(cat),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(cat.colorHex)
                                )
                            }
                        )
                    }
                }

                // Payment Method
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethod.entries.forEach { method ->
                        FilterChip(
                            selected = selectedPaymentMethod == method,
                            onClick = { selectedPaymentMethod = method },
                            label = { Text(method.displayName) }
                        )
                    }
                }

                // Note Field
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Optional)") },
                    placeholder = { Text("Add any extra reference details") },
                    maxLines = 2,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountVal = amountString.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amountVal > 0) {
                        onSave(
                            expense?.id ?: 0L,
                            title,
                            amountVal,
                            transactionType,
                            selectedCategory,
                            selectedPaymentMethod,
                            initialDate,
                            note
                        )
                    }
                },
                enabled = title.isNotBlank() && (amountString.toDoubleOrNull() ?: 0.0) > 0,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_expense_button")
            ) {
                Text(if (isEdit) "Save Changes" else "Add Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
