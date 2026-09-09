# Customization Guide

White-label DayFlow using the files below. No redesign of the product is required.

> **For a complete field-by-field reference of every AppConfig value, default, and example, see [APPCONFIG.md](APPCONFIG.md).** This guide gives the high-level map; APPCONFIG.md gives the exact values.

---

## App name

| Location | What it controls |
|----------|------------------|
| `app/src/main/java/com/example/AppConfig.kt` → `APP_NAME` | In-app branding used by backup metadata and config |
| `app/src/main/res/values/strings.xml` → `app_name` | Launcher label (`AndroidManifest` `android:label`) |
| `app/src/main/res/values/strings.xml` → `app_full_name` | Secondary display string |

Default launcher name is `DayFlow`. Marketplace listing title is **DayFlow \| Personal Finance and Daily Planner Android App** (use that on CodeCanyon; you may keep `DayFlow` as the app name on device).

Slogan: `AppConfig.APP_SLOGAN`.

---

## Application ID

Play Store unique ID in `app/build.gradle.kts`:

```kotlin
defaultConfig {
    applicationId = "com.yourcompany.dayflow"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0.0"
}
```

Change `applicationId` before publishing. Also update `AppConfig.PACKAGE_NAME` to the same value for documentation consistency.

---

## Package / namespace

Kotlin sources live under `com.example` (`namespace = "com.example"`). You may keep that namespace and only change `applicationId`, which is the usual Play Store approach.

If you move packages, use Android Studio **Refactor → Move** and update all imports. Then `./gradlew clean`.

---

## Branding

- **Icons:** `app/src/main/res/mipmap-*` and `drawable/ic_launcher_*`. Android Studio → **Image Asset** to replace adaptive icons.
- **Theme:** `app/src/main/java/com/example/ui/theme/Color.kt`, `Theme.kt`, `Type.kt`. Hex tokens also exist in `AppConfig` (`COLOR_PRIMARY_HEX`, etc.) for buyer reference; Compose theme colors in `Color.kt` are what the UI uses.
- **XML colors:** `app/src/main/res/values/colors.xml`, `themes.xml`.

---

## Default categories

Expense/income categories are the `ExpenseCategory` enum in `app/src/main/java/com/example/data/model/Expense.kt`:

FOOD, GROCERIES, TRANSPORT, BILLS, SHOPPING, ENTERTAINMENT, HEALTH, EDUCATION, SALARY, INVESTMENT, BUSINESS, OTHER.

Each has `displayName`, `iconName`, and `colorHex`. Custom categories at runtime are stored via `CustomCategoryDao`.

---

## Default subscription presets

`AppConfig.DEFAULT_SUBSCRIPTION_PRESETS` — Netflix, Spotify, gym, Google One, Wi-Fi, Amazon Prime, YouTube Premium, Apple One (amounts are examples). Used by `SubscriptionsDialog`.

Starter habits: `AppConfig.DEFAULT_STARTER_ROUTINES`.

---

## Colors / theme

Primary Compose tokens (dark theme):

- `RadiantLavender` `0xFFD0BCFF`
- `DeepCharcoalBg` `0xFF1C1B1F`
- `EmeraldGreen` / income greens in `Color.kt`

Edit `Color.kt` and rebuild.

---

## Icons / assets

Vector launcher layers: `ic_launcher_foreground.xml`, `ic_launcher_background.xml`. Adaptive XML: `mipmap-anydpi-v26/ic_launcher.xml`.

---

## Configuration files

| File | Purpose |
|------|---------|
| `AppConfig.kt` | Branding, currencies, feature flags, Play product ID **placeholders**, support URLs |
| `app/build.gradle.kts` | SDK, applicationId, signing env vars, dependencies |
| `gradle/libs.versions.toml` | Library versions |
| `gradle.properties` | JVM, configuration cache, Google Services passthrough |
| `.env.example` | Optional AI key **placeholder** (copy to local `.env`) |

Feature flags in `AppConfig` (defaults in this package):

- `FEATURE_AI_INSIGHTS_ENABLED = true` — local insights intended to be on
- `FEATURE_GEMINI_ONLINE_ENABLED = false` — no cloud Gemini implementation is shipped; set true only after you add your own API code
- `FEATURE_PREMIUM_PAYWALL_ENABLED = false` — paywall UI is optional; features are not Envato-gated
- PIN, export/import, notifications, blueprints, custom categories, transfers — flags exist; wire additional checks only if you need module toggles

---

## Support URLs

Replace placeholders in `AppConfig`: `SUPPORT_EMAIL`, `PRIVACY_POLICY_URL`, `TERMS_OF_SERVICE_URL`, `DOCUMENTATION_URL`.
