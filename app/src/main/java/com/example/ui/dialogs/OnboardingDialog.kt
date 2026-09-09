package com.example.ui.dialogs

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RadiantLavender
import com.example.ui.theme.SurfaceSlatePurple

@Composable
fun OnboardingDialog(
    currentCurrency: String,
    onComplete: (currency: String, monthlyBudget: Double) -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var selectedCurrency by remember { mutableStateOf(currentCurrency) }
    var monthlyBudgetString by remember { mutableStateOf("1800") }

    val currencies = listOf("$" to "USD ($)", "€" to "EUR (€)", "£" to "GBP (£)", "¥" to "JPY (¥)", "₹" to "INR (₹)", "Rs" to "LKR/PKR (Rs)", "C$" to "CAD (C$)")

    AlertDialog(
        onDismissRequest = { /* Force completion or skip */ },
        title = null,
        text = {
            AnimatedContent(targetState = step, label = "onboarding_step") { currentStep ->
                when (currentStep) {
                    0 -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(RadiantLavender.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = RadiantLavender,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Welcome to DayFlow",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your unified personal finance tracker, daily habit architect, and productivity command center.",
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            FeatureRow(icon = Icons.Filled.AccountBalance, title = "Smart Cashflow & Wallets", desc = "Track income, expense, and multi-bank balances safely offline.")
                            Spacer(modifier = Modifier.height(12.dp))
                            FeatureRow(icon = Icons.Filled.EventNote, title = "Atomic Habit Streaks", desc = "Build sustainable morning & evening routines with visual streaks.")
                            Spacer(modifier = Modifier.height(12.dp))
                            FeatureRow(icon = Icons.Filled.Savings, title = "Savings & Due-Date Radar", desc = "Reach your dream targets and never miss recurring subscriptions.")
                        }
                    }
                    1 -> {
                        Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                            Text(
                                text = "Select Currency & Budget",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Choose your primary currency symbol and target monthly spending envelope.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Preferred Currency", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                currencies.take(4).forEach { (sym, label) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (selectedCurrency == sym) RadiantLavender else SurfaceSlatePurple)
                                            .clickable { selectedCurrency = sym }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            sym,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedCurrency == sym) Color.Black else Color.White
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                currencies.drop(4).forEach { (sym, label) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (selectedCurrency == sym) RadiantLavender else SurfaceSlatePurple)
                                            .clickable { selectedCurrency = sym }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            sym,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedCurrency == sym) Color.Black else Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            Text("Monthly Budget Ceiling ($selectedCurrency)", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = monthlyBudgetString,
                                onValueChange = { monthlyBudgetString = it },
                                placeholder = { Text("1800") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step == 0) {
                        step = 1
                    } else {
                        val budgetVal = monthlyBudgetString.toDoubleOrNull() ?: 1800.0
                        onComplete(selectedCurrency, budgetVal)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("onboarding_continue_button")
            ) {
                Text(if (step == 0) "Get Started" else "Launch DayFlow")
            }
        },
        dismissButton = {
            if (step == 0) {
                TextButton(onClick = {
                    onComplete("$", 1800.0)
                }) {
                    Text("Skip")
                }
            }
        }
    )
}

@Composable
private fun FeatureRow(icon: ImageVector, title: String, desc: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceSlatePurple),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = RadiantLavender, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
