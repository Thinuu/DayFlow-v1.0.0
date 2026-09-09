# User Walkthrough

A screen-by-screen guide to every feature in DayFlow, written from the user's perspective. Use this as the basis for your App Store description, support documentation, or demo script.

---

## First launch — Onboarding

On a fresh install the app seeds demo data automatically (accounts, transactions, routines, goals, bills) so the UI is populated from the start. The Onboarding dialog appears the first time, letting the user:

- Select their preferred currency from a list of 12 supported options.
- Set their starting monthly budget.

Selections are saved to DataStore and persist across restarts.

---

## Tab 1 — Today

The home screen for the current day.

**Date strip** — Horizontal scrollable week strip at the top. Tap any day to jump to it. All widgets below filter to the selected date.

**DayFlow Score** — A 0–100 wellness score calculated from routine completion rate, best streak, and budget discipline. Updates as the day progresses.

**Daily summary cards**
- Today's total expenses and income
- Routine completion rate (e.g. "3 / 6 done")
- Current month's spend vs budget

**Today's Routines**
- List of habits scheduled for the selected day (filtered by `targetDaysOfWeekMask`).
- Tap the circle to mark complete / incomplete. Completion is stored in `routine_completions`.
- Streak badge shows consecutive-day count.
- Long-press or tap Edit to open the routine editor.
- FAB (+ button) adds a new routine.

**Today's Transactions**
- Expenses and income logged for the selected date.
- Tap an entry to edit. Swipe-to-delete or use the edit dialog's delete button.
- FAB opens the Add Transaction dialog.

**Today's Tasks**
- Tasks due on the selected date, sorted by priority.
- Tap the checkbox to complete. Tap the title to edit.
- Priority badge colours: Low (grey) → Medium (blue) → High (amber) → Urgent (red).

**Quick actions** (toolbar area)
- 🔍 Search/Filter — full-text search across all transactions and routines.
- 💡 AI Insights — opens the insights panel.
- ⚙️ Settings — opens the settings sheet.
- 🔒 Lock — activates PIN lock if enabled.

---

## Tab 2 — Finance

Financial ledger and account management.

**Account cards** — Scrollable row showing each account with current balance, total income, and total expense. Tap an account card to filter the transaction list. "Add Account" FAB.

**Transaction list** — All transactions sorted by date descending. Filtered by selected account (if any) and by current month. Tap to edit.

**Add/Edit Transaction dialog**
- Title, amount, type (Expense / Income / Transfer), category, payment method, date, note.
- Category picker shows `ExpenseCategory` enum entries plus any custom categories.
- Account defaults to the marked-default account.

**Search / Filter sheet** — Opened from the search icon. Filter by date range, category, type, or keyword.

**Account Transfer** — Available from the Finance screen overflow menu. Moves a specified amount between two accounts, creating paired debit/credit expense entries.

---

## Tab 3 — Savings

Savings goals with progress tracking.

**Goal cards** — Each card shows:
- Goal name and icon.
- Progress ring (percentage of target reached).
- Current amount / target amount.
- Days remaining to target date (if set).

**Add/Edit Goal dialog** — Name, target amount, current amount, months to target, colour.

**Contribution dialog** — Opened via the deposit button on a goal card. Enter an amount with an optional note. Toggle "Withdrawal" to remove funds.

All contributions are stored in `savings_contributions`. The current balance is calculated from `currentAmount + sum(contributions)`.

---

## Tab 4 — Bills

Recurring bills and subscription manager.

**Bill list** — Sorted by `dueDayOfMonth`. Each entry shows title, amount, next due day, and paid status.

**Mark as Paid / Unpaid** — Toggle button on each bill card. Updates `isPaidThisCycle`. When `autoLogExpense = true`, marking paid also creates an expense transaction automatically.

**Add/Edit Bill dialog** — Title, amount, category, frequency (One-time / Weekly / Bi-Weekly / Monthly / Quarterly / Yearly), due day, reminder toggle, colour.

**Subscriptions dialog** — Opened from the Bills overflow. Shows a preset catalog of common subscriptions (Netflix, Spotify, etc. — from `AppConfig.DEFAULT_SUBSCRIPTION_PRESETS`). One tap adds to the bills list.

---

## Tab 5 — Routines

Habit tracker and streak manager.

**Week strip** — Same date selector as the Today tab. Completions shown for the selected date.

**Routine list** — All non-archived routines for the selected day. Each card shows:
- Routine title and category icon.
- Scheduled time.
- Current streak (consecutive days completed).
- Completion toggle.

**Mark all done / reset** — Bulk action via the toolbar.

**Add/Edit Routine dialog** — Title, note, category (`RoutineCategory`), time, days of week (bitmask selector), time of day (Morning / Afternoon / Evening / Night), icon, colour.

**Starter Blueprints** — Opens a curated pack installer. Each pack (`BlueprintPack` from `AppConfig.DEFAULT_STARTER_ROUTINES`) installs a set of routines in one tap. Optionally sets a budget target.

---

## Tab 6 — Analytics

Spending insights and data export.

**Monthly summary** — Total income, total expense, net savings, savings rate percentage.

**Category breakdown** — Horizontal bar chart of spending by `ExpenseCategory`. Shows amount and percentage of total.

**Daily spending chart** — Bar chart of expense totals per day in the selected month.

**Achievements** — Badge cards for milestone unlocks (first routine completed, 3-day streak, 7-day streak, 5 transactions logged, etc.).

**DayFlow Score history** — Score breakdown showing contribution from routines, streaks, and budget.

**Export**
- CSV export of all transactions (opens share sheet).
- CSV export of all routines.
- Full JSON backup (opens share sheet). Same format as Settings → Backup.

---

## Settings sheet

Opened from the Today tab toolbar.

| Setting | What it does |
|---------|-------------|
| Currency | Picker from `AppConfig.SUPPORTED_CURRENCIES` — persists to DataStore |
| Monthly Budget | Opens `SetBudgetDialog` — updates `budgets` table for current month |
| Security / PIN | Enable 4-digit PIN lock. Stored hashed in DataStore. |
| Notifications | Toggle; persisted to DataStore |
| Backup & Restore | Export JSON / import JSON backup |
| Starter Blueprints | Install habit packs |
| Subscriptions | Manage recurring bills |
| Legal / About | Privacy policy, terms of service, version info |
| Lock Now | Immediately activates PIN lock screen |

---

## PIN lock screen

When PIN lock is enabled and the user taps "Lock Now" (or the app returns from background with lock triggered), a full-screen PIN entry appears. Enter the correct 4-digit code to unlock. Incorrect entry highlights the dots in red; no lockout timer in the default implementation.

---

## AI Insights

Opened from the Today tab toolbar (💡 icon) or from the Analytics tab.

Shows up to 6 insight cards generated by `AIService.generateOfflineInsights()`. Each card displays:
- Title and description.
- Urgency badge (INFO / WARNING / CELEBRATION).
- Optional action button that navigates to the relevant screen.

Insights are derived entirely on-device from the user's own data — no network call is made. Categories: BUDGET, SAVINGS, SUBSCRIPTION, HABIT.

---

## Search & Filter sheet

Full-text search across all transactions and routines simultaneously. Results update as the user types. Tap a result to jump to the edit dialog for that item.

---

## Backup & Restore dialog

- **Export JSON** — generates a complete `dayflow_backup_<date>.json` via `BackupManager.createJsonBackup()` and opens the system share sheet.
- **Import JSON** — accepts a JSON file, runs `BackupManager.validateBackupJson()` to preview record counts, then calls `restoreFromJson()` to insert records using `REPLACE` conflict strategy.
- **Export CSV** — transactions only, opens share sheet.
