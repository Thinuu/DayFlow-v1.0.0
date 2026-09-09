# Room Schema Exports

This directory contains auto-generated JSON schema snapshots produced by Room's
KSP annotation processor on each build.

**Commit these files to version control.**

They serve as a precise record of the database schema at every version, enabling:
- Safe schema diff reviews before releasing an update
- Verification that your `Migration` objects match the actual schema change
- Automated `MigrationTestHelper` tests (see Android documentation)

## Generated path

Files are written to `app/schemas/com.example.data.local.AppDatabase/<version>.json`
by the KSP argument configured in `app/build.gradle.kts`:

```kotlin
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
```

## Current versions

| Version | Summary |
|---------|---------|
| 1–3 | Initial schema (no schema JSON available for earlier versions) |
| 4 | Added performance indices on `expenses`, `savings_contributions`, `bills`, `routine_completions`, `category_budgets` — purely additive, no data loss |
