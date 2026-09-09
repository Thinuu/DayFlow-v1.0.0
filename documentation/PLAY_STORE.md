# Play Store Publishing Guide

Everything required to go from source code to a live Play Store listing.

---

## Pre-publish checklist

Work through these in order. Do not skip signing or applicationId steps.

### Identity
- [ ] Change `applicationId` in `app/build.gradle.kts` from `com.yourcompany.dayflow` to your own reverse-domain identifier (e.g. `com.acme.dayflow`).
- [ ] Update `AppConfig.APPLICATION_ID` to match the new `applicationId`.
- [ ] Update `AppConfig.APP_NAME` and `app/src/main/res/values/strings.xml` → `app_name` if rebranding.
- [ ] Update `AppConfig.APP_VERSION_NAME` / `APP_VERSION_CODE` (and the matching fields in `app/build.gradle.kts`).

### Legal and support
- [ ] Replace `AppConfig.SUPPORT_EMAIL` with your contact email.
- [ ] Replace `AppConfig.PRIVACY_POLICY_URL` with a real, publicly accessible URL.
- [ ] Replace `AppConfig.TERMS_OF_SERVICE_URL` with a real, publicly accessible URL.
- [ ] Replace `AppConfig.DOCUMENTATION_URL` with your support/docs URL.
- [ ] Write or host a Privacy Policy. Google Play requires one for all apps.

### Signing
- [ ] Create a release keystore (see [RELEASE_BUILD.md](RELEASE_BUILD.md)).
- [ ] Store it outside the project folder and outside version control.
- [ ] Set the four signing environment variables: `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
- [ ] Run `./gradlew bundleRelease` and confirm it succeeds without errors.

### Assets
- [ ] Replace launcher icons in `app/src/main/res/mipmap-*/` with your own (use Android Studio → Image Asset).
- [ ] Prepare Play Store assets: feature graphic (1024×500), at least 2 screenshots per device type.

### Firebase (optional)
- [ ] If using Firebase: create your own Firebase project, add the Android app with your `applicationId`, download `google-services.json`, and replace `app/google-services.json`.
- [ ] If NOT using Firebase: the app builds and runs without `google-services.json` (the plugin is set to `MissingGoogleServicesStrategy.WARN`).

### Final build
- [ ] Run `./gradlew clean bundleRelease`.
- [ ] Verify the output AAB at `app/build/outputs/bundle/release/app-release.aab`.

---

## Play Console setup

1. Go to [play.google.com/console](https://play.google.com/console).
2. Create a new app → select **Android** → set default language and title.
3. Complete the **Dashboard** tasks (store listing, content rating, data safety, target audience).

### Store listing

| Field | Suggested content |
|-------|------------------|
| App name | DayFlow — Personal Finance & Planner |
| Short description | Track spending, savings goals, habits, and bills — all offline. |
| Full description | (See `documentation/MARKETPLACE_DESCRIPTION.html` as a starting point) |
| Category | Finance |
| Tags | budget, expense tracker, habit tracker, savings, personal finance |

### Content rating

Complete the content rating questionnaire. DayFlow contains no violence, mature content, or user-generated social features — it will receive an "Everyone" or equivalent rating.

### Data safety

DayFlow stores all data locally on-device (Room SQLite + DataStore). No personal data is sent to external servers by default. Your answers:

| Question | Answer |
|----------|--------|
| Does the app collect or share user data? | No (default offline-only build) |
| Is data encrypted in transit? | N/A — no data leaves the device |
| Can users request data deletion? | Yes — Settings → Backup & Restore → wipe via uninstall |

If you add Firebase or cloud AI, update the data safety form accordingly.

---

## In-App Purchases (optional)

If you want to add subscriptions or one-time purchases:

1. In Play Console → Monetize → Subscriptions / In-app products, create products with these IDs (or change the IDs in `AppConfig`):
   - `dayflow_pro_lifetime` (one-time)
   - `dayflow_pro_annual_sub` (subscription)
   - `dayflow_pro_monthly_sub` (subscription)

2. Wire `GooglePlayBillingService` in your ViewModel (see [BILLING_SETUP.md](BILLING_SETUP.md)).

3. All features are available without IAP by default — `ProFeatureManager` never gates access.

---

## First upload

1. Play Console → Release → Testing → **Internal testing** → Create new release.
2. Upload the `app-release.aab`.
3. Add release notes (e.g. "Initial release").
4. Save and roll out to internal testers.
5. Install via the internal testing link on a physical device to verify the production build.

**Do not go straight to production.** Always test on internal track first.

---

## Version code policy

Every upload to Play must have a strictly increasing `versionCode`. The format used in this project is a plain integer starting at `1`.

Update both locations together:

```kotlin
// app/build.gradle.kts
versionCode = 2
versionName = "1.1.0"
```

```kotlin
// AppConfig.kt
const val APP_VERSION_NAME = "1.1.0"
const val APP_VERSION_CODE = 2
```

---

## Update releases

For each update:
1. Increment `versionCode` (must be higher than the previously uploaded value).
2. Update `versionName` to a human-readable string.
3. Add a `MIGRATION_N_(N+1)` object if the Room schema changed (see [ROOM_DATABASE.md](ROOM_DATABASE.md)).
4. Build: `./gradlew bundleRelease`.
5. Upload to Play Console → Testing → Create new release.

---

## Play App Signing

Google Play manages the final signing key when Play App Signing is enabled (the default for new apps). Your upload key signs the AAB you upload; Google re-signs the APK distributed to users.

- **Upload key**: the keystore you created for `KEYSTORE_PATH`. Guard it carefully.
- **App signing key**: managed by Google. You cannot download it.

If you lose your upload key, you can request a key upgrade through Play Console (requires an account in good standing).

---

## Useful commands

```bash
# Build release AAB
./gradlew bundleRelease

# Build release APK (for sideloading / testing)
./gradlew assembleRelease

# Run all unit tests before release
./gradlew testReleaseUnitTest

# Check the AAB is properly signed
bundletool validate --bundle=app/build/outputs/bundle/release/app-release.aab
```
