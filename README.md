# DayFlow | Personal Finance and Daily Planner Android App
*Android App Source Code (Kotlin, Jetpack Compose, Room SQLite, Material Design 3)*

---

## 🌟 Executive Overview
**DayFlow** is an all-in-one productivity suite and financial management engine for Android. Designed following Google's latest **Material Design 3** and modern Android architectural standards, this template combines:
- **Daily Planner & Habit Engine**: Streak tracking, time-of-day filtering, priority tasks, and completion analytics.
- **Full Double-Entry Financial Ledger**: Multi-account support (Cash, Bank, Cards, Savings, Investment), multi-currency formatting, custom categories with customizable vector icons and color swatches.
- **Budgets & Spending Thresholds**: Monthly budgets, daily targets, category caps, and progress monitors.
- **Smart Financial Insights & Forecasting**: Heuristics calculating DayFlow Wellness score, runway projections, spending leak alerts, and streak achievements, with optional AI integration.
- **Savings Goals & Milestones**: Target tracking, auto-progress percentages, and contribution logging.
- **Recurring Bills & Subscription Hub**: Due-date countdowns, payment method linking, and status tracking.
- **Security & Privacy First**: 4-digit PIN app lock, 100% offline local Room SQLite persistence, JSON data export/import, and CSV transaction reports.
- **Starter Templates**: Pre-packaged habit and budget templates for quick setup.

---

## 🚀 Key Features & Highlights

### 1. Finance & Multi-Account Engine
- **Multi-Account Tracking**: Add Checking, Savings, Credit Cards, Cash, and Investments with custom starting balances and real-time net worth calculation.
- **Account-to-Account Transfers**: Instant balance re-allocations with automatic dual-entry transaction logging.
- **Custom Categories**: Add, edit, and manage custom income and expense categories with dynamic color pickers and icon selector.
- **Integer Cents Precision**: All financial math is protected by the `Money` value object (`Long` integer cents) to prevent floating-point rounding errors.

### 2. Daily Routine & Task Planner
- **Dynamic Time-of-Day Segments**: Morning, Afternoon, Evening, and Anytime habit filters.
- **Streak Tracker & Completion Analytics**: Calculates consecutive days active, completion percentage, and weekly success rate.
- **Priority Tasks**: Daily to-do items with High, Medium, Low priority tags, due dates, and swipeable completion toggles.

### 3. Smart Analytics & DayFlow Score
- **DayFlow Health Score (0–100)**: Heuristic algorithmic score evaluating financial surplus, budgeting discipline, and routine completion rates.
- **Category Spend Distribution**: Interactive category breakdown showing monthly allocation vs budget caps.
- **Financial Insights**: Detects subscriptions due, budget overflows, and savings milestones.

### 4. Customization & Configuration
- **Centralized Config (`AppConfig.kt`)**: Configure app branding, currency, default features, package IDs, and support links in a single file.
- **Starter Blueprints**: Pre-configured habit and budget packs (e.g., Financial Freedom Pack, Student Starter, Entrepreneur Routine).
- **Data Portability**: Full JSON backup & restore pipeline, plus CSV export for Excel / Google Sheets analysis.
- **Privacy Lock**: 4-digit PIN security lock with instant lockout and custom PIN change options.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0+ (100% Kotlin)
- **UI Framework**: Jetpack Compose + Material 3
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern + Clean Architecture
- **Local Persistence**: Android Room Database (SQLite) v3 with full schema type converters
- **User Preferences**: Jetpack DataStore Preferences
- **Asynchronous Execution**: Kotlin Coroutines (`StateFlow`, `SharedFlow`, `combine`, `flatMapLatest`)
- **Lifecycle Management**: `androidx.lifecycle.compose.collectAsStateWithLifecycle`
- **File & Data Sharing**: Android `FileProvider` with CSV and JSON exporters
- **Notification Engine**: Android Notification Channels (`POST_NOTIFICATIONS` ready for Android 13+)

---

## 📂 Project Directory Structure

```
app/src/main/java/com/example/
├── AppConfig.kt                        # Centralized configuration
├── MainActivity.kt                     # Main entry activity & navigation host
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt              # Room database definition (v3)
│   │   ├── AccountDao.kt               # Account entities & balance queries
│   │   ├── BillDao.kt                  # Recurring bills DAO
│   │   ├── BudgetDao.kt                # Monthly budget DAO
│   │   ├── CategoryBudgetDao.kt        # Category-specific budget DAO
│   │   ├── CustomCategoryDao.kt        # User-defined categories DAO
│   │   ├── ExpenseDao.kt               # Financial transactions DAO
│   │   ├── RoutineDao.kt               # Habits & routines DAO
│   │   ├── SavingsGoalDao.kt           # Savings targets & contributions DAO
│   │   ├── TaskDao.kt                  # Daily task planner DAO
│   │   └── preferences/
│   │       └── UserPreferencesRepository.kt # DataStore settings repository
│   ├── model/
│   │   ├── Account.kt                  # Account entities & models
│   │   ├── Bill.kt                     # Recurring bill models
│   │   ├── Budget.kt                   # Budget & category budget entities
│   │   ├── CustomCategory.kt           # User-defined category model
│   │   ├── Expense.kt                  # Transaction & category enums
│   │   ├── Money.kt                    # Integer-cents financial math value object
│   │   ├── Routine.kt                  # Habits, categories & streak models
│   │   └── Task.kt                     # Task entities & priority enums
│   ├── repository/
│   │   └── DayFlowRepository.kt        # Unified Single Source of Truth repository
│   └── service/
│       ├── AIService.kt                # Smart insight & DayFlow score engine
│       ├── BackupManager.kt            # JSON backup, restore & validation engine
│       ├── BillingService.kt           # Decoupled in-app billing abstraction
│       ├── ProFeatureManager.kt        # Feature entitlement rules
│       └── NotificationHelper.kt       # Notification channels & reminders
└── ui/
    ├── components/                     # Reusable UI components & PinLockScreen
    ├── dialogs/                        # Material 3 dialogs, sheets & settings
    ├── screens/                        # Tab composables (Today, Finance, Savings, Bills, Routines, Analytics)
    ├── theme/                          # Material 3 theme, colors, shapes, typography
    └── viewmodel/
        └── MainViewModel.kt            # Core ViewModel handling business logic & UI state
```

---

## ⚙️ Quick Start & Customization Guide

### 1. Rebranding the App
Open `/app/src/main/java/com/example/AppConfig.kt` to customize brand values:
```kotlin
object AppConfig {
    const val APP_NAME = "DayFlow"
    const val APP_SLOGAN = "Smart Personal Finance & Daily Planner"
    const val DEFAULT_CURRENCY_SYMBOL = "$"
    const val DEFAULT_CURRENCY_CODE = "USD"
    const val SUPPORT_EMAIL = "support@yourdomain.com"
    const val PRIVACY_POLICY_URL = "https://yourdomain.com/privacy"
    const val TERMS_OF_SERVICE_URL = "https://yourdomain.com/terms"
}
```

### 2. Changing Application ID & Namespace
In `app/build.gradle.kts`:
```kotlin
android {
    namespace = "com.example" // Internal code namespace
    defaultConfig {
        applicationId = "com.yourcompany.dayflow" // Your unique Play Store ID
        versionCode = 1
        versionName = "1.0.0"
    }
}
```

### 3. Running & Compiling via Gradle Wrapper

**On macOS / Linux:**
```bash
# Clean project
./gradlew clean

# Build Debug APK
./gradlew assembleDebug

# Run Unit Tests
./gradlew testDebugUnitTest

# Build Release APK
./gradlew assembleRelease

# Build Release App Bundle (AAB for Google Play Store)
./gradlew bundleRelease
```

**On Windows:**
```cmd
# Clean project
gradlew.bat clean

# Build Debug APK
gradlew.bat assembleDebug

# Run Unit Tests
gradlew.bat testDebugUnitTest

# Build Release APK
gradlew.bat assembleRelease

# Build Release App Bundle
gradlew.bat bundleRelease
```

### 4. Release Keystore Configuration
Set the following environment variables before building signed release artifacts:
```bash
export KEYSTORE_PATH="/path/to/your/upload-keystore.jks"
export STORE_PASSWORD="your_keystore_password"
export KEY_ALIAS="your_key_alias"
export KEY_PASSWORD="your_key_password"
```

---

## 📚 Documentation

For complete installation, customization, AI, billing, release and troubleshooting instructions, see the **documentation/** folder:

- **DOCUMENTATION.md** - Complete product overview and architecture
- **INSTALLATION.md** - Android Studio setup and build instructions
- **CUSTOMIZATION.md** - Branding, package name, and configuration guide
- **AI_SETUP.md** - Local AI and optional cloud AI integration
- **BILLING_SETUP.md** - Google Play Billing configuration (optional)
- **RELEASE_BUILD.md** - Release APK and AAB build instructions
- **TROUBLESHOOTING.md** - Common issues and solutions

---

## 🔧 Compatibility

### Platform
- **Platform**: Android
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 36 (Android 16)
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room SQLite
- **Architecture**: MVVM + Repository

### System Requirements
- **Android Studio**: Koala (2024.1+) or Ladybug (2024.2+) recommended
- **JDK**: Java 17 or Java 21
- **Gradle**: 8.8+
- **Kotlin**: 2.0+
- **Compose**: 1.7+

---

## 📄 License & Commercial Rights

This item is distributed under the applicable Envato Market license selected by the buyer (Regular License or Extended License).
Included for commercial distribution, white-labeling, reskinning, and deployment on the Google Play Store.

---

## 🌟 Optional AI Integration

DayFlow includes optional AI-powered financial insight capabilities. Cloud AI connectivity requires the buyer to configure their own API credentials for the selected third-party AI provider.

**Important**: Third-party AI services may charge separately for API usage. These costs are not included with the purchase of this item.

See **AI_SETUP.md** for detailed configuration instructions.

---

## 💳 Optional Billing Integration

Google Play Billing is optional and not required for the application to function. Buyers can configure their own Google Play Billing products if they want to monetize their published application.

The application does not connect Envato licensing to Google Play subscriptions.

See **BILLING_SETUP.md** for detailed configuration instructions.
#   D a y F l o w - v 1 . 0 . 0  
 #   D a y F l o w - v 1 . 0 . 0  
 