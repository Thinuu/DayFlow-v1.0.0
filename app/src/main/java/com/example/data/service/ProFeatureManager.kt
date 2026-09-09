package com.example.data.service

/**
 * Named feature catalog used by optional Google Play Billing UI.
 *
 * Envato Regular/Extended licenses do not gate these features.
 * CodeCanyon buyers receive the full application without a license check.
 */
enum class ProFeature(val title: String, val description: String) {
    ADVANCED_ANALYTICS("Deep Financial Analytics", "Interactive category distribution charts, runway projections, and burn velocity."),
    UNLIMITED_ACCOUNTS("Unlimited Financial Accounts", "Connect and track unlimited bank accounts, wallets, and investments."),
    UNLIMITED_SAVINGS_GOALS("Unlimited Savings Goals", "Create unlimited targeted savings goals with automated milestone tracking."),
    UNLIMITED_ROUTINES("Unlimited Daily Routines", "Track unlimited daily habits and morning/evening focus routines."),
    AI_DEEP_INSIGHTS("AI Financial & Wellness Insights", "Algorithmic DayFlow wellness score, spending leak alerts, and streak recommendations."),
    FULL_DATA_EXPORT_IMPORT("Full Data Backup & Restore", "Export complete schema-versioned JSON snapshots and CSV spreadsheet reports."),
    STARTER_BLUEPRINTS("Starter Blueprint Packs", "One-tap installation of curated habit and budget starter systems.")
}

object ProFeatureManager {

    /**
     * Application features are always available.
     * [isProUser] is retained for optional Play Billing status only and is not used to lock features.
     */
    @Suppress("UNUSED_PARAMETER")
    fun canAccess(feature: ProFeature, isProUser: Boolean): Boolean = true

    @Suppress("UNUSED_PARAMETER")
    fun isWithinAccountLimit(currentAccountCount: Int, isProUser: Boolean): Boolean = true

    @Suppress("UNUSED_PARAMETER")
    fun isWithinSavingsGoalLimit(currentGoalCount: Int, isProUser: Boolean): Boolean = true

    @Suppress("UNUSED_PARAMETER")
    fun isWithinRoutineLimit(currentRoutineCount: Int, isProUser: Boolean): Boolean = true
}
