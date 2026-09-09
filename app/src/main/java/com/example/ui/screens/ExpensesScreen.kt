package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseEntity
import com.example.data.model.TransactionType
import com.example.ui.components.CategoryDonutChart
import com.example.ui.components.ExpenseItemCard
import com.example.ui.components.formatCurrency
import com.example.ui.theme.BorderPurple
import com.example.ui.theme.DeepIndigoContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RadiantLavender
import com.example.ui.theme.RoseRed
import com.example.ui.theme.SurfaceSlatePurple
import com.example.ui.theme.SurfaceVariantMedium
import com.example.ui.theme.TextHighEmphasis
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.DayFlowUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class ExpenseSortOrder(val displayName: String) {
    DATE_DESC("Newest Date"),
    DATE_ASC("Oldest Date"),
    AMOUNT_DESC("Highest Amount"),
    AMOUNT_ASC("Lowest Amount")
}

@Composable
fun ExpensesScreen(
    uiState: DayFlowUiState,
    onSelectDate: (LocalDate) -> Unit,
    onEditExpense: (ExpenseEntity) -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit,
    onAddExpense: () -> Unit,
    onOpenBudgetDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var typeFilter by remember { mutableStateOf<TransactionType?>(null) }
    var categoryFilter by remember { mutableStateOf<ExpenseCategory?>(null) }
    var sortOrder by remember { mutableStateOf(ExpenseSortOrder.DATE_DESC) }
    var showSortMenu by remember { mutableStateOf(false) }

    val monthlyBudgetLimit = uiState.currentBudget?.monthlyLimit ?: 1800.0
    val budgetProgress = if (monthlyBudgetLimit > 0) (uiState.monthlyTotalExpense / monthlyBudgetLimit).toFloat() else 0f
    val remainingBudget = (monthlyBudgetLimit - uiState.monthlyTotalExpense).coerceAtLeast(0.0)

    val currentMonthKey = uiState.selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

    val filteredExpenses = remember(uiState.allExpenses, typeFilter, categoryFilter, searchQuery, sortOrder) {
        val list = uiState.allExpenses.filter { item ->
            val matchType = typeFilter == null || item.type == typeFilter?.name
            val matchCategory = categoryFilter == null || item.category == categoryFilter?.name
            val matchSearch = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.note.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
            matchType && matchCategory && matchSearch
        }

        when (sortOrder) {
            ExpenseSortOrder.DATE_DESC -> list.sortedByDescending { it.dateEpochDay }
            ExpenseSortOrder.DATE_ASC -> list.sortedBy { it.dateEpochDay }
            ExpenseSortOrder.AMOUNT_DESC -> list.sortedByDescending { it.amount }
            ExpenseSortOrder.AMOUNT_ASC -> list.sortedBy { it.amount }
        }
    }

    val todayEpoch = remember { LocalDate.now().toEpochDay() }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FINANCIAL LEDGER",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.8.sp, fontWeight = FontWeight.Bold),
                            color = RadiantLavender
                        )
                        Text(
                            text = "Money & Budget",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DeepIndigoContainer,
                        border = BorderStroke(1.dp, BorderPurple),
                        modifier = Modifier.clickable { onOpenBudgetDialog() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.FilterList, contentDescription = null, tint = RadiantLavender, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Set Budget", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = RadiantLavender)
                        }
                    }
                }
            }

            // Executive Monthly Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF2C1045), Color(0xFF1B0B2E), Color(0xFF11071F))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$currentMonthKey Overview",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (budgetProgress > 1f) RoseRed.copy(alpha = 0.2f) else EmeraldGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (budgetProgress > 1f) "OVER BUDGET" else "${(budgetProgress * 100).toInt()}% USED",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (budgetProgress > 1f) RoseRed else EmeraldGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Budget Progress Bar
                        LinearProgressIndicator(
                            progress = { budgetProgress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = if (budgetProgress > 0.9f) RoseRed else RadiantLavender,
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Monthly Spent", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text(
                                    formatCurrency(uiState.monthlyTotalExpense, uiState.currencySymbol),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = RoseRed
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Remaining Budget", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text(
                                    formatCurrency(remainingBudget, uiState.currencySymbol),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (remainingBudget > 0) EmeraldGreen else RoseRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.ArrowUpward, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Income", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                    Text(
                                        formatCurrency(uiState.monthlyTotalIncome, uiState.currencySymbol),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(RoseRed.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.ArrowDownward, contentDescription = null, tint = RoseRed, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Monthly Limit", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                    Text(
                                        formatCurrency(monthlyBudgetLimit, uiState.currencySymbol),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Spending Distribution Category Chart
            if (uiState.categorySpends.isNotEmpty()) {
                item {
                    Text(
                        text = "Spending Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoryDonutChart(categorySpends = uiState.categorySpends)
                }
            }

            // Search Bar & Filter Strip
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search transactions, notes, categories...") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Search", tint = RadiantLavender)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear", tint = TextMuted)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceSlatePurple,
                            unfocusedContainerColor = SurfaceSlatePurple,
                            focusedBorderColor = RadiantLavender,
                            unfocusedBorderColor = BorderPurple.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_search_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Type Filter Chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (typeFilter == null) RadiantLavender else SurfaceSlatePurple,
                                modifier = Modifier
                                    .clickable { typeFilter = null }
                                    .testTag("filter_type_all")
                            ) {
                                Text(
                                    text = "All",
                                    color = if (typeFilter == null) DeepIndigoContainer else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (typeFilter == TransactionType.EXPENSE) RoseRed else SurfaceSlatePurple,
                                modifier = Modifier
                                    .clickable { typeFilter = if (typeFilter == TransactionType.EXPENSE) null else TransactionType.EXPENSE }
                                    .testTag("filter_type_expense")
                            ) {
                                Text(
                                    text = "Expenses",
                                    color = if (typeFilter == TransactionType.EXPENSE) Color.White else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (typeFilter == TransactionType.INCOME) EmeraldGreen else SurfaceSlatePurple,
                                modifier = Modifier
                                    .clickable { typeFilter = if (typeFilter == TransactionType.INCOME) null else TransactionType.INCOME }
                                    .testTag("filter_type_income")
                            ) {
                                Text(
                                    text = "Income",
                                    color = if (typeFilter == TransactionType.INCOME) Color.Black else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Sort Menu
                        Box {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceSlatePurple,
                                border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .clickable { showSortMenu = true }
                                    .testTag("expense_sort_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Sort, contentDescription = "Sort", tint = RadiantLavender, modifier = Modifier.size(18.dp))
                                }
                            }

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false },
                                modifier = Modifier.background(SurfaceSlatePurple)
                            ) {
                                ExpenseSortOrder.entries.forEach { order ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                order.displayName,
                                                color = if (sortOrder == order) RadiantLavender else Color.White,
                                                fontWeight = if (sortOrder == order) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            sortOrder = order
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Transactions List
            if (filteredExpenses.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceSlatePurple),
                        border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceVariantMedium),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.ReceiptLong,
                                    contentDescription = null,
                                    tint = RadiantLavender,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isNotBlank() || typeFilter != null) "No Matching Transactions" else "No Transactions Logged",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Try modifying your search or filter criteria"
                                       else "Tap + Log Transaction to record your first entry",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = onAddExpense,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderPurple),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RadiantLavender)
                            ) {
                                Text("+ Log Transaction")
                            }
                        }
                    }
                }
            } else {
                items(filteredExpenses) { item ->
                    ExpenseItemCard(
                        expense = item,
                        currencySymbol = uiState.currencySymbol,
                        onEdit = { onEditExpense(item) },
                        onDelete = { onDeleteExpense(item) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        FloatingActionButton(
            onClick = onAddExpense,
            containerColor = RadiantLavender,
            contentColor = DeepIndigoContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .testTag("fab_add_expense")
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
        }
    }
}
