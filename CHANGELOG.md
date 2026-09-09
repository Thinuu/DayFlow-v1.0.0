# Changelog

All notable changes to **DayFlow | Personal Finance and Daily Planner Android App** are documented in this file.

## [1.0.0] - 2026-09-01

### Commercial / marketplace
- Envato Regular/Extended licenses no longer described as feature gates. Application features ship to all buyers.
- Optional Google Play Billing remains available via `BillingService` / `NoOpBillingService` / `GooglePlayBillingService`.
- Buyer documentation moved into `documentation/` (installation, customization, AI, billing, release, troubleshooting).
- Marketplace title standardized to: DayFlow | Personal Finance and Daily Planner Android App.

### Added
- Safe ledger math (`Money.kt`) using integer cents.
- Multi-account tracking, transfers, custom categories.
- Centralized buyer configuration (`AppConfig.kt`).
- On-device financial insights (`AIService.kt`) plus optional cloud AI configuration for buyers.
- JSON backup/restore and CSV export.
- Notification channels for habits, bills, and budget alerts.
- 4-digit PIN lock (local).
- Unit tests for money math, backup validation, budget calculations, and unrestricted feature access.
