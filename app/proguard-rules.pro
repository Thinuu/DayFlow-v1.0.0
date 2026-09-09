# =============================================================================
# DayFlow ProGuard / R8 Rules
# =============================================================================
# Applied to release builds via app/build.gradle.kts:
#   isMinifyEnabled   = true
#   isShrinkResources = true
#   proguardFiles(..., "proguard-rules.pro")
#
# TROUBLESHOOTING
# If the release build crashes with ClassNotFoundException / NoSuchMethodException
# that the debug build does not produce, the offending class is being stripped or
# renamed by R8. Add a -keep rule for it here, rebuild, and re-test.
# Use the crash stack trace to identify the exact class/method name.
# =============================================================================


# =============================================================================
# 1. KOTLIN
# =============================================================================
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings { *; }
-keepclassmembers class kotlin.jvm.internal.DefaultConstructorMarker { *; }
# Kotlin coroutines debug symbols (not needed in release)
-dontwarn kotlinx.coroutines.debug.*


# =============================================================================
# 2. ANDROID / COMPOSE
# =============================================================================
# Compose compiler already handles most of its own reflection needs via
# the compiler plugin. These rules cover edge cases.
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
# Required to preserve Compose animation states
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}


# =============================================================================
# 3. ROOM DATABASE
# =============================================================================
# Room generates code via KSP; the generated DAOs and database class must
# be kept so Room can instantiate them at runtime.
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers @androidx.room.Entity class * { *; }
-keepclassmembers @androidx.room.Dao interface * { *; }
# Room type converters
-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}
# Suppress warnings about missing internal Room classes
-dontwarn androidx.room.**


# =============================================================================
# 4. MOSHI JSON (used for backup/export serialisation)
# =============================================================================
# Moshi's code generation via KSP produces JsonAdapter classes — keep them.
-keep class **JsonAdapter { *; }
-keep class **JsonAdapter$* { *; }
# Keep all classes annotated with @JsonClass so Moshi can instantiate them.
-keepclasseswithmembers class * {
    @com.squareup.moshi.JsonClass *;
}
# Moshi uses reflection on data class constructors
-keepclassmembers @com.squareup.moshi.JsonClass class * {
    <init>(...);
    <fields>;
}
-dontwarn com.squareup.moshi.**


# =============================================================================
# 5. RETROFIT + OKHTTP (network client — used by optional cloud AI / billing)
# =============================================================================
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }


# =============================================================================
# 6. FIREBASE (firebase-ai, firebase-appcheck-recaptcha)
# =============================================================================
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**


# =============================================================================
# 7. DATASTORE PREFERENCES
# =============================================================================
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**


# =============================================================================
# 8. APP DATA MODELS
# =============================================================================
# Keep all Room @Entity data classes so their column names survive minification.
# These are also used in Moshi serialisation for JSON backup/export.
-keep class com.example.data.model.** { *; }
# Keep AppConfig so constant names and values survive (accessed by reflection-free
# code, but the class itself must not be removed during dead-code elimination).
-keep class com.example.AppConfig { *; }
-keep class com.example.CurrencyOption { *; }
-keep class com.example.DefaultCategory { *; }
-keep class com.example.SubscriptionPreset { *; }
-keep class com.example.StarterRoutineConfig { *; }


# =============================================================================
# 9. ENUM CLASSES
# =============================================================================
# Enums are stored as strings in Room and DataStore; R8 must not rename them.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    public final java.lang.String name();
    public final int ordinal();
}


# =============================================================================
# 10. VIEWMODEL / LIFECYCLE
# =============================================================================
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}


# =============================================================================
# 11. GENERAL ANDROID
# =============================================================================
# Parcelables
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
# Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# Keep line numbers in release stack traces for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
