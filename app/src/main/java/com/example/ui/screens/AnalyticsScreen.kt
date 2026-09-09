package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionType
import com.example.ui.components.CategoryDonutChart
import com.example.ui.components.formatCurrency
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.BorderPurple
import com.example.ui.theme.DeepIndigoContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ProGold
import com.example.ui.theme.ProGoldDark
import com.example.ui.theme.ProVipIndigo
import com.example.ui.theme.RadiantLavender
import com.example.ui.theme.RoseRed
import com.example.ui.theme.SurfaceSlatePurple
import com.example.ui.theme.SurfaceVariantMedium
import com.example.ui.theme.TextHighEmphasis
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.DayFlowUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AnalyticsScreen(
    uiState: DayFlowUiState,
    onExportExpenses: (Context) -> Unit,
    onExportRoutines: (Context) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val today = remember { LocalDate.now() }
    val last7Days = remember(today) {
        (6 downTo 0).map { today.minusDays(it.toLong()) }
    }

    // Weekly spend amounts for the last 7 days
    val weeklySpends = remember(uiState.allExpenses, last7Days) {
        last7Days.map { date ->
            val epoch = date.toEpochDay()
            val total = uiState.allExpenses
                .filter { it.dateEpochDay == epoch && it.type == TransactionType.EXPENSE.name }
                .sumOf { it.amount }
            Pair(date, total)
        }
    }

    val maxDailySpend = remember(weeklySpends) {
        weeklySpends.maxOfOrNull { it.second }?.coerceAtLeast(50.0) ?: 100.0
    }

    // Calculate savings rate
    val savingsRate = remember(uiState.monthlyTotalIncome, uiState.monthlyTotalExpense) {
        if (uiState.monthlyTotalIncome > 0) {
            val saved = (uiState.monthlyTotalIncome - uiState.monthlyTotalExpense).coerceAtLeast(0.0)
            ((saved / uiState.monthlyTotalIncome) * 100).toInt()
        } else 0
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Column {
                Text(
                    text = "INSIGHTS & PERFORMANCE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.8.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = RadiantLavender
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Analytics & Mastery",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }

        // DayFlow Discipline & Wealth Score Card (0-100)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ProVipIndigo),
                border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(ProGold, RadiantLavender))),
                modifier = Modifier.fillMaxWidth().testTag("analytics_score_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(Brush.radialGradient(listOf(ProGold, ProGoldDark))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Speed, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "DAYFLOW MASTERY SCORE",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold),
                                    color = ProGold
                                )
                                Text(
                                    text = if (uiState.dayFlowScore >= 80) "Elite Momentum" else "Building Discipline",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }

                        Text(
                            text = "${uiState.dayFlowScore}/100",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                            color = ProGold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { uiState.dayFlowScore / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = ProGold,
                        trackColor = BorderPurple.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Calculated from your 7-day habit streak (${uiState.bestStreak}d), routine completion rate (${(uiState.routineCompletionRate * 100).toInt()}%), and budget adherence.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextHighEmphasis.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Financial Health Highlights (Savings Rate + Monthly Net)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Savings Rate Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceSlatePurple),
                    border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Savings, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SAVINGS RATE", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold), color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$savingsRate%",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = if (savingsRate >= 20) EmeraldGreen else AmberOrange
                        )
                        Text(
                            text = if (savingsRate >= 20) "Target achieved" else "Target 20%+",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                // Best Streak Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceSlatePurple),
                    border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = AmberOrange, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("BEST STREAK", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold), color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${uiState.bestStreak} Days",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = AmberOrange
                        )
                        Text(
                            text = "${uiState.totalCompletionsAllTime} habits logged",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Monthly Income vs Expense Breakdown Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceSlatePurple),
                border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monthly Financial Flow",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DeepIndigoContainer
                        ) {
                            Text(
                                text = "THIS MONTH",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RadiantLavender,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Income", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(
                                formatCurrency(uiState.monthlyTotalIncome, uiState.currencySymbol),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldGreen
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Outflow", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(
                                formatCurrency(uiState.monthlyTotalExpense, uiState.currencySymbol),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = RoseRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Net Remaining Pill
                    val net = uiState.monthlyTotalIncome - uiState.monthlyTotalExpense
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DeepIndigoContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Net Savings / Surplus",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TextHighEmphasis
                            )
                            Text(
                                text = "${if (net >= 0) "+" else ""}${formatCurrency(net, uiState.currencySymbol)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (net >= 0) EmeraldGreen else RoseRed
                            )
                        }
                    }
                }
            }
        }

        // Expense Category Donut Breakdown
        if (uiState.categorySpends.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceSlatePurple),
                    border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Spending by Category",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Icon(Icons.Filled.PieChart, contentDescription = null, tint = RadiantLavender, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        CategoryDonutChart(
                            categorySpends = uiState.categorySpends
                        )
                    }
                }
            }
        }

        // 7-Day Spending Trend Bar Graph
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceSlatePurple),
                border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "7-Day Expense Trend",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Icon(Icons.Filled.Timeline, contentDescription = null, tint = RadiantLavender, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklySpends.forEach { (date, amount) ->
                            val isToday = date == today
                            val heightFraction = if (maxDailySpend > 0) (amount / maxDailySpend).toFloat().coerceIn(0.08f, 1f) else 0.08f

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            ) {
                                if (amount > 0) {
                                    Text(
                                        text = "${amount.toInt()}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = if (isToday) RadiantLavender else TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(22.dp)
                                            .fillMaxHeight(heightFraction)
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                            .background(
                                                if (isToday) RadiantLavender
                                                else if (amount > 0) RadiantLavender.copy(alpha = 0.55f)
                                                else DeepIndigoContainer
                                            )
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern("EEE")).take(2),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isToday) RadiantLavender else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Commercial Achievements & Mastery Milestones
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceSlatePurple),
                border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = ProGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Milestones & Badges",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Text(
                            text = "${uiState.achievements.count { it.isUnlocked }} / ${uiState.achievements.size}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = ProGold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.achievements.forEach { badge ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (badge.isUnlocked) DeepIndigoContainer else SurfaceVariantMedium.copy(alpha = 0.5f))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (badge.isUnlocked) ProGold else DeepIndigoContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        when (badge.iconKey) {
                                            "flag" -> Icons.Filled.Flag
                                            "local_fire_department" -> Icons.Filled.LocalFireDepartment
                                            "workspace_premium" -> Icons.Filled.WorkspacePremium
                                            else -> Icons.Filled.AccountBalanceWallet
                                        },
                                        contentDescription = null,
                                        tint = if (badge.isUnlocked) Color.Black else RadiantLavender,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = badge.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        if (badge.isUnlocked) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Filled.Verified, contentDescription = null, tint = ProGold, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text(
                                        text = badge.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Data Export Options
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceSlatePurple),
                border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.FileDownload, contentDescription = null, tint = RadiantLavender, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Export Reports & Backups",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Download clean CSV datasets for spreadsheets or tax records.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onExportExpenses(context) },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BorderPurple),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RadiantLavender),
                            modifier = Modifier.weight(1f).testTag("analytics_export_expenses")
                        ) {
                            Text("Export Expenses", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { onExportRoutines(context) },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BorderPurple),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RadiantLavender),
                            modifier = Modifier.weight(1f).testTag("analytics_export_routines")
                        ) {
                            Text("Export Habits", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
