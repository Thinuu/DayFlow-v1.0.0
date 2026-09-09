package com.example.data.service

import android.app.Activity
import android.content.Context
import com.example.AppConfig
import com.example.data.local.preferences.UserPreferencesRepository
import com.example.data.model.PricingPlan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Billing & Subscription interface for DayFlow.
 *
 * This interface decouples the ViewModel and UI from any specific payment SDK.
 * Two implementations are provided:
 *
 *  - [NoOpBillingService]          — default; no Play Store dependency; all features
 *                                    remain unlocked. Suitable for Envato distribution
 *                                    where no license gate is needed.
 *
 *  - [GooglePlayBillingService]    — stub template; wire the Google Play Billing
 *                                    Library here when you publish to the Play Store.
 *                                    See documentation/BILLING_SETUP.md for steps.
 *
 * ============================================================
 * HOW TO SWAP THE BILLING IMPLEMENTATION
 * ============================================================
 * In MainViewModel (or your DI setup), replace:
 *
 *     val billingService: BillingService = NoOpBillingService(preferencesRepository)
 *
 * with:
 *
 *     val billingService: BillingService = GooglePlayBillingService(context, preferencesRepository)
 *
 * Then call billingService.initialize() in your ViewModel init block.
 * No UI code needs to change — it only observes [isProUser] and [availableProducts].
 * ============================================================
 */
interface BillingService {
    /** Emits the current Pro entitlement state. Observed by UI to show/hide Pro badges. */
    val isProUser: StateFlow<Boolean>

    /** Emits the list of available pricing plans to display in the paywall UI. */
    val availableProducts: StateFlow<List<PricingPlan>>

    /** Called once on app start to restore persisted entitlement and load products. */
    suspend fun initialize()

    /**
     * Initiates a purchase flow for the given product ID.
     * [activity] is required by the Play Billing Library; pass null for no-op builds.
     * Returns [Result.success(true)] on successful purchase or local grant.
     */
    suspend fun purchasePro(activity: Activity?, productId: String): Result<Boolean>

    /**
     * Queries the billing backend to restore previously purchased entitlements.
     * Returns [Result.success(true)] if the user has an active entitlement.
     */
    suspend fun restorePurchases(): Result<Boolean>

    /**
     * Manually sets Pro status (used for test builds, promo codes, or restoring
     * from a non-Play-Store backup). Does not contact Google Play.
     */
    suspend fun setProStatusManual(isPro: Boolean)
}

/**
 * Default billing implementation for Envato distribution and offline/debug builds.
 *
 * - Does NOT contact Google Play.
 * - Does NOT gate any application features.
 * - Persists the "pro" flag to DataStore so it survives restarts.
 * - Buyers who want to add real IAP: replace with [GooglePlayBillingService].
 *
 * Envato Regular/Extended license terms are enforced by the marketplace,
 * not by runtime license checks in this code.
 */
class NoOpBillingService(
    private val preferencesRepository: UserPreferencesRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : BillingService {

    private val _isProUser = MutableStateFlow(false)
    override val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

    private val _availableProducts = MutableStateFlow<List<PricingPlan>>(emptyList())
    override val availableProducts: StateFlow<List<PricingPlan>> = _availableProducts.asStateFlow()

    override suspend fun initialize() {
        // Restore persisted entitlement from DataStore
        val prefs = preferencesRepository.userPreferencesFlow.first()
        _isProUser.value = prefs.isProUser

        // Populate product catalog from AppConfig so prices are editable in one place
        _availableProducts.value = buildProductCatalog()
    }

    override suspend fun purchasePro(activity: Activity?, productId: String): Result<Boolean> {
        // Preview-only: grants Pro locally without contacting a payment backend
        _isProUser.value = true
        preferencesRepository.setProUser(true)
        return Result.success(true)
    }

    override suspend fun restorePurchases(): Result<Boolean> {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        _isProUser.value = prefs.isProUser
        return Result.success(prefs.isProUser)
    }

    override suspend fun setProStatusManual(isPro: Boolean) {
        _isProUser.value = isPro
        preferencesRepository.setProUser(isPro)
    }
}

/**
 * Production template for Google Play Billing Library.
 *
 * ============================================================
 * TO ENABLE REAL GOOGLE PLAY IN-APP BILLING:
 * ============================================================
 * 1. Add the dependency to app/build.gradle.kts:
 *        implementation("com.android.billingclient:billing-ktx:7.0.0")
 *
 * 2. Create matching in-app products in your Google Play Console:
 *        Monetize → Products → In-app products / Subscriptions
 *        Use the IDs defined in AppConfig:
 *          - AppConfig.PLAY_STORE_PRO_LIFETIME_ID
 *          - AppConfig.PLAY_STORE_PRO_ANNUAL_ID
 *          - AppConfig.PLAY_STORE_PRO_MONTHLY_ID
 *
 * 3. Implement the BillingClient connection, queryProductDetails,
 *    launchBillingFlow, and PurchasesUpdatedListener inside this class.
 *    See documentation/BILLING_SETUP.md for the full integration guide.
 *
 * 4. In MainViewModel.init{}, swap:
 *        NoOpBillingService(preferencesRepository)
 *    for:
 *        GooglePlayBillingService(application, preferencesRepository)
 *
 * 5. No UI or ViewModel logic changes are needed — they observe [isProUser].
 * ============================================================
 */
class GooglePlayBillingService(
    private val context: Context,
    private val preferencesRepository: UserPreferencesRepository
) : BillingService {

    private val _isProUser = MutableStateFlow(false)
    override val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

    private val _availableProducts = MutableStateFlow<List<PricingPlan>>(emptyList())
    override val availableProducts: StateFlow<List<PricingPlan>> = _availableProducts.asStateFlow()

    override suspend fun initialize() {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        _isProUser.value = prefs.isProUser
        _availableProducts.value = buildProductCatalog()
        // TODO: Initialize BillingClient and query Play Store for real prices
    }

    override suspend fun purchasePro(activity: Activity?, productId: String): Result<Boolean> {
        return Result.failure(
            IllegalStateException(
                "Google Play Billing Client is not wired. " +
                    "Follow the steps in the GooglePlayBillingService KDoc " +
                    "and documentation/BILLING_SETUP.md."
            )
        )
    }

    override suspend fun restorePurchases(): Result<Boolean> {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        _isProUser.value = prefs.isProUser
        return Result.success(prefs.isProUser)
        // TODO: Query BillingClient.queryPurchasesAsync for INAPP + SUBS
    }

    override suspend fun setProStatusManual(isPro: Boolean) {
        _isProUser.value = isPro
        preferencesRepository.setProUser(isPro)
    }
}

/**
 * Builds the pricing plan catalog from [AppConfig] product IDs.
 * Prices shown here are display-only defaults; real prices come from Play Console
 * once [GooglePlayBillingService] queries [BillingClient.queryProductDetailsAsync].
 */
private fun buildProductCatalog(): List<PricingPlan> = listOf(
    PricingPlan(
        id = AppConfig.PLAY_STORE_PRO_LIFETIME_ID,
        name = "Lifetime VIP",
        price = "$39.99",
        subtext = "One-time purchase • Pay once, own forever",
        badge = "BEST VALUE",
        isPopular = true
    ),
    PricingPlan(
        id = AppConfig.PLAY_STORE_PRO_ANNUAL_ID,
        name = "Annual Pass",
        price = "$19.99 / yr",
        subtext = "Billed annually ($1.66 / mo) • 7-day free trial",
        badge = "SAVE 58%"
    ),
    PricingPlan(
        id = AppConfig.PLAY_STORE_PRO_MONTHLY_ID,
        name = "Monthly",
        price = "$3.99 / mo",
        subtext = "Billed monthly • Cancel anytime"
    )
)
