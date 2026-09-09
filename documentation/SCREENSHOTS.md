# Screenshots Guide

Guidelines for capturing screenshots and preparing Play Store / Envato gallery assets.

---

## Required Play Store assets

Google Play requires the following for every app listing:

| Asset | Dimensions | Format | Notes |
|-------|-----------|--------|-------|
| Feature graphic | 1024 × 500 px | JPG or PNG | Shown at top of store listing |
| Phone screenshots | Min 2, max 8 | JPG or PNG, 16:9 or 9:16 | At least 320 px on shortest side |
| 7-inch tablet screenshots (optional) | Min 1 | JPG or PNG | Required if you target tablets |
| 10-inch tablet screenshots (optional) | Min 1 | JPG or PNG | Required if you target tablets |

All screenshots must be captured from an actual build — no mockups or composites are accepted by Google Play.

---

## Recommended screenshot set (8 screens)

Capture these screens to cover every major feature:

| # | Screen | What to show |
|---|--------|-------------|
| 1 | **Today — Home** | Date strip, DayFlow Score card, today's routines (some checked), today's transactions |
| 2 | **Finance** | Account cards with balances, transaction list with income and expense entries |
| 3 | **Savings** | Two or three goal cards with progress rings at different stages |
| 4 | **Bills** | Monthly subscription list with some marked paid |
| 5 | **Routines** | Habit list with streak badges and completion toggles |
| 6 | **Analytics** | Category breakdown bar chart, monthly summary totals |
| 7 | **AI Insights dialog** | Two or three insight cards — one WARNING, one CELEBRATION |
| 8 | **Settings sheet** | Open settings with currency, budget, security options visible |

---

## How to capture screenshots in Android Studio

1. Run the app on an emulator (Pixel 6 Pro at API 35 gives clean Pixel UI).
2. Navigate to the desired screen and load the demo data state you want to show.
3. In the **Logcat** toolbar, click the camera icon (📷 "Take Screenshot") or use:
   - macOS: `Cmd+S` in the emulator window
   - Windows: emulator toolbar → camera icon
4. Save as PNG.

For frame overlays (device chrome around the screenshot), use the [Device Art Generator](https://developer.android.com/distribute/marketing-tools/device-art-generator) or Figma with a free device frame component.

---

## Demo data state for screenshots

The app seeds rich demo data on first install. To ensure a good-looking state for screenshots:

1. **Fresh install** on a clean emulator — demo data seeds automatically.
2. Navigate to each tab and verify data is populated.
3. On the **Today** tab: mark 2–3 routines as complete to show the streak and completion ring.
4. On the **Finance** tab: the seeded transactions cover salary, groceries, transport, and utilities — all categories will show in Analytics.
5. On the **Savings** tab: the MacBook and Tokyo goals are at 74% and 65% progress respectively — visually compelling rings.
6. On the **Bills** tab: mark the Internet bill as paid for a mixed paid/unpaid state.

If demo data was cleared, use **Settings → Backup & Restore → Restore Demo Data** to reseed.

---

## Emulator settings for clean screenshots

For the cleanest screenshots:

- Use a **Pixel 6 Pro** emulator profile (2560 × 1440, 560 dpi).
- Set the system time to a clean value (e.g. 9:41 AM — the traditional screenshot time).
- Enable **Do Not Disturb** mode on the emulator to suppress notification icons.
- Use **Light / Dark** theme as appropriate — DayFlow defaults to Dark theme.

To set the emulator time:

```bash
adb shell date MMDDHHmmYYYY.SS
# e.g. for Sep 5, 9:41 AM 2026:
adb shell date 090509412026.00
```

---

## Envato / CodeCanyon preview images

CodeCanyon requires a main preview image and optional additional previews.

| Asset | Recommended size | Notes |
|-------|-----------------|-------|
| Main preview | 590 × 300 px | Shown in search results |
| Screenshots / previews | 590 × auto | Up to 8 additional images |
| Video preview | Optional | MP4, 30–90 seconds |

**Suggested layout for the main preview**: place two or three device frames side by side (Today tab, Finance tab, Analytics tab) on a dark or branded background matching `AppConfig.COLOR_PRIMARY_HEX` (#8B5CF6 Radiant Purple).

---

## Automated screenshot testing (Roborazzi)

DayFlow includes Roborazzi for automated screenshot tests:

```bash
./gradlew recordRoborazziDebug
```

Captured screenshots are saved to `app/build/outputs/roborazzi/`. These are reference images for regression testing, not Play Store assets.

To run screenshot comparison tests:

```bash
./gradlew verifyRoborazziDebug
```

A test failure means a UI element changed. Review the diff images in `build/outputs/roborazzi/` and update references with `recordRoborazzi` when the change is intentional.
