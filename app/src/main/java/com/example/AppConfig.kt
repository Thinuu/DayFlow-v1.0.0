package com.example

import com.example.data.model.BillFrequency
import com.example.data.model.ExpenseCategory
import com.example.data.model.RoutineCategory
import com.example.data.model.TimeOfDay

/**
 * ==============================================================================
 * DayFlow — Centralized Customization & Configuration Engine
 * ==============================================================================
 *
 * This single configuration file controls the entire application's branding,
 * color palette, default currency, categories, starter habits, subscription presets,
 * feature flags, and Google Play Billing product IDs.
 *
 * You can rebrand and customize this app in under 5 minutes without touching any UI code!
 */
object AppConfig {

    // --------------------------------------------------------------------------
    // 1. APPLICATION IDENTITY & BRANDING
    // --------------------------------------------------------------------------
    const val APP_NAME = "DayFlow"
    const val APP_SLOGAN = "Smart Personal Finance & Daily Planner"
    const val APP_VERSION_NAME = "1.0.0"
    const val APP_VERSION_CODE = 1

    /**
     * APPLICATION ID / PACKAGE NOTES FOR BUYERS
     * ==========================================
     * There are three distinct identifiers in this project:
     *
     *  1. Kotlin namespace (package):   com.example
     *     Declared in app/build.gradle.kts → android.namespace
     *     Used in all Kotlin `package` declarations and import paths.
     *
     *  2. Gradle applicationId:         com.yourcompany.dayflow  ← CHANGE THIS
     *     Declared in app/build.gradle.kts → android.defaultConfig.applicationId
     *     This is the Google Play / device identifier. Must be unique per published app.
     *     Change it to your own reverse-domain identifier BEFORE publishing
     *     (e.g. "com.acme.dayflow"). Update google-services.json to match if using Firebase.
     *
     *  3. APPLICATION_ID below mirrors the Gradle applicationId for runtime use
     *     (e.g. FileProvider authority, deep links). Keep it in sync with the
     *     Gradle value whenever you change it.
     *
     * SAFE TO CHANGE: applicationId in build.gradle.kts + APPLICATION_ID below.
     * REQUIRES CAUTION: Renaming the Kotlin namespace (com.example) requires a full
     *   refactor of all package declarations. Only do this if your tooling supports
     *   project-wide rename. See documentation/PACKAGE_RENAME.md.
     */
    const val APPLICATION_ID = "com.yourcompany.dayflow"

    // --------------------------------------------------------------------------
    // 2. BRAND COLOR PALETTE (Theme Tokens)
    // --------------------------------------------------------------------------
    const val COLOR_PRIMARY_HEX = 0xFF8B5CF6 // Radiant Purple
    const val COLOR_SECONDARY_HEX = 0xFF10B981 // Emerald Green
    const val COLOR_ACCENT_CORAL_HEX = 0xFFF43F5E // Coral Red
    const val COLOR_GOLD_VIP_HEX = 0xFFFFD700 // VIP Gold
    const val COLOR_SURFACE_DARK_HEX = 0xFF1C1B1F
    const val COLOR_BACKGROUND_DARK_HEX = 0xFF0F0E17

    // --------------------------------------------------------------------------
    // 3. DEFAULT CURRENCY & LOCALIZATION
    // --------------------------------------------------------------------------
    const val DEFAULT_CURRENCY_SYMBOL = "$"
    const val DEFAULT_CURRENCY_CODE = "USD"

    val SUPPORTED_CURRENCIES = listOf(
        CurrencyOption("$", "USD", "US Dollar ($)"),
        CurrencyOption("€", "EUR", "Euro (€)"),
        CurrencyOption("£", "GBP", "British Pound (£)"),
        CurrencyOption("¥", "JPY", "Japanese Yen (¥)"),
        CurrencyOption("₹", "INR", "Indian Rupee (₹)"),
        CurrencyOption("C$", "CAD", "Canadian Dollar (C$)"),
        CurrencyOption("A$", "AUD", "Australian Dollar (A$)"),
        CurrencyOption("CHF", "CHF", "Swiss Franc (CHF)"),
        CurrencyOption("R$", "BRL", "Brazilian Real (R$)"),
        CurrencyOption("₱", "PHP", "Philippine Peso (₱)"),
        CurrencyOption("₩", "KRW", "South Korean Won (₩)"),
        CurrencyOption("zł", "PLN", "Polish Złoty (zł)")
    )

    // --------------------------------------------------------------------------
    // 4. COMMERCIAL FEATURE FLAGS (Toggle modules ON/OFF)
    // --------------------------------------------------------------------------
    const val FEATURE_AI_INSIGHTS_ENABLED = true
    // Cloud AI is optional. Local on-device insights work with this flag false.
    const val FEATURE_GEMINI_ONLINE_ENABLED = false
    const val FEATURE_SECURITY_PIN_ENABLED = true
    const val FEATURE_DATA_EXPORT_ENABLED = true
    const val FEATURE_DATA_IMPORT_ENABLED = true
    const val FEATURE_NOTIFICATIONS_ENABLED = true
    // Optional Play Billing UI for the buyer's published app. Not an Envato license gate.
    const val FEATURE_PREMIUM_PAYWALL_ENABLED = false
    const val FEATURE_STARTER_BLUEPRINTS_ENABLED = true
    const val FEATURE_CUSTOM_CATEGORIES_ENABLED = true
    const val FEATURE_ACCOUNT_TRANSFERS_ENABLED = true

    // --------------------------------------------------------------------------
    // 5. GOOGLE PLAY BILLING PRODUCT IDs
    // --------------------------------------------------------------------------
    // Configure these with the exact In-App Products and Subscriptions created in
    // your Google Play Console -> Monetize -> Products:
    const val PLAY_STORE_PRO_LIFETIME_ID = "dayflow_pro_lifetime"
    const val PLAY_STORE_PRO_ANNUAL_ID = "dayflow_pro_annual_sub"
    const val PLAY_STORE_PRO_MONTHLY_ID = "dayflow_pro_monthly_sub"

    // --------------------------------------------------------------------------
    // 6. FINANCIAL DEFAULTS & SAFETY LIMITS
    // --------------------------------------------------------------------------
    const val DEFAULT_MONTHLY_BUDGET = 2500.00
    const val DEFAULT_DAILY_BUDGET = 80.00
    const val MINIMUM_TRANSACTION_AMOUNT = 0.01
    const val MAXIMUM_TRANSACTION_AMOUNT = 10_000_000.00

    // --------------------------------------------------------------------------
    // 7. DEFAULT SUBSCRIPTION PRESETS (Subscriptions Catalog)
    // --------------------------------------------------------------------------
    val DEFAULT_SUBSCRIPTION_PRESETS = listOf(
        SubscriptionPreset("Netflix 4K Premium", 19.99, ExpenseCategory.ENTERTAINMENT, BillFrequency.MONTHLY, 12, "movie", 0xFFE50914),
        SubscriptionPreset("Spotify Duo / Premium", 14.99, ExpenseCategory.ENTERTAINMENT, BillFrequency.MONTHLY, 18, "music_note", 0xFF1DB954),
        SubscriptionPreset("Equinox / Gym Membership", 75.00, ExpenseCategory.HEALTH, BillFrequency.MONTHLY, 1, "fitness_center", 0xFF10B981),
        SubscriptionPreset("Google One 2TB Cloud", 9.99, ExpenseCategory.BILLS, BillFrequency.MONTHLY, 25, "cloud", 0xFF4285F4),
        SubscriptionPreset("High-Speed Fiber Wi-Fi", 65.00, ExpenseCategory.BILLS, BillFrequency.MONTHLY, 5, "wifi", 0xFF8B5CF6),
        SubscriptionPreset("Amazon Prime Membership", 14.99, ExpenseCategory.SHOPPING, BillFrequency.MONTHLY, 15, "shopping_cart", 0xFFFF9900),
        SubscriptionPreset("YouTube Premium", 13.99, ExpenseCategory.ENTERTAINMENT, BillFrequency.MONTHLY, 22, "play_circle", 0xFFFF0000),
        SubscriptionPreset("Apple One Bundle", 19.95, ExpenseCategory.ENTERTAINMENT, BillFrequency.MONTHLY, 8, "phone_iphone", 0xFF94A3B8)
    )

    // --------------------------------------------------------------------------
    // 8. DEFAULT STARTER HABITS & FOCUS ROUTINES
    // --------------------------------------------------------------------------
    val DEFAULT_STARTER_ROUTINES = listOf(
        StarterRoutineConfig("Morning Hydration (500ml)", "Drink 1 glass of mineral water right after waking up.", RoutineCategory.WELLNESS, 5, TimeOfDay.MORNING, "water_drop", 0xFF06B6D4),
        StarterRoutineConfig("Deep Focus Work Session", "60 minutes uninterrupted creative or deep technical work.", RoutineCategory.PRODUCTIVITY, 60, TimeOfDay.MORNING, "psychology", 0xFF8B5CF6),
        StarterRoutineConfig("Mindful Walk / Exercise", "Outdoor walk or cardio workout to reset energy.", RoutineCategory.FITNESS, 30, TimeOfDay.AFTERNOON, "directions_walk", 0xFF10B981),
        StarterRoutineConfig("Evening Financial Review", "Log daily receipts and check remaining budget balance.", RoutineCategory.PRODUCTIVITY, 10, TimeOfDay.EVENING, "account_balance_wallet", 0xFFF59E0B),
        StarterRoutineConfig("Night Reading & Wind-down", "Read 15 pages of non-fiction book before sleeping.", RoutineCategory.LEARNING, 20, TimeOfDay.EVENING, "menu_book", 0xFFA855F7)
    )

    // --------------------------------------------------------------------------
    // 9. BACKUP & SCHEMA SETTINGS
    // --------------------------------------------------------------------------
    const val BACKUP_SCHEMA_VERSION = 4   // matches @Database(version = 4) in AppDatabase.kt
    const val BACKUP_FILE_PREFIX = "dayflow_backup_"

    // --------------------------------------------------------------------------
    // 10. DEFAULT CUSTOM CATEGORIES (Seed data — do not duplicate in Repository)
    // --------------------------------------------------------------------------
    /**
     * These custom categories are seeded once on first launch via
     * DayFlowRepository.seedInitialDataIfEmpty().
     *
     * Income categories: type = "INCOME"
     * Expense categories: type = "EXPENSE"
     *
     * To add a new default category: add an entry here and it will be inserted
     * automatically on a fresh install. Existing user data is never modified.
     *
     * Note: The built-in ExpenseCategory enum (Expense.kt) covers the primary
     * finance categories. Custom categories here extend that set with user-facing
     * optional categories that buyers can adjust freely.
     */
    val DEFAULT_CUSTOM_CATEGORIES = listOf(
        // Income categories
        DefaultCategory(name = "Side Hustle",     type = "INCOME",  iconKey = "work",         colorHex = 0xFF10B981L),
        DefaultCategory(name = "Rental Income",   type = "INCOME",  iconKey = "home",         colorHex = 0xFF06B6D4L),
        DefaultCategory(name = "Gift Received",   type = "INCOME",  iconKey = "card_giftcard", colorHex = 0xFFEC4899L),
        // Expense categories
        DefaultCategory(name = "Dining Out",      type = "EXPENSE", iconKey = "restaurant",    colorHex = 0xFFF97316L),
        DefaultCategory(name = "Tech & Gadgets",  type = "EXPENSE", iconKey = "devices",       colorHex = 0xFF6366F1L),
        DefaultCategory(name = "Self Care",       type = "EXPENSE", iconKey = "spa",            colorHex = 0xFFEC4899L),
        DefaultCategory(name = "Subscriptions",   type = "EXPENSE", iconKey = "subscriptions",  colorHex = 0xFF8B5CF6L),
        DefaultCategory(name = "Travel",          type = "EXPENSE", iconKey = "flight_takeoff", colorHex = 0xFF14B8A6L)
    )

    // --------------------------------------------------------------------------
    // 11. SUPPORT & LEGAL LINKS (Update with buyer's company URLs)
    // --------------------------------------------------------------------------
    const val SUPPORT_EMAIL = "support@yourdomain.com"
    const val PRIVACY_POLICY_URL = "https://yourdomain.com/privacy"
    const val TERMS_OF_SERVICE_URL = "https://yourdomain.com/terms"
    const val DOCUMENTATION_URL = "https://yourdomain.com/docs"
}

data class CurrencyOption(
    val symbol: String,
    val code: String,
    val displayName: String
)

data class SubscriptionPreset(
    val title: String,
    val defaultAmount: Double,
    val category: ExpenseCategory,
    val frequency: BillFrequency,
    val defaultDueDay: Int,
    val iconKey: String,
    val colorHex: Long
)

data class StarterRoutineConfig(
    val title: String,
    val note: String,
    val category: RoutineCategory,
    val timeMinutes: Int,
    val timeOfDay: TimeOfDay,
    val iconKey: String,
    val colorHex: Long,
    val targetDaysMask: Int = 127
)

/**
 * Represents a default custom category to seed on first launch.
 * @param type Either "INCOME" or "EXPENSE" — matches TransactionType.name
 */
data class DefaultCategory(
    val name: String,
    val type: String,
    val iconKey: String,
    val colorHex: Long
)
