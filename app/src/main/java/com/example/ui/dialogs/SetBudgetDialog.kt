package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun SetBudgetDialog(
    initialMonthlyLimit: Double,
    initialDailyTarget: Double,
    onDismiss: () -> Unit,
    onSave: (monthlyLimit: Double, dailyTarget: Double) -> Unit
) {
    var monthlyLimitString by remember {
        mutableStateOf(if (initialMonthlyLimit > 0) initialMonthlyLimit.toInt().toString() else "1800")
    }
    var dailyTargetString by remember {
        mutableStateOf(if (initialDailyTarget > 0) initialDailyTarget.toInt().toString() else "60")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Set Spending Budgets",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Setting realistic limits helps you stay mindful of everyday expenses.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = monthlyLimitString,
                    onValueChange = { monthlyLimitString = it },
                    label = { Text("Monthly Budget Limit ($)") },
                    placeholder = { Text("e.g. 1800") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("monthly_budget_input")
                )

                OutlinedTextField(
                    value = dailyTargetString,
                    onValueChange = { dailyTargetString = it },
                    label = { Text("Daily Spend Target ($)") },
                    placeholder = { Text("e.g. 60") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("daily_target_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val m = monthlyLimitString.toDoubleOrNull() ?: 1800.0
                    val d = dailyTargetString.toDoubleOrNull() ?: 60.0
                    onSave(m, d)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_budget_button")
            ) {
                Text("Save Budget")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
