# Frequently Asked Questions

---

## General

**Q: Does DayFlow require an internet connection?**

No. All data is stored locally in Room SQLite. Insights are generated on-device by `AIService.OfflineAIProvider`. The app works fully offline. An internet connection is only needed if you implement optional cloud AI or Google Play Billing (neither is enabled by default).

---

**Q: Does the app require a Firebase account or Google account?**

No. The Firebase SDK is included as an optional dependency (`firebase-ai`, `firebase-appcheck-recaptcha`), but the app builds and runs without a `google-services.json` file. The Google Services Gradle plugin is configured with `MissingGoogleServicesStrategy.WARN` — it warns instead of failing.

---

**Q: What Android version does DayFlow support?**

`minSdk = 24` (Android 7.0 Nougat) through `targetSdk = 36`. The app supports devices from 2016 onwards.

---

**Q: Is this a white-label product? Can I rebrand it?**

Yes. All branding is centralized in `AppConfig.kt` (app name, slogan, currency, feature flags, Play product IDs, support URLs) and `app/src/main/res/values/strings.xml` (launcher label). Colors and theme are in `ui/theme/Color.kt`. See [CUSTOMIZATION.md](CUSTOMIZATION.md).

---

**Q: What does the Envato Regular vs Extended license mean for this code?**

The license governs how you may use and distribute the compiled app commercially. It does **not** restrict which features compile or run. `ProFeatureManager` always returns access — there is no Envato license check at runtime. See the Envato license terms on the item page for commercial use definitions.

---

## Build and setup

**Q: The Gradle sync fails with "Could not resolve" dependency errors.**

Ensure you have an internet connection and that `google()` and `mavenCentral()` are reachable. Try **File → Invalidate Caches / Restart** in Android Studio, then sync again. See [TROUBLESHOOTING.md](TROUBLESHOOTING.md).

---

**Q: Android Studio asks me to install SDK platform 36. Is that required?**

Yes. `compileSdk = 36` is set in `app/build.gradle.kts`. Install it from **SDK Manager → SDK Platforms**. You also need Android 7.0 (API 24) for the emulator if you want to test the minimum-supported version.

---

**Q: Can I build without Android Studio?**

Yes, from the command line with a JDK 17 or 21 and `ANDROID_HOME` pointing to your SDK:

```bash
./gradlew assembleDebug
```

Windows: `gradlew.bat assembleDebug`. See [GETTING_STARTED.md](GETTING_STARTED.md).

---

**Q: The release build crashes but debug works fine.**

R8 minification is enabled for release builds (`isMinifyEnabled = true`). A class is being stripped or renamed. Open the crash logcat, find the `ClassNotFoundException` or `NoSuchMethodException`, and add a `-keep` rule for that class in `app/proguard-rules.pro`. Rebuild and re-test.

---

## Data and database

**Q: How do I wipe all demo data and start fresh?**

Uninstall the app from the device. The next install seeds fresh demo data. Alternatively, clear app data from Android Settings → Apps → DayFlow → Storage → Clear Data.

---

**Q: I changed the Room schema and the app crashes with an `IllegalStateException: Room cannot verify the data integrity`.**

You changed an `@Entity` class without incrementing the database version. Add a `MIGRATION_N_(N+1)` object in `AppDatabase.kt`, increment `version`, and add the migration to `addMigrations(...)`. See [ROOM_DATABASE.md](ROOM_DATABASE.md).

---

**Q: The old code used `fallbackToDestructiveMigration()`. What happened to it?**

It was removed. It destroys all user data on a schema mismatch, which is not acceptable for a production app. All schema changes now require an explicit `Migration` object.

---

**Q: Can I export user data?**

Yes. Settings → Backup & Restore exports a complete JSON backup (`dayflow_backup_<date>.json`) and a CSV of all transactions. Both formats are readable in any text editor or spreadsheet.

---

## Currency

**Q: How do I change the default currency?**

Edit `AppConfig.DEFAULT_CURRENCY_SYMBOL` and `AppConfig.DEFAULT_CURRENCY_CODE`. This affects fresh installs. Existing users keep their persisted preference from DataStore.

---

**Q: How do I add a currency that isn't in the list?**

Add one entry to `AppConfig.SUPPORTED_CURRENCIES`:

```kotlin
CurrencyOption("kr", "SEK", "Swedish Krona (kr)")
```

No other change is needed. The currency picker in Settings and Onboarding will include it automatically. See [CURRENCY.md](CURRENCY.md).

---

## AI insights

**Q: The AI Insights panel shows "Financial Health Stable" — is something broken?**

No. That is the fallback card shown when no other conditions are triggered. Add more transactions, set a budget, and complete some routines — the heuristics need data to generate useful cards.

---

**Q: Is a real AI model (ChatGPT, Gemini, etc.) being used?**

No. The default implementation (`OfflineAIProvider`) is a rule-based heuristic engine that runs entirely on-device. There is no model, no API, and no network call. To add a real AI model, implement the `AIProvider` interface and call `AIService.setProvider(yourProvider)`. See [AI_SETUP.md](AI_SETUP.md).

---

**Q: Where do I put my Gemini API key?**

Copy `.env.example` to `.env` (gitignored), add your key there, and read it via `BuildConfig` (the Secrets Gradle plugin is already configured). Then implement an `AIProvider` that calls the API. **Never commit a real key to version control.** See [AI_SETUP.md](AI_SETUP.md).

---

## Billing

**Q: Can I sell the app without setting up Google Play Billing?**

Yes. `NoOpBillingService` is the default — it does not contact Play and does not lock any features. All screens and features work without configuring IAP.

---

**Q: How do I add real in-app purchases?**

1. Create products in Google Play Console with IDs matching `AppConfig.PLAY_STORE_PRO_*`.
2. Add `billing-ktx` to `app/build.gradle.kts`.
3. Implement `BillingClient` inside `GooglePlayBillingService`.
4. Swap `NoOpBillingService` for `GooglePlayBillingService` in `MainViewModel.init{}`.

Full steps in [BILLING_SETUP.md](BILLING_SETUP.md).

---

**Q: `GooglePlayBillingService.purchasePro` throws an `IllegalStateException`. Why?**

That is the placeholder error. `GooglePlayBillingService` is a template — `purchasePro` intentionally fails until you implement `BillingClient`. See [BILLING_SETUP.md](BILLING_SETUP.md).

---

## Notifications

**Q: Notifications aren't showing on Android 13+.**

The user denied the `POST_NOTIFICATIONS` permission. The app does not request it again after denial — this is intentional (respecting the system behaviour). The user can re-enable it in Android Settings → Apps → DayFlow → Notifications.

---

**Q: Can I force users to enable notifications?**

No, and you should not. Android policy requires graceful handling of denied permissions. `NotificationHelper.canPostNotifications()` silently no-ops when permission is not granted. The app continues working normally.

---

## Publishing

**Q: What is the difference between `namespace` and `applicationId`?**

`namespace` (`com.example`) is the Kotlin package root — it controls source file paths and generated class names. `applicationId` (`com.yourcompany.dayflow`) is the Play Store and device identifier — change this before publishing. They are currently different values and can stay different. See [PACKAGE_RENAME.md](PACKAGE_RENAME.md).

---

**Q: Do I need to rename `com.example` before publishing?**

No. You only need to change `applicationId`. The Kotlin namespace (`com.example`) affects source file organisation but not the app's Play Store identity. Renaming the namespace is optional — see [PACKAGE_RENAME.md](PACKAGE_RENAME.md) if you want to do it anyway.

---

**Q: My signed release AAB uploads to Play Console but Play says "Package name already taken."**

The `applicationId` `com.yourcompany.dayflow` is a placeholder — it may already exist on the Play Store from another upload. Change it to a unique reverse-domain identifier that you own before creating your Play Console app listing.

---

**Q: What `versionCode` should I use for my first upload?**

`1` (the current default). Every subsequent upload must increment it. Keep `versionCode` in `app/build.gradle.kts` and `AppConfig.APP_VERSION_CODE` in sync. See [PLAY_STORE.md](PLAY_STORE.md).
