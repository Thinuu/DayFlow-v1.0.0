# DayFlow | Personal Finance and Daily Planner Android App

**Product name:** DayFlow | Personal Finance and Daily Planner Android App  
**Version:** 1.0.0 (`versionCode` 1)  
**Platform:** Android (Kotlin, Jetpack Compose, Material 3, Room, DataStore)

This guide matches the source in this package. Related procedures: [INSTALLATION.md](INSTALLATION.md), [APPCONFIG.md](APPCONFIG.md), [CUSTOMIZATION.md](CUSTOMIZATION.md), [AI_SETUP.md](AI_SETUP.md), [BILLING_SETUP.md](BILLING_SETUP.md), [RELEASE_BUILD.md](RELEASE_BUILD.md), [TROUBLESHOOTING.md](TROUBLESHOOTING.md).

---

## 1. Product overview

DayFlow is an offline-first personal finance and daily planner app. Users track accounts, transactions, budgets, savings goals, bills, habits, and tasks on-device in Room SQLite.

It is not a cloud banking product and is not entirely cloud-AI powered. Insights in `AIService.generateOfflineInsights()` are local calculations. Optional cloud AI is buyer-configured (see [AI_SETUP.md](AI_SETUP.md)).

Envato Regular vs Extended license terms apply to how you use the item commercially. They do **not** restrict which features compile or run in this source.

---

## 2. Architecture

- **MVVM:** `MainViewModel` exposes `DayFlowUiState` via Kotlin `StateFlow`. Screens are Compose functions.
- **Repository:** `DayFlowRepository` is the single source of truth over Room DAOs.
- **Preferences:** `UserPreferencesRepository` uses DataStore for currency, PIN flags, notification toggles, and optional Play Billing preference.
- **UI:** Jetpack Compose + Material 3 (`Theme.kt`, `Color.kt`).
- **Navigation:** Tab host in `MainActivity` (Today, Finance, Savings, Bills, Routines, Analytics). No Navigation-Compose graph is required.

---

## 3. Project structure

```
app/src/main/java/com/example/
├── AppConfig.kt
├── MainActivity.kt
├── data/
│   ├── local/          # Room database, DAOs, DataStore
│   ├── model/          # Entities and Money
│   ├── repository/     # DayFlowRepository
│   └── service/        # AIService, BackupManager, BillingService, NotificationHelper, ProFeatureManager
└── ui/
    ├── components/
    ├── dialogs/
    ├── screens/
    ├── theme/
    └── viewmodel/
```

Gradle: `build.gradle.kts`, `app/build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `gradle/wrapper/`, `gradle/libs.versions.toml`.

---

## 4. Toolchain (from Gradle)

| Setting | Location | Value |
|---------|----------|--------|
| Kotlin | `gradle/libs.versions.toml` | 2.2.10 |
| AGP | `gradle/libs.versions.toml` | 9.1.1 |
| Gradle wrapper | `gradle/wrapper/gradle-wrapper.properties` | 9.3.1 |
| minSdk | `app/build.gradle.kts` | 24 |
| targetSdk | `app/build.gradle.kts` | 36 |
| compileSdk | `app/build.gradle.kts` | 36 (release 36, minor API 1) |
| applicationId | `app/build.gradle.kts` | `com.yourcompany.dayflow` |
| namespace | `app/build.gradle.kts` | `com.example` |
| versionName / versionCode | `app/build.gradle.kts` | 1.0.0 / 1 |
| Compose BOM | `gradle/libs.versions.toml` | 2024.09.00 |
| Room | `gradle/libs.versions.toml` | 2.7.0 |
| DataStore | `gradle/libs.versions.toml` | 1.1.7 |
| Java compatibility | `app/build.gradle.kts` | 11 |

Use Android Studio with JDK 17 or 21 for Gradle.

---

## 5. Build instructions

From the project root:

```bash
./gradlew clean
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew assembleRelease
./gradlew bundleRelease
```

Windows: `gradlew.bat` instead of `./gradlew`. Details: [INSTALLATION.md](INSTALLATION.md), [RELEASE_BUILD.md](RELEASE_BUILD.md).

---

## 6. Database

Room database name: `dayflow_database` (`AppDatabase.kt`).

**Schema version:** 3 (`@Database(..., version = 3)`).  
**Backup JSON schema:** `AppConfig.BACKUP_SCHEMA_VERSION` = 1.

Entities: accounts, expenses (transactions), budgets, category_budgets, savings_goals, savings_contributions, bills, routines, routine_completions, tasks, custom_categories.

Current code uses `.fallbackToDestructiveMigration()`. Custom schema changes require a new Room version and a migration if you need to keep user data.

Financial amounts in the UI often use `Double`; `Money.kt` provides integer-cent helpers for tests and precise math.

---

## 7. Backup / restore

`BackupManager` writes schema-versioned JSON (prefix `dayflow_backup_`) and can restore after validation. CSV export is available from Settings for transactions. UI: `BackupRestoreDialog`.

---

## 8. Notifications

`NotificationHelper.createNotificationChannels()` registers:

- `dayflow_habits_channel` — habit reminders  
- `dayflow_bills_channel` — bill due alerts  
- `dayflow_budget_channel` — budget threshold alerts  

Manifest permission: `POST_NOTIFICATIONS` (Android 13+). Runtime grant is required on API 33+.

---

## 9. Customization

See [CUSTOMIZATION.md](CUSTOMIZATION.md). Primary file: `AppConfig.kt` (name, slogan, currency, subscription presets, starter routines, Play product ID placeholders, support URLs). Launcher name: `app/src/main/res/values/strings.xml`. Theme: `ui/theme/Color.kt`.

---

## 10. AI integration

See [AI_SETUP.md](AI_SETUP.md).

**Optional AI Integration**

DayFlow includes optional AI-powered financial insight capabilities.
Cloud AI connectivity requires the buyer to configure their own API credentials for the selected third-party AI provider.

Third-party AI services may charge separately for API usage.
These costs are not included with the purchase of this item.

Local insights do not use those APIs.

---

## 11. Billing integration

See [BILLING_SETUP.md](BILLING_SETUP.md).

`BillingService` + `NoOpBillingService` (default) + `GooglePlayBillingService` (template). Google Play is optional. Envato licensing is not connected to Play subscriptions. Product IDs in `AppConfig` are placeholders, not live catalog IDs.

`ProFeatureManager` always grants access; it is not an Envato lock.

---

## 12. Release APK and AAB

- APK: `./gradlew assembleRelease` → `app/build/outputs/apk/release/`
- AAB: `./gradlew bundleRelease` → `app/build/outputs/bundle/release/`

Signing uses buyer-provided keystore via environment variables (`KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`). No keystore is shipped. Unsigned/debug-signed release is used when those variables are unset (AGP default debug signing). See [RELEASE_BUILD.md](RELEASE_BUILD.md).

---

## 13. Troubleshooting

See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for Gradle, SDK, Room, billing, and AI issues.

---

## 14. Version information

- Marketplace title: DayFlow | Personal Finance and Daily Planner Android App  
- Version: 1.0.0  
- Version code: 1  
- minSdk 24, targetSdk 36, Kotlin 2.2.10, Gradle 9.3.1  
