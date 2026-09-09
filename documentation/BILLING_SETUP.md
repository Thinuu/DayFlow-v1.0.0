# Billing Setup

Google Play Billing is **optional**. This source is fully usable without Play products, without a Play Developer account, and without any Envato license check.

Envato Regular/Extended licenses are **not** connected to Google Play subscriptions.

---

## BillingService architecture

File: `app/src/main/java/com/example/data/service/BillingService.kt`

```kotlin
interface BillingService {
    val isProUser: StateFlow<Boolean>
    val availableProducts: StateFlow<List<PricingPlan>>
    suspend fun initialize()
    suspend fun purchasePro(activity: Activity?, productId: String): Result<Boolean>
    suspend fun restorePurchases(): Result<Boolean>
    suspend fun setProStatusManual(isPro: Boolean)
}
```

`isProUser` is a **local/Play subscription flag** for buyers who monetize their own published app. It does **not** lock CodeCanyon features. `ProFeatureManager` always returns access.

Optional UI template: `PaywallDialog.kt` (not shown on the main path by default). `AppConfig.FEATURE_PREMIUM_PAYWALL_ENABLED` is `false`.

---

## NoOpBillingService

Default implementation for Envato distribution and apps that do not sell IAP.

- Does not call Google Play.
- Lists placeholder `PricingPlan` rows via the shared `buildProductCatalog()` helper, which reads IDs from `AppConfig`.
- `purchasePro` only writes a DataStore preference. It does not unlock extra source features (those are already available to all buyers).
- Use this when you are not monetizing, or while you test UI.

To wire it, add to `MainViewModel.init{}`:

```kotlin
val billingService: BillingService = NoOpBillingService(preferencesRepository)
viewModelScope.launch { billingService.initialize() }
```

---

## GooglePlayBillingService

Production **template**. `purchasePro` returns `Result.failure` until you:

1. Add `implementation("com.android.billingclient:billing-ktx:7.0.0")` to `app/build.gradle.kts`.
2. Create products in Play Console with IDs matching your `AppConfig` constants.
3. Implement `BillingClient`, `queryProductDetailsAsync`, and `launchBillingFlow` inside `GooglePlayBillingService`.
4. Handle `PurchasesUpdatedListener` and purchase acknowledgment.
5. In `MainViewModel.init{}`, replace `NoOpBillingService` with `GooglePlayBillingService(application, preferencesRepository)`.

No UI or ViewModel code needs to change — they only observe `isProUser` and `availableProducts`.

Do not ship a fake "unlock everything" button as if it were a real Play purchase.

---

## Google Play Console setup

1. Play Console → your app (must use **your** `applicationId`).
2. Monetization / products: one-time product and/or subscriptions.
3. Product IDs must match code exactly.

Placeholders in `AppConfig.kt` (replace with **your** IDs; these are not live store SKUs):

```kotlin
const val PLAY_STORE_PRO_LIFETIME_ID = "dayflow_pro_lifetime"
const val PLAY_STORE_PRO_ANNUAL_ID   = "dayflow_pro_annual_sub"
const val PLAY_STORE_PRO_MONTHLY_ID  = "dayflow_pro_monthly_sub"
```

No real merchant credentials or license keys are included in this package.

---

## Product IDs

| Constant | Typical Play type |
|----------|-------------------|
| `PLAY_STORE_PRO_LIFETIME_ID` | One-time in-app product |
| `PLAY_STORE_PRO_ANNUAL_ID` | Subscription base plan (yearly) |
| `PLAY_STORE_PRO_MONTHLY_ID` | Subscription base plan (monthly) |

Prices shown in `NoOpBillingService` / `PaywallDialog` are **UI samples**. Play Console prices are authoritative after you go live.

---

## Testing

- License testers in Play Console.
- Internal testing track, signed with the **same key** you upload.
- Real `BillingClient` only works on devices with Play Store.
- `NoOpBillingService` is for local UI/dev, not Play purchase validation.

---

## Production configuration checklist

- [ ] Replace `applicationId` in `build.gradle.kts` with your own reverse-domain identifier.
- [ ] Create Play Console products with matching IDs.
- [ ] Implement purchase acknowledgment / subscription query in `GooglePlayBillingService`.
- [ ] Handle `PURCHASES_UPDATED` listener.
- [ ] Call `restorePurchases()` on app startup.
- [ ] Keep Envato item license and Play IAP as separate concerns.
- [ ] Do not commit Play service account JSON or API keys to source control.

Application features work without billing being configured.
