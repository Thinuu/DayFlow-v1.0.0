package com.example.ui.dialogs

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppConfig
import com.example.ui.theme.BorderPurple
import com.example.ui.theme.DeepIndigoContainer
import com.example.ui.theme.RadiantLavender
import com.example.ui.theme.SurfaceSlatePurple
import com.example.ui.theme.SurfaceVariantMedium
import com.example.ui.theme.TextHighEmphasis
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.DayFlowUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    uiState: DayFlowUiState,
    onDismiss: () -> Unit,
    onOpenBudgetDialog: () -> Unit,
    // Passes symbol AND ISO-4217 code so the ViewModel can persist both
    onSelectCurrency: (symbol: String, code: String) -> Unit,
    onExportExpenses: (Context) -> Unit,
    onExportRoutines: (Context) -> Unit,
    onOpenBlueprints: () -> Unit,
    onOpenSubscriptions: () -> Unit,
    onOpenSecurity: () -> Unit,
    onOpenBackupRestore: () -> Unit,
    onOpenLegal: (String) -> Unit,
    onLockAppNow: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    // Use the authoritative list from AppConfig — edit AppConfig.SUPPORTED_CURRENCIES to add/remove currencies.
    val currencies = AppConfig.SUPPORTED_CURRENCIES

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1C1B1F),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(BorderPurple)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DayFlow Control",
                        style = MaterialTheme.typography.titleMedium,
                        color = RadiantLavender,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Settings & Suite",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.clip(CircleShape).background(SurfaceVariantMedium)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(18))

            // Productivity & Wealth Tools Section
            Text(
                text = "Productivity & Wealth Tools",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = TextHighEmphasis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 1-Click Blueprints
            SettingsActionCard(
                icon = Icons.Filled.Widgets,
                title = "Starter Blueprints & Packs",
                subtitle = "1-Click install Millionaire Morning & 50/30/20 Wealth",
                onClick = {
                    onDismiss()
                    onOpenBlueprints()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subscriptions Manager
            SettingsActionCard(
                icon = Icons.Filled.Repeat,
                title = "Subscriptions & Fixed Bills",
                subtitle = "Track Netflix, Gym, Wi-Fi & monthly recurring burn rate",
                onClick = {
                    onDismiss()
                    onOpenSubscriptions()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Security & Passcode Lock
            SettingsActionCard(
                icon = Icons.Filled.Shield,
                title = "4-Digit PIN & Privacy Lock",
                subtitle = if (uiState.isPinEnabled) "Active • PIN required on launch" else "Off • Secure financial & habit logs",
                onClick = {
                    onDismiss()
                    onOpenSecurity()
                },
                badgeText = if (uiState.isPinEnabled) "SECURED" else null
            )

            if (uiState.isPinEnabled) {
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onLockAppNow()
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderPurple),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RadiantLavender),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lock App Now (Test Security)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Currency Selection
            Text(
                text = "Currency Symbol",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = TextHighEmphasis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currencies.take(4).forEach { option ->
                    val isSelected = uiState.currencySymbol == option.symbol
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) DeepIndigoContainer else SurfaceSlatePurple,
                        border = BorderStroke(1.dp, if (isSelected) RadiantLavender else BorderPurple.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectCurrency(option.symbol, option.code) }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option.symbol,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) RadiantLavender else TextHighEmphasis
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currencies.drop(4).take(4).forEach { option ->
                    val isSelected = uiState.currencySymbol == option.symbol
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) DeepIndigoContainer else SurfaceSlatePurple,
                        border = BorderStroke(1.dp, if (isSelected) RadiantLavender else BorderPurple.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectCurrency(option.symbol, option.code) }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option.symbol,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) RadiantLavender else TextHighEmphasis
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Third row: remaining currencies (slots 9-12)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currencies.drop(8).forEach { option ->
                    val isSelected = uiState.currencySymbol == option.symbol
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) DeepIndigoContainer else SurfaceSlatePurple,
                        border = BorderStroke(1.dp, if (isSelected) RadiantLavender else BorderPurple.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectCurrency(option.symbol, option.code) }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option.symbol,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) RadiantLavender else TextHighEmphasis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Budget Quick Tuning
            SettingsActionCard(
                icon = Icons.Filled.Tune,
                title = "Monthly Budget Caps",
                subtitle = "Target: ${uiState.currencySymbol}${uiState.currentBudget?.monthlyLimit?.toInt() ?: 1800}/mo",
                onClick = {
                    onDismiss()
                    onOpenBudgetDialog()
                },
                badgeText = "EDIT"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Data Export & Full Backup Section
            Text(
                text = "Data Portability & Backup",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = TextHighEmphasis
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onExportExpenses(context) },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderPurple),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RadiantLavender),
                    modifier = Modifier.weight(1f).testTag("export_expenses_button")
                ) {
                    Icon(Icons.Filled.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Expenses CSV", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { onExportRoutines(context) },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderPurple),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RadiantLavender),
                    modifier = Modifier.weight(1f).testTag("export_routines_button")
                ) {
                    Icon(Icons.Filled.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Routines CSV", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsActionCard(
                icon = Icons.Filled.Backup,
                title = "Full Database JSON Backup",
                subtitle = "Export snapshot for device migration or safe keeping",
                onClick = {
                    onDismiss()
                    onOpenBackupRestore()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Legal & Compliance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onOpenLegal("terms")
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Gavel, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Terms of Service", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onOpenLegal("privacy")
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Policy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Privacy Policy", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Version Info
            Text(
                text = "DayFlow v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun SettingsActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    badgeText: String? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSlatePurple),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DeepIndigoContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = RadiantLavender, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            badgeText?.let {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DeepIndigoContainer
                ) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = RadiantLavender,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
