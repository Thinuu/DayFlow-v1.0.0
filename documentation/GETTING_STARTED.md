# Getting Started

Everything you need to open, build, and run DayFlow for the first time.

---

## Prerequisites

| Tool | Minimum version | Where to get it |
|------|----------------|-----------------|
| Android Studio | Hedgehog (2023.1) or later | [developer.android.com/studio](https://developer.android.com/studio) |
| JDK | 17 or 21 (use the embedded JDK that ships with Android Studio) | Bundled in Android Studio |
| Android SDK | Platform 36, Build-Tools 36 | Android Studio → SDK Manager |
| Git | Any recent version | [git-scm.com](https://git-scm.com) |

You do **not** need a Firebase account, a Google Play account, or any API key to build and run the app.

---

## Step 1 — Open the project

1. Launch Android Studio.
2. **File → Open** (or "Open an Existing Project" on the welcome screen).
3. Select the folder that contains `settings.gradle.kts` — that is the project root.
4. Wait for Gradle sync to finish. First sync downloads dependencies and may take several minutes.

If sync fails, see [TROUBLESHOOTING.md](TROUBLESHOOTING.md).

---

## Step 2 — Install the missing SDK platforms (if prompted)

Android Studio shows a banner when `compileSdk 36` or `minSdk 24` platforms are not installed.

1. **SDK Manager** (toolbar wrench icon → SDK Platforms tab).
2. Check **Android 14 (API 34)** and **Android 16 (API 36)** (or whatever the banner lists).
3. Apply and close.
4. Re-sync.

---

## Step 3 — Run on an emulator or device

1. **Device Manager** → create an emulator with API 33 or higher (recommended: Pixel 6, API 35).
2. Press the green **Run** button (Shift+F10 on Windows).
3. The app installs and launches automatically.
4. First launch seeds demo data — accounts, transactions, routines, goals — so the UI is populated immediately.

To run on a physical device: enable **Developer Options → USB Debugging**, connect via USB, select the device from the target dropdown.

---

## Step 4 — Run the tests

```bash
# Unit tests (Robolectric, no device needed)
./gradlew testDebugUnitTest

# Instrumentation tests (requires connected device or running emulator)
./gradlew connectedAndroidTest
```

Windows: use `gradlew.bat` instead of `./gradlew`.

---

## Step 5 — Explore the key source files

| File | What it does |
|------|-------------|
| `AppConfig.kt` | Central configuration — app name, currency, feature flags, product IDs |
| `MainActivity.kt` | Entry point; tab navigation host; notification permission request |
| `MainViewModel.kt` | All UI state; exposes `DayFlowUiState` via `StateFlow` |
| `DayFlowRepository.kt` | Single source of truth over all Room DAOs |
| `AppDatabase.kt` | Room database definition, version history, migrations |
| `AIService.kt` | AI provider interface + offline rule-based implementation |
| `BillingService.kt` | Billing interface + no-op and Play Billing stubs |
| `NotificationHelper.kt` | Notification channel registration and delivery |

---

## Step 6 — Prepare for publishing (before release)

These steps are required before uploading to the Play Store:

1. Change `applicationId` in `app/build.gradle.kts` from `com.yourcompany.dayflow` to your own identifier.
2. Update `AppConfig.APPLICATION_ID` to match.
3. Update `AppConfig.SUPPORT_EMAIL`, `PRIVACY_POLICY_URL`, `TERMS_OF_SERVICE_URL`.
4. Create your release keystore and set the signing environment variables (see [RELEASE_BUILD.md](RELEASE_BUILD.md)).
5. Replace placeholder support URLs.
6. Build the release AAB: `./gradlew bundleRelease`.

For a full pre-publish checklist see [PLAY_STORE.md](PLAY_STORE.md).

---

## Where to go next

| Goal | Document |
|------|---------|
| Configure every AppConfig field with examples | [APPCONFIG.md](APPCONFIG.md) |
| Rebrand the app (name, colors, icons) | [CUSTOMIZATION.md](CUSTOMIZATION.md) |
| Change the default currency | [CURRENCY.md](CURRENCY.md) |
| Add cloud AI | [AI_SETUP.md](AI_SETUP.md) |
| Enable Google Play Billing | [BILLING_SETUP.md](BILLING_SETUP.md) |
| Understand the database schema | [ROOM_DATABASE.md](ROOM_DATABASE.md) |
| Build a signed release APK / AAB | [RELEASE_BUILD.md](RELEASE_BUILD.md) |
| Publish to the Play Store | [PLAY_STORE.md](PLAY_STORE.md) |
| Fix a build or runtime error | [TROUBLESHOOTING.md](TROUBLESHOOTING.md) |
| Browse common questions | [FAQ.md](FAQ.md) |
