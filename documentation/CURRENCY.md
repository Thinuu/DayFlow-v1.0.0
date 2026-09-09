# Currency System

How DayFlow handles currency selection, persistence, formatting, and extension.

---

## Architecture overview

| Layer | File | Responsibility |
|-------|------|----------------|
| Definition | `AppConfig.kt` | `SUPPORTED_CURRENCIES` list, `DEFAULT_CURRENCY_SYMBOL`, `DEFAULT_CURRENCY_CODE` |
| Persistence | `UserPreferencesRepository.kt` | Saves symbol + code to DataStore |
| State | `MainViewModel.kt` | `currencySymbol` and `currencyCode` StateFlows |
| UI state | `MainViewModel.kt` | `DayFlowUiState.currencySymbol` + `.currencyCode` |
| Formatting | `Money.kt` | `Money.format(currencySymbol)` |

---

## Default currency

```kotlin
// AppConfig.kt
const val DEFAULT_CURRENCY_SYMBOL = "$"
const val DEFAULT_CURRENCY_CODE   = "USD"
```

Change both constants to change the out-of-box default. This affects fresh installs only — returning users retain their persisted preference.

---

## Supported currencies

Defined in `AppConfig.SUPPORTED_CURRENCIES` as a `List<CurrencyOption>`:

```kotlin
data class CurrencyOption(
    val symbol: String,      // e.g. "$"
    val code: String,        // ISO 4217, e.g. "USD"
    val displayName: String  // e.g. "US Dollar ($)"
)
```

Current list (12 currencies):

| Symbol | Code | Display name |
|--------|------|-------------|
| `$` | USD | US Dollar ($) |
| `€` | EUR | Euro (€) |
| `£` | GBP | British Pound (£) |
| `¥` | JPY | Japanese Yen (¥) |
| `₹` | INR | Indian Rupee (₹) |
| `C$` | CAD | Canadian Dollar (C$) |
| `A$` | AUD | Australian Dollar (A$) |
| `CHF` | CHF | Swiss Franc (CHF) |
| `R$` | BRL | Brazilian Real (R$) |
| `₱` | PHP | Philippine Peso (₱) |
| `₩` | KRW | South Korean Won (₩) |
| `zł` | PLN | Polish Złoty (zł) |

### Adding a new currency

Edit `AppConfig.kt` only — add one entry to `SUPPORTED_CURRENCIES`:

```kotlin
CurrencyOption("kr", "SEK", "Swedish Krona (kr)")
```

No other file needs to change. The new entry appears automatically in the Settings currency picker.

---

## How currency is persisted

When the user selects a currency (via Settings → Currency or Onboarding), `MainViewModel.setCurrency()` is called:

```kotlin
fun setCurrency(symbol: String, code: String = "") {
    currencySymbol.value = symbol
    val resolvedCode = if (code.isNotBlank()) code else
        AppConfig.SUPPORTED_CURRENCIES.firstOrNull { it.symbol == symbol }?.code
            ?: AppConfig.DEFAULT_CURRENCY_CODE
    currencyCode.value = resolvedCode
    viewModelScope.launch {
        preferencesRepository.setCurrency(symbol = symbol, code = resolvedCode)
    }
}
```

Both `symbol` and `code` are stored independently in DataStore under keys `currency_symbol` and `currency_code`.

On app restart the `init{}` block of `MainViewModel` collects `userPreferencesFlow` and hydrates the StateFlows from the persisted values — so the user's choice survives process death.

---

## How currency reaches the UI

`DayFlowUiState` carries both fields:

```kotlin
data class DayFlowUiState(
    ...
    val currencySymbol: String = AppConfig.DEFAULT_CURRENCY_SYMBOL,
    val currencyCode: String   = AppConfig.DEFAULT_CURRENCY_CODE,
    ...
)
```

Every screen receives `uiState` and passes `uiState.currencySymbol` to formatting calls. Dialogs that display amounts (e.g. `AddEditExpenseDialog`, `SetBudgetDialog`) receive `currencySymbol` as a parameter.

---

## Formatting amounts

**For simple display** (most screens):

```kotlin
"${uiState.currencySymbol}${String.format("%.2f", amount)}"
// e.g. "$1,234.50"
```

**For precision-critical display** (totals, percentage splits) use `Money.kt`:

```kotlin
val money = Money.fromDouble(amount)
val formatted = money.format(currencySymbol = uiState.currencySymbol)
// Returns e.g. "$1,234.50" with correct rounding
```

`Money.format()` uses `DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))` — thousands separator and two decimal places, always.

---

## Onboarding currency selection

On first launch `OnboardingDialog` is presented. The user picks their currency from the `AppConfig.SUPPORTED_CURRENCIES` list. `MainViewModel.completeOnboarding(currency, budget)` persists the choice to DataStore via `UserPreferencesRepository.setCurrency()` and marks onboarding complete.

---

## Currency code vs symbol

The `currencyCode` (ISO 4217, e.g. `"USD"`) is stored alongside the symbol so that:
- Backup JSON files can record the currency unambiguously.
- Future locale-aware formatting can use `java.util.Currency.getInstance(code)`.
- The cloud AI provider (if implemented) can receive the ISO code for context.

Currently the UI only renders the `symbol`. The `code` is available in `DayFlowUiState.currencyCode` for any future use.

---

## DataStore keys

| Key | Type | Default |
|-----|------|---------|
| `currency_symbol` | String | `AppConfig.DEFAULT_CURRENCY_SYMBOL` |
| `currency_code` | String | `AppConfig.DEFAULT_CURRENCY_CODE` |

Defined in `UserPreferencesRepository.PreferenceKeys`.
