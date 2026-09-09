# AI Setup

## Optional AI Integration

DayFlow includes optional AI-powered financial insight capabilities.
Cloud AI connectivity requires the buyer to configure their own API credentials for the selected third-party AI provider.

Third-party AI services may charge separately for API usage.
These costs are not included with the purchase of this item.

---

## Local financial intelligence (ships and runs by default)

Implementation: `app/src/main/java/com/example/data/service/AIService.kt`

Key types:
- `AIProvider` — interface; implement to swap the AI backend
- `OfflineAIProvider` — default rule-based implementation (no API key, no network required)
- `AIService` — singleton manager; call `AIService.setProvider(yourProvider)` in `Application.onCreate()` to swap
- `InsightInput` — all contextual data passed into the provider
- `FinancialInsight` — a single insight card returned to the UI

The on-device analysis covers:

- Monthly expense vs budget pace
- Savings rate from income minus expense
- Savings goal proximity
- Unpaid bills
- Habit streaks and pending tasks

`MainViewModel` calls `AIService.generateOfflineInsights(...)` when building `DayFlowUiState.aiInsights`.
The insights dialog subtitle is "Private, on-device smart heuristics." No API key is required. No user ledger is uploaded.

`FEATURE_AI_INSIGHTS_ENABLED` in `AppConfig.kt` is the buyer toggle (default `true`).
Preferences also store `isAiEnabled` per user.

This is **not** a hosted large-language-model product. Do not market the APK as "fully cloud AI powered."

---

## How to swap in a custom AI provider

1. Create a class implementing `AIProvider`:

```kotlin
class MyCloudAIProvider : AIProvider {
    override suspend fun generateInsights(
        input: InsightInput
    ): Result<List<FinancialInsight>> {
        // Call your API here using input fields
        // Return Result.failure() on error — never throw
    }
}
```

2. Register it before the ViewModel initialises, e.g. in your `Application.onCreate()`:

```kotlin
AIService.setProvider(MyCloudAIProvider())
```

3. No ViewModel or UI code changes are needed.

---

## Optional cloud AI

There is **no shipped Gemini/OpenAI client** in Kotlin. `FEATURE_GEMINI_ONLINE_ENABLED` defaults to `false`.

`app/build.gradle.kts` includes `firebase-ai` and related Firebase artifacts so you can add Google AI / Firebase AI Logic later. The Google Services plugin warns if `google-services.json` is missing; the local app still builds.

To add cloud AI yourself:

1. Choose a provider (Firebase AI Logic / Gemini, OpenAI, Anthropic, Azure, etc.).
2. Create **your** project and **your** API credentials. Never commit real keys.
3. Copy `.env.example` to `.env` in the project root and set your key locally:
   ```
   GEMINI_API_KEY=your_key_here
   ```
4. Read keys via the Secrets Gradle plugin / `BuildConfig` (plugin already applied: `propertiesFileName = ".env"`, `defaultPropertiesFileName = ".env.example"`).
5. Implement `AIProvider` calling your API. Send only aggregated summary fields, not full transaction histories.
6. Register via `AIService.setProvider(...)`.
7. Set `FEATURE_GEMINI_ONLINE_ENABLED = true` only after that code exists.

---

## Where credentials go

| Allowed | Not allowed |
|---------|-------------|
| Local `.env` (gitignored) | Hardcoding keys in Kotlin, XML, HTML, or README |
| Play / Firebase console restricted keys | Shipping `google-services.json` with production secrets in the CodeCanyon zip |
| CI secrets | Committing `.env` with live values |

`.env.example` in this package contains **empty comments only**.

---

## API cost warning

Third-party AI services may charge separately for API usage.
These costs are not included with the purchase of this item.

Set quotas and budget alerts in the provider console.

---

## Security recommendations

- Restrict keys by app package / SHA-1 when the provider supports it.
- Use HTTPS only.
- Do not log full API keys.
- Tell end users if data leaves the device.
- Prefer sending aggregates, not full transaction histories, unless your privacy policy allows it.
- Rotate keys if they leak.

---

## Testing

Local: add a few expenses and routines → Home → AI Insights. You should see heuristic cards without network access.

Cloud: only after you implement a custom `AIProvider`. Failures are usually a missing `google-services.json`, empty key, quota exceeded, or network error.
