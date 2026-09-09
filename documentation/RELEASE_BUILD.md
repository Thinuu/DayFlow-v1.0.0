# Release Build Guide

---

## Debug build

```bash
./gradlew assembleDebug
```

Windows: `gradlew.bat assembleDebug`

- Signed with the **machine default** debug keystore (`~/.android/debug.keystore`), not a file from this zip.
- `applicationIdSuffix = ".debug"` is applied so debug and release can coexist on the same device.
- `isMinifyEnabled = false` — stack traces are fully readable.
- Output: `app/build/outputs/apk/debug/`

---

## Release APK

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/`

---

## Release AAB (Play Store — required for new apps)

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/`

---

## R8 minification (release only)

`isMinifyEnabled = true` and `isShrinkResources = true` are set for the release build type.

ProGuard/R8 rules are in `app/proguard-rules.pro`. Rules are provided for:

- Kotlin & coroutines
- Jetpack Compose
- Room (entities, DAOs, type converters)
- Moshi (JSON backup/export)
- Retrofit + OkHttp
- Firebase (AI, AppCheck)
- DataStore Preferences
- App data models and enums
- ViewModel / Lifecycle
- Line number preservation for crash reports

If a release build crashes with a `ClassNotFoundException` that the debug build does not reproduce, the class is being stripped by R8. Add a `-keep` rule to `proguard-rules.pro` for the class shown in the stack trace.

---

## Signing (buyer-provided keystore)

This package does **not** include `*.jks` or `*.keystore` files. You must create and keep your own upload key.

`app/build.gradle.kts` enables a `release` signing config only when **all** of these environment variables are set:

| Variable | Meaning |
|----------|---------|
| `KEYSTORE_PATH` | Absolute path to your `.jks` / `.keystore` |
| `STORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |

**Windows PowerShell (current session):**

```powershell
$env:KEYSTORE_PATH = "C:\secure\upload-keystore.jks"
$env:STORE_PASSWORD = "your_store_password"
$env:KEY_ALIAS = "your_key_alias"
$env:KEY_PASSWORD = "your_key_password"
.\gradlew.bat assembleRelease bundleRelease
```

**macOS / Linux:**

```bash
export KEYSTORE_PATH="/secure/upload-keystore.jks"
export STORE_PASSWORD="your_store_password"
export KEY_ALIAS="your_key_alias"
export KEY_PASSWORD="your_key_password"
./gradlew assembleRelease bundleRelease
```

If the variables are missing, Gradle does not attach a custom signing config. The build still succeeds but the output is unsigned. **Do not upload an unsigned or debug-signed AAB to production.**

### Create a new keystore

```bash
keytool -genkey -v -keystore upload-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias upload
```

Store the `.jks` file **outside the project tree** and **outside version control**. If you lose it you cannot publish updates to the same Play Store listing.

---

## Application ID / Package identity

Two identifiers coexist in this project:

| Identifier | Value | Where to change |
|------------|-------|-----------------|
| **Kotlin namespace** | `com.example` | `android.namespace` in `app/build.gradle.kts` + all `package` declarations |
| **Gradle applicationId** | `com.yourcompany.dayflow` | `defaultConfig.applicationId` in `app/build.gradle.kts` + `AppConfig.APPLICATION_ID` |

**Before publishing, you must change `applicationId`** to your own reverse-domain identifier (e.g. `com.acme.dayflow`).

Steps:
1. Edit `defaultConfig.applicationId` in `app/build.gradle.kts`.
2. Update `AppConfig.APPLICATION_ID` to match.
3. If using Firebase: regenerate `google-services.json` with the new package name from the Firebase Console and replace the file in `app/`.
4. Create a new Play Console app with the new `applicationId`.

**Renaming the Kotlin namespace (`com.example`) is optional** but recommended for a professional release. This requires:
1. Android Studio: Refactor → Rename on the `com.example` package.
2. Update `android.namespace` in `app/build.gradle.kts`.
3. Rebuild and verify all imports resolve correctly.

---

## Google Play publishing checklist

- [ ] Change `applicationId` from `com.yourcompany.dayflow` to your own ID.
- [ ] Update `AppConfig.APPLICATION_ID` to match.
- [ ] Increment `versionCode` for every Play Store upload.
- [ ] Update `versionName` in `defaultConfig`.
- [ ] Replace `AppConfig.SUPPORT_EMAIL`, `PRIVACY_POLICY_URL`, `TERMS_OF_SERVICE_URL`.
- [ ] Sign with your upload key (set environment variables above).
- [ ] Run `./gradlew bundleRelease` and verify the AAB builds without errors.
- [ ] Upload to an **internal testing track** first.
- [ ] Complete Play Console content rating, data safety, and store listing.
- [ ] Optional: configure Play Billing products ([BILLING_SETUP.md](BILLING_SETUP.md)).
