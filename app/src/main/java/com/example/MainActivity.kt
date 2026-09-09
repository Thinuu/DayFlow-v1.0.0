package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.preferences.AppThemeMode
import com.example.data.model.TransactionType
import com.example.data.model.PaymentMethod
import com.example.ui.components.PinLockScreen
import com.example.ui.dialogs.AIInsightsDialog
import com.example.ui.dialogs.AddEditAccountDialog
import com.example.ui.dialogs.AddEditBillDialog
import com.example.ui.dialogs.AddEditExpenseDialog
import com.example.ui.dialogs.AddEditRoutineDialog
import com.example.ui.dialogs.AddEditSavingsGoalDialog
import com.example.ui.dialogs.AddEditTaskDialog
import com.example.ui.dialogs.AddSavingsContributionDialog
import com.example.ui.dialogs.BackupRestoreDialog
import com.example.ui.dialogs.BlueprintsDialog
import com.example.ui.dialogs.LegalDialog
import com.example.ui.dialogs.OnboardingDialog

import com.example.ui.dialogs.SearchFilterSheet
import com.example.ui.dialogs.SecurityLockDialog
import com.example.ui.dialogs.SetBudgetDialog
import com.example.ui.dialogs.SettingsSheet
import com.example.ui.dialogs.SubscriptionsDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BillsScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RoutinesScreen
import com.example.ui.screens.SavingsScreen
import com.example.ui.theme.DeepIndigoContainer
import com.example.ui.theme.DayFlowTheme
import com.example.ui.theme.RadiantLavender
import com.example.ui.theme.SurfaceSlatePurple
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    /**
     * Launcher for the POST_NOTIFICATIONS runtime permission (Android 13+, API 33+).
     *
     * Result handling:
     * - Granted: notifications will be delivered normally.
     * - Denied: the app continues to work fully; notification delivery is silently
     *   skipped by [com.example.data.service.NotificationHelper.canPostNotifications].
     * - Permanently denied (user checked "Don't ask again"): same behaviour — the app
     *   does not re-request or show an error. Users can re-enable via system Settings.
     *
     * We do NOT force users to enable notifications. Denying the permission must never
     * cause a crash or degrade core functionality.
     */
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // No-op: the app works whether the user grants or denies the permission.
        // NotificationHelper.canPostNotifications() checks state at delivery time.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create notification channels first (idempotent on repeated calls, no-op pre-O)
        com.example.data.service.NotificationHelper.createNotificationChannels(this)

        // Request POST_NOTIFICATIONS on Android 13+ only if not already granted.
        // On Android 12 and below this permission does not exist — no request needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val alreadyGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!alreadyGranted) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        enableEdgeToEdge()
        setContent {
            DayFlowApp(viewModel = viewModel)
        }
    }
}

@Composable
fun DayFlowApp(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()

    // Resolve dark/light from the persisted preference.
    // SYSTEM follows the device setting; DARK/LIGHT override it.
    val systemDark = isSystemInDarkTheme()
    val useDarkTheme = when (uiState.themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> systemDark
    }

    DayFlowTheme(darkTheme = useDarkTheme) {

    val showAddRoutine by viewModel.showAddRoutineDialog.collectAsStateWithLifecycle()
    val routineToEdit by viewModel.routineToEdit.collectAsStateWithLifecycle()
    val showAddExpense by viewModel.showAddExpenseDialog.collectAsStateWithLifecycle()
    val expenseToEdit by viewModel.expenseToEdit.collectAsStateWithLifecycle()
    val showAddAccount by viewModel.showAddAccountDialog.collectAsStateWithLifecycle()
    val accountToEdit by viewModel.accountToEdit.collectAsStateWithLifecycle()
    val showAddSavingsGoal by viewModel.showAddSavingsGoalDialog.collectAsStateWithLifecycle()
    val savingsGoalToEdit by viewModel.savingsGoalToEdit.collectAsStateWithLifecycle()
    val showAddContribution by viewModel.showAddContributionDialog.collectAsStateWithLifecycle()
    val goalForContribution by viewModel.savingsGoalForContribution.collectAsStateWithLifecycle()
    val showAddBill by viewModel.showAddBillDialog.collectAsStateWithLifecycle()
    val billToEdit by viewModel.billToEdit.collectAsStateWithLifecycle()
    val showAddTask by viewModel.showAddTaskDialog.collectAsStateWithLifecycle()
    val taskToEdit by viewModel.taskToEdit.collectAsStateWithLifecycle()

    val showBudgetDialog by viewModel.showBudgetDialog.collectAsStateWithLifecycle()
    val showSettingsSheet by viewModel.showSettingsSheet.collectAsStateWithLifecycle()
    val showAiInsights by viewModel.showAiInsightsDialog.collectAsStateWithLifecycle()
    val showSearchFilter by viewModel.showSearchFilterSheet.collectAsStateWithLifecycle()
    val showOnboarding by viewModel.showOnboardingDialog.collectAsStateWithLifecycle()

    // Dialog states
    val showBlueprints by viewModel.showBlueprintsDialog.collectAsStateWithLifecycle()
    val showSubscriptions by viewModel.showSubscriptionsDialog.collectAsStateWithLifecycle()
    val showSecurity by viewModel.showSecurityDialog.collectAsStateWithLifecycle()
    val showBackupRestore by viewModel.showBackupRestoreDialog.collectAsStateWithLifecycle()
    val showLegal by viewModel.showLegalDialog.collectAsStateWithLifecycle()

    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val hasUndo by viewModel.hasUndoAction.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = if (hasUndo) "UNDO" else null,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoLastDelete()
            }
            viewModel.clearSnackbar()
        }
    }

    if (isAppLocked) {
        PinLockScreen(
            correctPinHash = uiState.userPin,
            onUnlocked = { viewModel.unlockApp() }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = SurfaceSlatePurple,
                        contentColor = Color.White,
                        actionColor = RadiantLavender,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("main_bottom_nav"),
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp
                ) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RadiantLavender,
                        selectedTextColor = RadiantLavender,
                        indicatorColor = DeepIndigoContainer,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.TODAY,
                        onClick = { viewModel.selectTab(AppTab.TODAY) },
                        colors = navItemColors,
                        icon = {
                            Icon(
                                if (uiState.currentTab == AppTab.TODAY) Icons.Filled.Today else Icons.Outlined.Today,
                                contentDescription = "Today"
                            )
                        },
                        label = { Text(AppTab.TODAY.title, fontWeight = if (uiState.currentTab == AppTab.TODAY) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_tab_today")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.FINANCE,
                        onClick = { viewModel.selectTab(AppTab.FINANCE) },
                        colors = navItemColors,
                        icon = {
                            Icon(
                                if (uiState.currentTab == AppTab.FINANCE) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                                contentDescription = "Finance"
                            )
                        },
                        label = { Text(AppTab.FINANCE.title, fontWeight = if (uiState.currentTab == AppTab.FINANCE) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_tab_finance")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.SAVINGS,
                        onClick = { viewModel.selectTab(AppTab.SAVINGS) },
                        colors = navItemColors,
                        icon = {
                            Icon(
                                if (uiState.currentTab == AppTab.SAVINGS) Icons.Filled.Savings else Icons.Outlined.Savings,
                                contentDescription = "Savings"
                            )
                        },
                        label = { Text(AppTab.SAVINGS.title, fontWeight = if (uiState.currentTab == AppTab.SAVINGS) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_tab_savings")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.BILLS,
                        onClick = { viewModel.selectTab(AppTab.BILLS) },
                        colors = navItemColors,
                        icon = {
                            Icon(
                                if (uiState.currentTab == AppTab.BILLS) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                contentDescription = "Bills"
                            )
                        },
                        label = { Text(AppTab.BILLS.title, fontWeight = if (uiState.currentTab == AppTab.BILLS) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_tab_bills")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.ROUTINES,
                        onClick = { viewModel.selectTab(AppTab.ROUTINES) },
                        colors = navItemColors,
                        icon = {
                            Icon(
                                if (uiState.currentTab == AppTab.ROUTINES) Icons.Filled.EventNote else Icons.Outlined.EventNote,
                                contentDescription = "Routines"
                            )
                        },
                        label = { Text(AppTab.ROUTINES.title, fontWeight = if (uiState.currentTab == AppTab.ROUTINES) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_tab_routines")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.ANALYTICS,
                        onClick = { viewModel.selectTab(AppTab.ANALYTICS) },
                        colors = navItemColors,
                        icon = {
                            Icon(
                                if (uiState.currentTab == AppTab.ANALYTICS) Icons.Filled.Analytics else Icons.Outlined.Analytics,
                                contentDescription = "Analytics"
                            )
                        },
                        label = { Text(AppTab.ANALYTICS.title, fontWeight = if (uiState.currentTab == AppTab.ANALYTICS) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("nav_tab_analytics")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = uiState.currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { currentTab ->
                    when (currentTab) {
                        AppTab.TODAY -> HomeScreen(
                            uiState = uiState,
                            onSelectDate = { viewModel.selectDate(it) },
                            onToggleRoutine = { id, done -> viewModel.toggleRoutine(id, done) },
                            onEditRoutine = { viewModel.openEditRoutine(it) },
                            onDeleteRoutine = { viewModel.deleteRoutine(it) },
                            onAddRoutine = { viewModel.openAddRoutine() },
                            onToggleTask = { id, done -> viewModel.toggleTaskCompleted(id, done) },
                            onEditTask = { viewModel.openEditTask(it) },
                            onDeleteTask = { viewModel.deleteTask(it) },
                            onAddTask = { viewModel.openAddTask() },
                            onEditExpense = { viewModel.openEditExpense(it) },
                            onDeleteExpense = { viewModel.deleteExpense(it) },
                            onAddExpense = { viewModel.openAddExpense() },
                            onOpenBudgetDialog = { viewModel.showBudgetDialog.value = true },
                            onOpenSettings = { viewModel.openSettings() },
                            onNavigateToTab = { viewModel.selectTab(it) },
                            onOpenBlueprints = { viewModel.showBlueprintsDialog.value = true },
                            onOpenAiInsights = { viewModel.showAiInsightsDialog.value = true },
                            onOpenSearch = { viewModel.showSearchFilterSheet.value = true },
                            onLockApp = { viewModel.lockApp() }
                        )

                        AppTab.FINANCE -> FinanceScreen(
                            uiState = uiState,
                            onAddTransaction = { viewModel.openAddExpense() },
                            onEditTransaction = { viewModel.openEditExpense(it) },
                            onDeleteTransaction = { viewModel.deleteExpense(it) },
                            onAddAccount = { viewModel.openAddAccount() },
                            onEditAccount = { viewModel.openEditAccount(it) },
                            onOpenSearch = { viewModel.showSearchFilterSheet.value = true }
                        )

                        AppTab.SAVINGS -> SavingsScreen(
                            uiState = uiState,
                            onAddGoal = { viewModel.openAddSavingsGoal() },
                            onEditGoal = { viewModel.openEditSavingsGoal(it) },
                            onAddContribution = { viewModel.openAddContribution(it) }
                        )

                        AppTab.BILLS -> BillsScreen(
                            uiState = uiState,
                            onAddBill = { viewModel.openAddBill() },
                            onEditBill = { viewModel.openEditBill(it) },
                            onTogglePaid = { id, paid -> viewModel.toggleBillPaid(id, paid) }
                        )

                        AppTab.ROUTINES -> RoutinesScreen(
                            uiState = uiState,
                            onSelectDate = { viewModel.selectDate(it) },
                            onToggleRoutine = { id, done -> viewModel.toggleRoutine(id, done) },
                            onMarkAllRoutines = { viewModel.markAllRoutinesDoneForDate(it) },
                            onEditRoutine = { viewModel.openEditRoutine(it) },
                            onDeleteRoutine = { viewModel.deleteRoutine(it) },
                            onAddRoutine = { viewModel.openAddRoutine() }
                        )

                        AppTab.ANALYTICS -> AnalyticsScreen(
                            uiState = uiState,
                            onExportExpenses = { viewModel.exportTransactionsCsv(it) },
                            onExportRoutines = { viewModel.exportRoutinesCsv(it) }
                        )
                    }
                }

                // Routine Creation / Edit Dialog
                if (showAddRoutine) {
                    AddEditRoutineDialog(
                        routine = routineToEdit,
                        onDismiss = {
                            viewModel.showAddRoutineDialog.value = false
                            viewModel.routineToEdit.value = null
                        },
                        onSave = { id, title, note, cat, timeMins, daysMask, tod, icon, color ->
                            viewModel.saveRoutine(id, title, note, cat, timeMins, daysMask, tod, icon, color)
                        },
                        onDelete = {
                            viewModel.deleteRoutine(it)
                        }
                    )
                }

                // Expense Logging Dialog
                if (showAddExpense) {
                    AddEditExpenseDialog(
                        expense = expenseToEdit,
                        initialDate = uiState.selectedDate,
                        onDismiss = {
                            viewModel.showAddExpenseDialog.value = false
                            viewModel.expenseToEdit.value = null
                        },
                        onSave = { id, title, amt, type, cat, method, date, note ->
                            viewModel.saveExpense(id, title, amt, type, cat, method, date, note)
                        },
                        onDelete = {
                            viewModel.deleteExpense(it)
                        }
                    )
                }

                // Account Dialog
                if (showAddAccount) {
                    AddEditAccountDialog(
                        account = accountToEdit,
                        currencySymbol = uiState.currencySymbol,
                        onDismiss = {
                            viewModel.showAddAccountDialog.value = false
                            viewModel.accountToEdit.value = null
                        },
                        onSave = { id, name, type, initBal, colorHex, iconKey, isDefault, mask ->
                            viewModel.saveAccount(id, name, type, initBal, colorHex, iconKey, isDefault, mask)
                        },
                        onDelete = {
                            viewModel.deleteAccount(it)
                        }
                    )
                }

                // Savings Goal Dialog
                if (showAddSavingsGoal) {
                    AddEditSavingsGoalDialog(
                        goal = savingsGoalToEdit,
                        currencySymbol = uiState.currencySymbol,
                        onDismiss = {
                            viewModel.showAddSavingsGoalDialog.value = false
                            viewModel.savingsGoalToEdit.value = null
                        },
                        onSave = { id, name, targetAmt, currentAmt, months, colorHex, note ->
                            viewModel.saveSavingsGoal(id, name, targetAmt, currentAmt, months, colorHex, note)
                        },
                        onDelete = {
                            viewModel.deleteSavingsGoal(it)
                        }
                    )
                }

                // Savings Contribution Dialog
                if (showAddContribution && goalForContribution != null) {
                    AddSavingsContributionDialog(
                        goal = goalForContribution!!,
                        currencySymbol = uiState.currencySymbol,
                        onDismiss = {
                            viewModel.showAddContributionDialog.value = false
                            viewModel.savingsGoalForContribution.value = null
                        },
                        onContribute = { goalId, amt, note, isWithdrawal ->
                            viewModel.addSavingsContribution(goalId, amt, note, isWithdrawal)
                        }
                    )
                }

                // Bill Dialog
                if (showAddBill) {
                    AddEditBillDialog(
                        bill = billToEdit,
                        currencySymbol = uiState.currencySymbol,
                        onDismiss = {
                            viewModel.showAddBillDialog.value = false
                            viewModel.billToEdit.value = null
                        },
                        onSave = { id, title, amt, cat, freq, dueDay, reminder, colorHex ->
                            viewModel.saveBill(id, title, amt, cat, freq, dueDay, reminder, colorHex)
                        },
                        onDelete = {
                            viewModel.deleteBill(it)
                        }
                    )
                }

                // Task Dialog
                if (showAddTask) {
                    AddEditTaskDialog(
                        task = taskToEdit,
                        initialDate = uiState.selectedDate,
                        onDismiss = {
                            viewModel.showAddTaskDialog.value = false
                            viewModel.taskToEdit.value = null
                        },
                        onSave = { id, title, note, priority, category, reminder, date ->
                            viewModel.saveTask(id, title, note, priority, category, reminder, date)
                        },
                        onDelete = {
                            viewModel.deleteTask(it)
                        }
                    )
                }

                // AI Insights Dialog
                if (showAiInsights) {
                    AIInsightsDialog(
                        insights = uiState.aiInsights,
                        onDismiss = { viewModel.showAiInsightsDialog.value = false },
                        onPerformAction = { insight ->
                            viewModel.showAiInsightsDialog.value = false
                            when (insight.category) {
                                "BUDGET" -> viewModel.showBudgetDialog.value = true
                                "SAVINGS" -> viewModel.selectTab(AppTab.SAVINGS)
                                "SUBSCRIPTION" -> viewModel.selectTab(AppTab.BILLS)
                                else -> viewModel.selectTab(AppTab.ROUTINES)
                            }
                        }
                    )
                }

                // Search & Filter Sheet
                if (showSearchFilter) {
                    SearchFilterSheet(
                        transactions = uiState.allExpenses,
                        routines = uiState.allRoutines,
                        currencySymbol = uiState.currencySymbol,
                        onDismiss = { viewModel.showSearchFilterSheet.value = false },
                        onSelectTransaction = { tx ->
                            viewModel.showSearchFilterSheet.value = false
                            viewModel.openEditExpense(tx)
                        },
                        onSelectRoutine = { r ->
                            viewModel.showSearchFilterSheet.value = false
                            viewModel.openEditRoutine(r)
                        }
                    )
                }

                // Onboarding Dialog
                if (showOnboarding) {
                    OnboardingDialog(
                        currentCurrency = uiState.currencySymbol,
                        onComplete = { cur, budg ->
                            viewModel.completeOnboarding(cur, budg)
                        }
                    )
                }

                // Budget Dialog
                if (showBudgetDialog) {
                    SetBudgetDialog(
                        initialMonthlyLimit = uiState.currentBudget?.monthlyLimit ?: 1800.0,
                        initialDailyTarget = uiState.currentBudget?.dailyTarget ?: 60.0,
                        onDismiss = { viewModel.showBudgetDialog.value = false },
                        onSave = { monthly, daily ->
                            viewModel.saveBudget(monthly, daily)
                        }
                    )
                }

                // Settings Sheet
                if (showSettingsSheet) {
                    SettingsSheet(
                        uiState = uiState,
                        onDismiss = { viewModel.closeSettings() },
                        onOpenBudgetDialog = { viewModel.showBudgetDialog.value = true },
                        onSelectCurrency = { symbol, code -> viewModel.setCurrency(symbol, code) },
                        onExportExpenses = { viewModel.exportTransactionsCsv(it) },
                        onExportRoutines = { viewModel.exportRoutinesCsv(it) },
                        onOpenBlueprints = { viewModel.showBlueprintsDialog.value = true },
                        onOpenSubscriptions = { viewModel.showSubscriptionsDialog.value = true },
                        onOpenSecurity = { viewModel.showSecurityDialog.value = true },
                        onOpenBackupRestore = { viewModel.showBackupRestoreDialog.value = true },
                        onOpenLegal = { type -> viewModel.showLegalDialog.value = type },
                        onLockAppNow = { viewModel.lockApp() }
                    )
                }

                // Starter Blueprints Dialog
                if (showBlueprints) {
                    BlueprintsDialog(
                        onDismiss = { viewModel.showBlueprintsDialog.value = false },
                        onInstallBlueprint = { pack ->
                            viewModel.installBlueprintPack(pack)
                        }
                    )
                }

                // Subscriptions & Recurring Bills Dialog
                if (showSubscriptions) {
                    SubscriptionsDialog(
                        bills = uiState.bills,
                        currencySymbol = uiState.currencySymbol,
                        onDismiss = { viewModel.showSubscriptionsDialog.value = false },
                        onAddSubscription = { title, amt, cat, freq, dueDay ->
                            viewModel.saveBill(
                                title = title,
                                amount = amt,
                                category = cat,
                                frequency = freq,
                                dueDay = dueDay,
                                reminder = true,
                                colorHex = cat.colorHex
                            )
                        },
                        onDeleteSubscription = { bill ->
                            viewModel.deleteBill(bill)
                        },
                        onLogExpense = { title, amt, cat ->
                            viewModel.saveExpense(
                                title = title,
                                amount = amt,
                                type = TransactionType.EXPENSE,
                                category = cat,
                                paymentMethod = PaymentMethod.CARD,
                                date = LocalDate.now(),
                                note = "Auto-logged from Subscription Hub"
                            )
                        }
                    )
                }

                // Security Passcode Dialog
                if (showSecurity) {
                    SecurityLockDialog(
                        isPinEnabled = uiState.isPinEnabled,
                        currentPinHash = uiState.userPin,
                        onDismiss = { viewModel.showSecurityDialog.value = false },
                        onSavePinConfig = { enabled, pinHash ->
                            viewModel.setPinSecurity(enabled, pinHash)
                        }
                    )
                }

                // Backup & Device Migration Dialog
                if (showBackupRestore) {
                    BackupRestoreDialog(
                        onDismiss = { viewModel.showBackupRestoreDialog.value = false },
                        onExportJson = { ctx ->
                            viewModel.exportFullJsonBackup(ctx)
                        },
                        onRestoreDemoData = {
                            viewModel.restoreDemoShowcaseData()
                        },
                        onRestoreFromJson = { json ->
                            viewModel.restoreFromJsonBackup(json)
                        }
                    )
                }

                // Legal Dialog (Privacy Policy / Terms)
                showLegal?.let { legalType ->
                    LegalDialog(
                        type = legalType,
                        onDismiss = { viewModel.showLegalDialog.value = null }
                    )
                }
            }
        }
    } // end DayFlowTheme
}
