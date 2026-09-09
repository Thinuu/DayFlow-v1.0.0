# AppConfig Reference

`app/src/main/java/com/example/AppConfig.kt`

This is the **single file you edit to configure and rebrand the entire application**. Every section below documents the exact fields in that file with their current default values, what each field controls, and how to change it safely.

No UI code, no layout files, and no build scripts need to change for the configurations marked ✅ below.

---

## Quick-change summary

| What you want to do | Field(s) to change | File |
|---------------------|--------------------|------|
| Change app name | `APP_NAME` + `app_name` in `strings.xml` | `AppConfig.kt` + `strings.xml` |
| Change default currency | `DEFAULT_CURRENCY_SYMBOL` + `DEFAULT_CURRENCY_CODE` | `AppConfig.kt` |
| Add a new currency to the picker | Add entry to `SUPPORTED_CURRENCIES` | `AppConfig.kt` |
| Change Play Store app ID | `APPLICATION_ID` + `applicationId` in `build.gradle.kts` | Both files |
| Disable AI insights | `FEATURE_AI_INSIGHTS_ENABLED = false` | `AppConfig.kt` |
| Disable PIN lock | `FEATURE_SECURITY_PIN_ENABLED = false` | `AppConfig.kt` |
| Enable the paywall UI | `FEATURE_PREMIUM_PAYWALL_ENABLED = true` | `AppConfig.kt` |
| Change Play Billing product IDs | `PLAY_STORE_PRO_*` constants | `AppConfig.kt` |
| Change default monthly budget | `DEFAULT_MONTHLY_BUDGET` | `AppConfig.kt` |
| Add a default custom category | Add entry to `DEFAULT_CUSTOM_CATEGORIES` | `AppConfig.kt` |
| Change subscription presets | Edit entries in `DEFAULT_SUBSCRIPTION_PRESETS` | `AppConfig.kt` |
| Change starter habit packs | Edit entries in `DEFAULT_STARTER_ROUTINES` | `AppConfig.kt` |
| Update support / legal URLs | `SUPPORT_EMAIL`, `*_URL` constants | `AppConfig.kt` |

---

## Section 1 — Application identity

```kotlin
const val APP_NAME         = "DayFlow"
const val APP_SLOGAN       = "Smart Personal Finance & Daily Planner"
const val APP_VERSION_NAME = "1.0.0"
const val APP_VERSION_CODE = 1
const val APPLICATION_ID   = "com.yourcompany.dayflow"
```

| Field | Default | What it controls |
|-------|---------|-----------------|
| `APP_NAME` | `"DayFlow"` | In-app display name used in backup metadata and config references |
| `APP_SLOGAN` | `"Smart Personal Finance & Daily Planner"` | Subtitle shown on onboarding and About screen |
| `APP_VERSION_NAME` | `"1.0.0"` | Human-readable version string — keep in sync with `app/build.gradle.kts` |
| `APP_VERSION_CODE` | `1` | Integer build counter — keep in sync with `app/build.gradle.kts` |
| `APPLICATION_ID` | `"com.yourcompany.dayflow"` | Runtime mirror of the Gradle `applicationId` (used in FileProvider authority, deep links) |

**Important — APPLICATION_ID vs launcher label:**
- The launcher label shown on the home screen comes from `app/src/main/res/values/strings.xml` → `app_name`, not from `APP_NAME`. Change both.
- The Google Play / device identifier comes from `android.defaultConfig.applicationId` in `app/build.gradle.kts`. Change that **and** `APPLICATION_ID` together.

**To rebrand the app name:**
1. `AppConfig.APP_NAME = "YourAppName"`
2. `strings.xml` → `<string name="app_name">YourAppName</string>`

**To change the applicationId before publishing:**
1. `app/build.gradle.kts` → `applicationId = "com.yourcompany.yourapp"`
2. `AppConfig.APPLICATION_ID = "com.yourcompany.yourapp"`
3. If using Firebase: regenerate `google-services.json` with the new package name.

See [PACKAGE_RENAME.md](PACKAGE_RENAME.md) for a full walkthrough.

---

## Section 2 — Brand colour palette

```kotlin
const val COLOR_PRIMARY_HEX         = 0xFF8B5CF6  // Radiant Purple
const val COLOR_SECONDARY_HEX       = 0xFF10B981  // Emerald Green
const val COLOR_ACCENT_CORAL_HEX    = 0xFFF43F5E  // Coral Red
const val COLOR_GOLD_VIP_HEX        = 0xFFFFD700  // VIP Gold
const val COLOR_SURFACE_DARK_HEX    = 0xFF1C1B1F
const val COLOR_BACKGROUND_DARK_HEX = 0xFF0F0E17
```

These constants are **documentation tokens** for buyer reference. They reflect the design intent and are used as defaults in seed data (account colours, goal colours, etc.).

The Compose theme colours that the UI actually renders are in `app/src/main/java/com/example/ui/theme/Color.kt`. If you change the visual theme, update `Color.kt` — and optionally update these constants to stay in sync.

**Format:** `0xFF` + 6-digit hex RGB. The `0xFF` alpha prefix means fully opaque.

---

## Section 3 — Default currency and localisation

```kotlin
const val DEFAULT_CURRENCY_SYMBOL = "$"
const val DEFAULT_CURRENCY_CODE   = "USD"
```

These are the out-of-box currency settings applied on fresh installs and used as the fallback when no saved preference exists.

| Field | Default | Notes |
|-------|---------|-------|
| `DEFAULT_CURRENCY_SYMBOL` | `"$"` | Displayed in all amount labels |
| `DEFAULT_CURRENCY_CODE` | `"USD"` | ISO 4217 code; stored in DataStore alongside symbol |

**To change the default currency:**
```kotlin
const val DEFAULT_CURRENCY_SYMBOL = "€"
const val DEFAULT_CURRENCY_CODE   = "EUR"
```
Existing users keep their saved preference. Only fresh installs use these defaults.

### Supported currencies list

`SUPPORTED_CURRENCIES` is a `List<CurrencyOption>` that populates the currency picker in Settings and Onboarding.

```kotlin
data class CurrencyOption(
    val symbol: String,      // e.g. "€"
    val code: String,        // ISO 4217, e.g. "EUR"
    val displayName: String  // e.g. "Euro (€)"
)
```

Current entries (12):

| Symbol | Code | Display name |
|--------|------|-------------|
| `$` | USD | US Dollar ($) |
| `€` | EUR | Euro (€) |
| `£` | GBP | British Pound (£) |
| `¥` | JPY | Japanese Yen (¥) |
| `₹` | INR | Indian Rupee (₹) |
| `C$` | CAD | Canadian Dollar (C$) |
| `A$` | AUD | Australian Dollar (A$) |
| `CHF` | CHF | Swiss Franc (CHF) |
| `R$` | BRL | Brazilian Real (R$) |
| `₱` | PHP | Philippine Peso (₱) |
| `₩` | KRW | South Korean Won (₩) |
| `zł` | PLN | Polish Złoty (zł) |

**To add a currency:**
```kotlin
// Add one line anywhere in the list:
CurrencyOption("kr", "SEK", "Swedish Krona (kr)"),
```
No other file needs to change.

---

## Section 4 — Feature flags

Each flag is a `const val Boolean`. Setting a flag to `false` disables the corresponding module globally.

```kotlin
const val FEATURE_AI_INSIGHTS_ENABLED       = true
const val FEATURE_GEMINI_ONLINE_ENABLED     = false
const val FEATURE_SECURITY_PIN_ENABLED      = true
const val FEATURE_DATA_EXPORT_ENABLED       = true
const val FEATURE_DATA_IMPORT_ENABLED       = true
const val FEATURE_NOTIFICATIONS_ENABLED     = true
const val FEATURE_PREMIUM_PAYWALL_ENABLED   = false
const val FEATURE_STARTER_BLUEPRINTS_ENABLED= true
const val FEATURE_CUSTOM_CATEGORIES_ENABLED = true
const val FEATURE_ACCOUNT_TRANSFERS_ENABLED = true
```

| Flag | Default | What it controls |
|------|---------|-----------------|
| `FEATURE_AI_INSIGHTS_ENABLED` | `true` | Shows the AI Insights button and panel. Set `false` to hide entirely. |
| `FEATURE_GEMINI_ONLINE_ENABLED` | `false` | Reserved for cloud AI. **No Gemini client is shipped** — set this `true` only after you implement your own `AIProvider`. See [AI_SETUP.md](AI_SETUP.md). |
| `FEATURE_SECURITY_PIN_ENABLED` | `true` | Shows the PIN lock option in Settings. Set `false` to remove the security section. |
| `FEATURE_DATA_EXPORT_ENABLED` | `true` | Shows CSV/JSON export buttons in Settings and Analytics. |
| `FEATURE_DATA_IMPORT_ENABLED` | `true` | Shows the JSON restore button in Backup & Restore dialog. |
| `FEATURE_NOTIFICATIONS_ENABLED` | `true` | Shows the notification toggle in Settings and enables notification scheduling. |
| `FEATURE_PREMIUM_PAYWALL_ENABLED` | `false` | Shows the optional Pro/paywall dialog. Off by default — features are not gated. Enable only if you wire Google Play Billing. See [BILLING_SETUP.md](BILLING_SETUP.md). |
| `FEATURE_STARTER_BLUEPRINTS_ENABLED` | `true` | Shows the Starter Blueprints button (habit pack installer). |
| `FEATURE_CUSTOM_CATEGORIES_ENABLED` | `true` | Shows the custom category manager in Settings. |
| `FEATURE_ACCOUNT_TRANSFERS_ENABLED` | `true` | Shows the Account Transfer option in the Finance screen. |

**Important notes:**
- These flags are read by `UserPreferencesRepository` as initial defaults. If a user has already saved a preference (e.g. `isAiEnabled`), their stored value takes precedence over the flag on subsequent launches.
- `FEATURE_PREMIUM_PAYWALL_ENABLED = false` does not lock features — it only hides the paywall UI. `ProFeatureManager` always grants access regardless of Pro status.
- `FEATURE_GEMINI_ONLINE_ENABLED = true` does nothing on its own — you must also implement a cloud `AIProvider`.

---

## Section 5 — Google Play Billing product IDs

```kotlin
const val PLAY_STORE_PRO_LIFETIME_ID = "dayflow_pro_lifetime"
const val PLAY_STORE_PRO_ANNUAL_ID   = "dayflow_pro_annual_sub"
const val PLAY_STORE_PRO_MONTHLY_ID  = "dayflow_pro_monthly_sub"
```

These are **placeholder IDs** — not live Play Store SKUs. They are referenced by `BillingService` to build the product catalog UI.

| Constant | Default value | Play product type |
|----------|--------------|-------------------|
| `PLAY_STORE_PRO_LIFETIME_ID` | `"dayflow_pro_lifetime"` | One-time in-app product |
| `PLAY_STORE_PRO_ANNUAL_ID` | `"dayflow_pro_annual_sub"` | Subscription (yearly base plan) |
| `PLAY_STORE_PRO_MONTHLY_ID` | `"dayflow_pro_monthly_sub"` | Subscription (monthly base plan) |

**To use real Play Billing:**
1. Create products in Google Play Console with these exact IDs (or change the constants to match your chosen IDs).
2. Wire `GooglePlayBillingService` (see [BILLING_SETUP.md](BILLING_SETUP.md)).

The app functions fully with `NoOpBillingService` and these IDs are never sent to Play unless you implement the billing client.

---

## Section 6 — Financial defaults and safety limits

```kotlin
const val DEFAULT_MONTHLY_BUDGET      = 2500.00
const val DEFAULT_DAILY_BUDGET        = 80.00
const val MINIMUM_TRANSACTION_AMOUNT  = 0.01
const val MAXIMUM_TRANSACTION_AMOUNT  = 10_000_000.00
```

| Field | Default | What it controls |
|-------|---------|-----------------|
| `DEFAULT_MONTHLY_BUDGET` | `2500.00` | Fallback monthly budget limit when no budget has been saved for a month |
| `DEFAULT_DAILY_BUDGET` | `80.00` | Fallback daily spend target used in the Today summary card |
| `MINIMUM_TRANSACTION_AMOUNT` | `0.01` | Input validation floor — amounts below this are rejected |
| `MAXIMUM_TRANSACTION_AMOUNT` | `10_000_000.00` | Input validation ceiling — prevents accidental data corruption |

`DEFAULT_MONTHLY_BUDGET` is also used to seed the AI insights budget comparison on first launch before the user sets their own budget. Change it to match a realistic budget for your target market.

---

## Section 7 — Default subscription presets

`DEFAULT_SUBSCRIPTION_PRESETS` is a `List<SubscriptionPreset>` shown in the Subscriptions catalog dialog. Users tap a preset to add it to their bills list in one step.

```kotlin
data class SubscriptionPreset(
    val title: String,           // Display name
    val defaultAmount: Double,   // Pre-filled amount
    val category: ExpenseCategory,
    val frequency: BillFrequency,
    val defaultDueDay: Int,      // 1–31, pre-filled due day
    val iconKey: String,         // Material icon name
    val colorHex: Long           // ARGB packed long, e.g. 0xFFE50914L
)
```

Current presets (8):

| Title | Amount | Category | Due day |
|-------|--------|----------|---------|
| Netflix 4K Premium | $19.99 | ENTERTAINMENT | 12 |
| Spotify Duo / Premium | $14.99 | ENTERTAINMENT | 18 |
| Equinox / Gym Membership | $75.00 | HEALTH | 1 |
| Google One 2TB Cloud | $9.99 | BILLS | 25 |
| High-Speed Fiber Wi-Fi | $65.00 | BILLS | 5 |
| Amazon Prime Membership | $14.99 | SHOPPING | 15 |
| YouTube Premium | $13.99 | ENTERTAINMENT | 22 |
| Apple One Bundle | $19.95 | ENTERTAINMENT | 8 |

**To add a preset:**
```kotlin
SubscriptionPreset(
    title = "Adobe Creative Cloud",
    defaultAmount = 54.99,
    category = ExpenseCategory.EDUCATION,
    frequency = BillFrequency.MONTHLY,
    defaultDueDay = 10,
    iconKey = "brush",
    colorHex = 0xFFFF0000L
),
```

**To remove a preset:** delete the line. Changes take effect on next cold start of the Subscriptions dialog.

**To change an amount:** edit the `defaultAmount`. These are suggestions — the user can edit the amount before saving.

---

## Section 8 — Default starter routines

`DEFAULT_STARTER_ROUTINES` is a `List<StarterRoutineConfig>` shown in the Starter Blueprints installer. Installing a blueprint pack adds these routines in one tap.

```kotlin
data class StarterRoutineConfig(
    val title: String,
    val note: String,
    val category: RoutineCategory,    // WELLNESS | FITNESS | PRODUCTIVITY | MINDFULNESS | LEARNING | PERSONAL | CHORES
    val timeMinutes: Int,             // Minutes from midnight, e.g. 480 = 8:00 AM
    val timeOfDay: TimeOfDay,         // MORNING | AFTERNOON | EVENING | NIGHT
    val iconKey: String,              // Material icon name
    val colorHex: Long,               // ARGB packed long
    val targetDaysMask: Int = 127     // 7-bit bitmask; 127 = every day of week
)
```

Current default routines (5):

| Title | Category | Time | Days |
|-------|----------|------|------|
| Morning Hydration (500ml) | WELLNESS | 00:05 (first 5 min of day) | Every day |
| Deep Focus Work Session | PRODUCTIVITY | 01:00 | Every day |
| Mindful Walk / Exercise | FITNESS | 00:30 | Every day |
| Evening Financial Review | PRODUCTIVITY | 00:10 | Every day |
| Night Reading & Wind-down | LEARNING | 00:20 | Every day |

> **Note on `timeMinutes`:** this is the routine's approximate duration in minutes, not the clock time. Scheduling and clock-time display use the `timeOfDay` enum combined with `timeMinutes` as a relative offset within that period.

**Days mask values:**

| Mask | Days |
|------|------|
| `127` | Mon–Sun (every day) |
| `31` | Mon–Fri (weekdays only) |
| `96` | Sat–Sun (weekends only) |
| `1` | Monday only |
| `64` | Sunday only |

The mask is a 7-bit integer where bit 0 = Monday, bit 1 = Tuesday, … bit 6 = Sunday. `1 shl dayOfWeek.value - 1` for any day.

**To add a routine to the default blueprint:**
```kotlin
StarterRoutineConfig(
    title = "Cold Shower",
    note = "2 minutes cold at the end of your regular shower.",
    category = RoutineCategory.WELLNESS,
    timeMinutes = 5,
    timeOfDay = TimeOfDay.MORNING,
    iconKey = "shower",
    colorHex = 0xFF0EA5E9L,
    targetDaysMask = 127
),
```

---

## Section 9 — Backup and schema settings

```kotlin
const val BACKUP_SCHEMA_VERSION = 1
const val BACKUP_FILE_PREFIX    = "dayflow_backup_"
```

| Field | Default | What it controls |
|-------|---------|-----------------|
| `BACKUP_SCHEMA_VERSION` | `1` | Written into exported JSON backups as `"schemaVersion"`. `BackupManager.validateBackupJson()` reads this to verify compatibility before restoring. Bump this if you change the backup JSON format. |
| `BACKUP_FILE_PREFIX` | `"dayflow_backup_"` | Filename prefix for exported backup files, e.g. `dayflow_backup_2026-09-05.json` |

---

## Section 10 — Default custom categories

`DEFAULT_CUSTOM_CATEGORIES` is a `List<DefaultCategory>` seeded into the `custom_categories` Room table on first launch.

```kotlin
data class DefaultCategory(
    val name: String,    // Display name shown to the user
    val type: String,    // "INCOME" or "EXPENSE" — must match TransactionType.name exactly
    val iconKey: String, // Material icon name key, e.g. "restaurant"
    val colorHex: Long   // ARGB packed long, must include L suffix e.g. 0xFFF97316L
)
```

Current defaults (8):

| Name | Type | Icon key | Colour |
|------|------|----------|--------|
| Side Hustle | INCOME | `work` | `0xFF10B981L` (Emerald) |
| Rental Income | INCOME | `home` | `0xFF06B6D4L` (Cyan) |
| Gift Received | INCOME | `card_giftcard` | `0xFFEC4899L` (Pink) |
| Dining Out | EXPENSE | `restaurant` | `0xFFF97316L` (Orange) |
| Tech & Gadgets | EXPENSE | `devices` | `0xFF6366F1L` (Indigo) |
| Self Care | EXPENSE | `spa` | `0xFFEC4899L` (Pink) |
| Subscriptions | EXPENSE | `subscriptions` | `0xFF8B5CF6L` (Purple) |
| Travel | EXPENSE | `flight_takeoff` | `0xFF14B8A6L` (Teal) |

These categories extend the built-in `ExpenseCategory` enum (FOOD, GROCERIES, TRANSPORT, BILLS, etc.) with additional user-facing options.

**Rules:**
- `type` must be exactly `"INCOME"` or `"EXPENSE"` (case-sensitive, matches `TransactionType.name`).
- `colorHex` must include the `L` suffix (`0xFF10B981L`) to avoid Kotlin Int overflow on ARGB values with the `FF` alpha byte.
- `iconKey` must be a valid Material Symbols icon name (snake_case). Browse at [fonts.google.com/icons](https://fonts.google.com/icons).

**To add a default category:**
```kotlin
DefaultCategory(
    name     = "Pet Care",
    type     = "EXPENSE",
    iconKey  = "pets",
    colorHex = 0xFFF59E0BL  // Amber
),
```

**Seeding behaviour:**
- Categories are inserted by `DayFlowRepository.seedInitialDataIfEmpty()` on first launch only.
- The guard condition checks whether routine ID 1 exists — if it does, seeding is skipped entirely.
- Existing user categories are **never modified** on upgrade. Only fresh installs see changes here.

**Icon key reference (common values):**

| Icon key | Represents |
|----------|-----------|
| `restaurant` | Dining / food |
| `shopping_cart` | Groceries / shopping |
| `directions_car` | Transport |
| `receipt_long` | Bills |
| `fitness_center` | Gym / exercise |
| `local_hospital` | Health / medical |
| `school` | Education |
| `home` | Housing / rent |
| `flight_takeoff` | Travel |
| `pets` | Pet care |
| `work` | Work / freelance |
| `spa` | Wellness / self-care |
| `devices` | Tech / gadgets |
| `card_giftcard` | Gifts |
| `subscriptions` | Subscriptions |
| `savings` | Savings |
| `celebration` | Entertainment |
| `coffee` | Coffee |

For a full list: [fonts.google.com/icons](https://fonts.google.com/icons) — use the icon name in snake_case.

---

## Section 11 — Support and legal links

```kotlin
const val SUPPORT_EMAIL       = "support@yourdomain.com"
const val PRIVACY_POLICY_URL  = "https://yourdomain.com/privacy"
const val TERMS_OF_SERVICE_URL= "https://yourdomain.com/terms"
const val DOCUMENTATION_URL   = "https://yourdomain.com/docs"
```

Replace all four placeholders before publishing. These values are shown in the Legal / About section of the Settings sheet and linked from the Privacy dialog.

| Field | Where it appears |
|-------|-----------------|
| `SUPPORT_EMAIL` | About screen, legal footer |
| `PRIVACY_POLICY_URL` | Privacy policy link in Settings → Legal |
| `TERMS_OF_SERVICE_URL` | Terms link in Settings → Legal |
| `DOCUMENTATION_URL` | Help / docs link in About screen |

Google Play requires a real, publicly accessible privacy policy URL. The app will be rejected from the Play Store if this points to a placeholder domain.

---

## Data class reference

All data classes used by `AppConfig` are defined at the bottom of `AppConfig.kt`. They have no dependencies outside that file.

| Class | Used by | Fields |
|-------|---------|--------|
| `CurrencyOption` | `SUPPORTED_CURRENCIES` | `symbol`, `code`, `displayName` |
| `SubscriptionPreset` | `DEFAULT_SUBSCRIPTION_PRESETS` | `title`, `defaultAmount`, `category`, `frequency`, `defaultDueDay`, `iconKey`, `colorHex` |
| `StarterRoutineConfig` | `DEFAULT_STARTER_ROUTINES` | `title`, `note`, `category`, `timeMinutes`, `timeOfDay`, `iconKey`, `colorHex`, `targetDaysMask` |
| `DefaultCategory` | `DEFAULT_CUSTOM_CATEGORIES` | `name`, `type`, `iconKey`, `colorHex` |

---

## What is NOT in AppConfig

| Thing | Where it actually lives |
|-------|------------------------|
| Compose theme colours | `app/src/main/java/com/example/ui/theme/Color.kt` |
| Room database version | `AppDatabase.kt` → `@Database(version = 4)` |
| Kotlin namespace | `app/build.gradle.kts` → `android.namespace` |
| Gradle applicationId | `app/build.gradle.kts` → `android.defaultConfig.applicationId` |
| DataStore preference keys | `UserPreferencesRepository.kt` → `PreferenceKeys` |
| Notification channel IDs | `NotificationHelper.kt` → `CHANNEL_*_ID` constants |
| Built-in finance categories | `Expense.kt` → `ExpenseCategory` enum |
| Routine categories | `Routine.kt` → `RoutineCategory` enum |
