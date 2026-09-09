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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditAccountDialog(
    account: AccountEntity?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        name: String,
        type: AccountType,
        initialBalance: Double,
        colorHex: Long,
        iconKey: String,
        isDefault: Boolean,
        accountNumberMask: String
    ) -> Unit,
    onDelete: (AccountEntity) -> Unit
) {
    val isEdit = account != null
    var name by remember { mutableStateOf(account?.name ?: "") }
    var initialBalanceString by remember {
        mutableStateOf(account?.initialBalance?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }
    var selectedType by remember {
        mutableStateOf(
            runCatching { AccountType.valueOf(account?.type ?: "") }.getOrDefault(AccountType.BANK)
        )
    }
    var colorHex by remember { mutableStateOf(account?.colorHex ?: 0xFF6366F1) }
    var iconKey by remember { mutableStateOf(account?.iconKey ?: "account_balance") }
    var isDefault by remember { mutableStateOf(account?.isDefault ?: false) }
    var accountMask by remember { mutableStateOf(account?.accountNumberMask ?: "") }

    val colors = remember {
        listOf(
            0xFF6366F1, 0xFF10B981, 0xFFF59E0B, 0xFFEC4899,
            0xFF3B82F6, 0xFF8B5CF6, 0xFFEF4444, 0xFF14B8A6
        )
    }

    val iconOptions = remember {
        listOf(
            "account_balance" to Icons.Filled.AccountBalance,
            "payments" to Icons.Filled.Payments,
            "savings" to Icons.Filled.Savings,
            "account_balance_wallet" to Icons.Filled.AccountBalanceWallet,
            "credit_card" to Icons.Filled.CreditCard,
            "trending_up" to Icons.Filled.TrendingUp,
            "wallet" to Icons.Filled.Wallet
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
                    text = if (isEdit) "Edit Account" else "Add Account / Wallet",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                if (isEdit) {
                    IconButton(
                        onClick = { onDelete(account) },
                        modifier = Modifier.testTag("delete_account_button")
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete Account",
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
                    label = { Text("Account Name *") },
                    placeholder = { Text("e.g. Chase Checking, Daily Cash, Crypto") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("account_name_input")
                )

                OutlinedTextField(
                    value = initialBalanceString,
                    onValueChange = { initialBalanceString = it },
                    label = { Text("Initial Balance ($currencySymbol)") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("account_balance_input")
                )

                Text(
                    text = "Account Type",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AccountType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.displayName) }
                        )
                    }
                }

                OutlinedTextField(
                    value = accountMask,
                    onValueChange = { accountMask = it },
                    label = { Text("Card/Account Mask (Optional)") },
                    placeholder = { Text("e.g. •••• 4242 or Checking") },
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
                        Text("Default Account", fontWeight = FontWeight.Bold)
                        Text("Pre-selected for new transactions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val initBal = initialBalanceString.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onSave(
                            account?.id ?: 0L,
                            name,
                            selectedType,
                            initBal,
                            colorHex,
                            iconKey,
                            isDefault,
                            accountMask
                        )
                    }
                },
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_account_button")
            ) {
                Text(if (isEdit) "Save Changes" else "Create Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
