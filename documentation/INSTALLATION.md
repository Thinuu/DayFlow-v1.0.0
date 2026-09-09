# Installation Guide

How to open, sync, and run DayFlow from this source package.

---

## Android Studio requirements

- **Android Studio:** Koala (2024.1) or later (Ladybug / Meerkat / current stable recommended)
- **JDK for Gradle:** 17 or 21 (Android Studio embedded JDK is fine)
- **RAM:** 8 GB minimum, 16 GB recommended
- **OS:** Windows 10+, macOS, or Linux
- **SDK platforms:** install API 24 through 36 as needed; **minSdk is 24**, **targetSdk is 36** (`app/build.gradle.kts`)

Download: [developer.android.com/studio](https://developer.android.com/studio)

---

## Opening the project

1. Extract the item (for example `DayFlow-v1.0.0`) to a local folder.
2. Android Studio → **File → Open** → select that folder (the one that contains `settings.gradle.kts` and `gradlew`).
3. Wait for Gradle sync.

If prompted for missing SDK components, install them.

**Gradle JDK:** **Settings → Build, Execution, Deployment → Build Tools → Gradle** → JDK 17+.

This project uses **Gradle 9.3.1** (wrapper) and **AGP 9.1.1**. Do not replace the wrapper with Gradle 8.8; that would not match this package.

---

## Firebase / Google services

The app Gradle file applies the Google Services plugin with:

```kotlin
googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }
```

and `googleServices.missing.passthrough=true` in `gradle.properties`.

**You do not need `google-services.json` to compile or run the local finance/planner app.** Kotlin source does not call Firebase AI APIs. `firebase-bom`, `firebase-ai`, and App Check are on the classpath for buyers who add cloud AI later.

If you add Firebase:

1. Create a Firebase project and Android app with your `applicationId`.
2. Place `google-services.json` in `app/` (do not commit secrets into a public repo).
3. Follow [AI_SETUP.md](AI_SETUP.md) for optional cloud AI.

---

## Environment files

- `.env.example` is a **safe template** (no real keys).
- Copy to `.env` only on your machine if you add a cloud AI key.
- The Secrets Gradle plugin reads `.env` with fallback `.env.example`. Never put real keys in documentation or in files you upload to CodeCanyon.

---

## Running a debug build

1. Create or start an emulator (API 24+) or enable USB debugging on a device.
2. Select the `app` run configuration.
3. Run (green play / Shift+F10).

Command line:

```bash
./gradlew assembleDebug
```

Windows: `gradlew.bat assembleDebug`

Debug APK: `app/build/outputs/apk/debug/`

Debug builds use the **default Android debug keystore** on your machine (`~/.android/debug.keystore`). This package does not include `*.jks` or `*.keystore` files.

---

## Building the application

| Goal | Command |
|------|---------|
| Clean | `./gradlew clean` |
| Debug APK | `./gradlew assembleDebug` |
| Unit tests | `./gradlew testDebugUnitTest` |
| Release APK | `./gradlew assembleRelease` |
| Play Bundle | `./gradlew bundleRelease` |

Release signing: [RELEASE_BUILD.md](RELEASE_BUILD.md).

---

## Next steps

1. [CUSTOMIZATION.md](CUSTOMIZATION.md)  
2. [AI_SETUP.md](AI_SETUP.md)  
3. [BILLING_SETUP.md](BILLING_SETUP.md)  
