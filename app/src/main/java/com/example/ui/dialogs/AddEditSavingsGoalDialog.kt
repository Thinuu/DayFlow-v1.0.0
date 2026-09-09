package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.SavingsGoalEntity

@Composable
fun AddEditSavingsGoalDialog(
    goal: SavingsGoalEntity?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        name: String,
        targetAmount: Double,
        currentAmount: Double,
        monthsTarget: Int,
        colorHex: Long,
        note: String
    ) -> Unit,
    onDelete: (SavingsGoalEntity) -> Unit
) {
    val isEdit = goal != null
    var name by remember { mutableStateOf(goal?.name ?: "") }
    var targetAmountString by remember {
        mutableStateOf(goal?.targetAmount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }
    var currentAmountString by remember {
        mutableStateOf(goal?.currentAmount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "0")
    }
    var monthsString by remember { mutableStateOf("6") }
    var colorHex by remember { mutableStateOf(goal?.colorHex ?: 0xFF10B981) }
    var note by remember { mutableStateOf(goal?.note ?: "") }

    val colors = remember {
        listOf(0xFF10B981, 0xFF6366F1, 0xFFF59E0B, 0xFFEC4899, 0xFF3B82F6, 0xFF8B5CF6)
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
                    text = if (isEdit) "Edit Savings Goal" else "New Savings Goal",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                if (isEdit) {
                    IconButton(
                        onClick = { onDelete(goal) },
                        modifier = Modifier.testTag("delete_goal_button")
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete Goal",
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal Name *") },
                    placeholder = { Text("e.g. Emergency Fund, New Laptop, Holiday") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("goal_name_input")
                )

                OutlinedTextField(
                    value = targetAmountString,
                    onValueChange = { targetAmountString = it },
                    label = { Text("Target Amount ($currencySymbol) *") },
                    placeholder = { Text("2500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("goal_target_input")
                )

                OutlinedTextField(
                    value = currentAmountString,
                    onValueChange = { currentAmountString = it },
                    label = { Text("Starting Saved Amount ($currencySymbol)") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = monthsString,
                    onValueChange = { monthsString = it },
                    label = { Text("Target Duration (Months)") },
                    placeholder = { Text("6") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Goal Color Accent",
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

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Motivation / Notes") },
                    placeholder = { Text("Why are you saving for this goal?") },
                    maxLines = 2,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val targetVal = targetAmountString.toDoubleOrNull() ?: 0.0
                    val currentVal = currentAmountString.toDoubleOrNull() ?: 0.0
                    val monthsVal = monthsString.toIntOrNull() ?: 6
                    if (name.isNotBlank() && targetVal > 0) {
                        onSave(
                            goal?.id ?: 0L,
                            name,
                            targetVal,
                            currentVal,
                            monthsVal,
                            colorHex,
                            note
                        )
                    }
                },
                enabled = name.isNotBlank() && (targetAmountString.toDoubleOrNull() ?: 0.0) > 0,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_goal_button")
            ) {
                Text(if (isEdit) "Save Changes" else "Create Goal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddSavingsContributionDialog(
    goal: SavingsGoalEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onContribute: (goalId: Long, amount: Double, note: String, isWithdrawal: Boolean) -> Unit
) {
    var amountString by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isWithdrawal by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isWithdrawal) "Withdraw from ${goal.name}" else "Contribute to ${goal.name}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isWithdrawal = false },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = if (!isWithdrawal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Deposit", color = if (!isWithdrawal) Color.Black else MaterialTheme.colorScheme.onSurface)
                    }
                    Button(
                        onClick = { isWithdrawal = true },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = if (isWithdrawal) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Withdraw", color = if (isWithdrawal) Color.White else MaterialTheme.colorScheme.onSurface)
                    }
                }

                OutlinedTextField(
                    value = amountString,
                    onValueChange = { amountString = it },
                    label = { Text("Amount ($currencySymbol) *") },
                    placeholder = { Text("100") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("contrib_amount_input")
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Optional)") },
                    placeholder = { Text("e.g. Monthly transfer, bonus allocation") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountString.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onContribute(goal.id, amt, note, isWithdrawal)
                    }
                },
                enabled = (amountString.toDoubleOrNull() ?: 0.0) > 0,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("submit_contrib_button")
            ) {
                Text(if (isWithdrawal) "Confirm Withdrawal" else "Confirm Deposit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
