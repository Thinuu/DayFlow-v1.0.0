# Package Rename Guide

This document covers the difference between the two identifiers in this project
and the steps required to rename each one.

---

## The two identifiers

| Identifier | Current value | Purpose |
|------------|---------------|---------|
| Kotlin namespace | `com.example` | Root of all Kotlin `package` declarations; used in generated `R`, `BuildConfig`, and KSP-generated classes |
| Gradle applicationId | `com.yourcompany.dayflow` | Google Play / device identifier; must be globally unique |

---

## Changing the applicationId (REQUIRED before publishing)

This is the simpler change. It does not touch any Kotlin source files.

1. Open `app/build.gradle.kts`.
2. Change:
   ```kotlin
   applicationId = "com.yourcompany.dayflow"
   ```
   to your own reverse-domain identifier, e.g.:
   ```kotlin
   applicationId = "com.acme.dayflow"
   ```
3. Open `app/src/main/java/com/example/AppConfig.kt` and update:
   ```kotlin
   const val APPLICATION_ID = "com.acme.dayflow"
   ```
4. If using Firebase, regenerate `google-services.json` from the Firebase Console
   (Project settings → Your apps → Android → download `google-services.json`)
   and replace `app/google-services.json`.
5. Sync Gradle and rebuild.

**No Kotlin source files need to change for an applicationId-only rename.**

---

## Renaming the Kotlin namespace (OPTIONAL — advanced)

This changes every `package com.example.*` declaration in the project.
It is optional but gives a more professional appearance in stack traces and
on the Play Store detail page.

### Recommended approach: Android Studio refactor

1. In the Project panel, right-click the `com.example` package node.
2. **Refactor → Rename** → enter your new package root (e.g. `com.acme.dayflow`).
3. Check "Search in comments and strings" only if you want doc comments updated too.
4. Click Refactor. Android Studio updates all `package` and `import` declarations.
5. In `app/build.gradle.kts`, update:
   ```kotlin
   namespace = "com.acme.dayflow"
   ```
6. Clean and rebuild:
   ```bash
   ./gradlew clean assembleDebug
   ```
7. Verify the app runs correctly on a device/emulator.

### Files that reference the package explicitly

These are also updated automatically by the refactor tool, but verify manually:

| File | Reference |
|------|-----------|
| `app/build.gradle.kts` | `namespace = "com.example"` |
| `app/src/main/AndroidManifest.xml` | `.MainActivity` activity name (dot-relative) |
| All `*.kt` files | `package com.example.*` declarations |
| KSP-generated files in `app/build/` | Regenerated automatically — no manual action |

### Files that do NOT need changes

| File | Why |
|------|-----|
| `app/build.gradle.kts` `applicationId` | Separate identifier — change independently |
| `app/google-services.json` | Tied to `applicationId`, not namespace |
| Room schema JSON in `app/schemas/` | References the database class by qualified name — regenerated on next build |

---

## After renaming

Run the full test suite to confirm nothing broke:

```bash
./gradlew test
./gradlew connectedAndroidTest   # requires a connected device/emulator
```

Then do a release build to confirm ProGuard rules still apply correctly:

```bash
./gradlew bundleRelease
```
