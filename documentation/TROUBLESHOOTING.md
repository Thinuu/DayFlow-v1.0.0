# Troubleshooting

---

## Gradle sync problems

- Use JDK 17 or 21 for Gradle (Android Studio embedded JDK).
- Confirm internet access to `google()`, `mavenCentral()`, `gradlePluginPortal()`.
- Wrapper is **Gradle 9.3.1** (`gradle/wrapper/gradle-wrapper.properties`). Do not force 8.8.
- **File → Invalidate Caches / Restart**, then **Sync Project with Gradle Files**.
- Increase memory: `org.gradle.jvmargs=-Xmx4g` in `gradle.properties` (already set).
- Windows: run `gradlew.bat` from the folder that contains `settings.gradle.kts`.
- If configuration cache errors appear, retry after `gradlew.bat --stop`.

---

## SDK problems

From `app/build.gradle.kts`:

- `minSdk = 24`
- `targetSdk = 36`
- `compileSdk` release 36 (minor API 1)

Install those platforms in **SDK Manager**. A missing compile SDK 36 causes sync/build failure.

Emulators below API 24 will not match minSdk.

---

## Build problems

- `./gradlew clean` then the failing task.
- KSP / Room: check `ksp` room-compiler in `app/build.gradle.kts`.
- Compose compiler is applied via `alias(libs.plugins.kotlin.compose)` (Kotlin 2.2.10).
- Secrets plugin expects `.env.example` (shipped). Missing `.env` is OK.
- Google Services: missing `google-services.json` is a **warning**, not a hard fail (`MissingGoogleServicesStrategy.WARN`).
- Do not point `signingConfigs` at a missing `debug.keystore` in the repo; debug uses the SDK default keystore.

Release without env vars: local APK/AAB may be debug-signed. Production Play uploads need your keystore env vars ([RELEASE_BUILD.md](RELEASE_BUILD.md)).

---

## Room / database issues

- Database name: `dayflow_database`. Version **4**.
- `MIGRATION_3_4` adds performance indices and is the only migration in the current codebase (purely additive — no data is lost).
- `fallbackToDestructiveMigration()` has been removed. All schema changes require an explicit `Migration` object. See [ROOM_DATABASE.md](ROOM_DATABASE.md).
- Backup JSON uses `schemaVersion` 1 (`AppConfig.BACKUP_SCHEMA_VERSION`). Restoring a file with a different schema fails validation in `BackupManager`.
- Tests: `BackupValidationTest.kt`, `BudgetAndLedgerCalculationsTest.kt`.

---

## Billing configuration issues

- App features work with **no** Play Billing setup.
- `NoOpBillingService` does not talk to Play.
- `GooglePlayBillingService.purchasePro` fails until you implement Billing Library + Play Console products.
- Product ID mismatch with Play Console → “item not found”.
- Test purchases require a tester account and a Play-signed build.
- Envato license is unrelated to IAP errors.

---

## AI configuration issues

- Local insights need data (transactions, budgets, routines), not an API key.
- Empty insights: log more expenses or complete habits; `AIService` still emits a fallback “Financial Health Stable” card.
- Cloud AI: not implemented in Kotlin. Setting `FEATURE_GEMINI_ONLINE_ENABLED = true` does nothing until you write a client.
- Invalid `GEMINI_API_KEY` only matters after you read it in **your** code.
- Third-party usage is billed by the provider, not by this item.

---

## Tests

```bash
./gradlew testDebugUnitTest
```

Robolectric screenshot test (`GreetingScreenshotTest`) needs working Robolectric graphics on the host. If it fails on a headless CI image, run on a developer machine or inspect the Robolectric/Roborazzi logs; do not delete the test to hide failures.
