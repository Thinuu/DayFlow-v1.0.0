plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
}

android {
  // ─────────────────────────────────────────────────────────────────────────
  // NAMESPACE vs APPLICATION ID — READ BEFORE PUBLISHING
  // ─────────────────────────────────────────────────────────────────────────
  //
  // namespace      = Kotlin package root used in all `package com.example.*`
  //                  declarations and in the generated R / BuildConfig classes.
  //                  Changing this requires a project-wide rename of every
  //                  Kotlin source file. Only do this with IDE refactor tooling.
  //
  // applicationId  = Unique identifier on Google Play and on the device.
  //                  CHANGE THIS before publishing your app.
  //                  Format: reverse-domain, e.g. "com.acme.dayflow"
  //                  Must also match your google-services.json package_name
  //                  if you use Firebase. Update AppConfig.APPLICATION_ID too.
  //
  // Current state:
  //   namespace     = "com.example"        (Kotlin package — leave as-is unless
  //                                         you do a full project rename)
  //   applicationId = "com.yourcompany.dayflow"  ← PLACEHOLDER — change this!
  // ─────────────────────────────────────────────────────────────────────────
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.yourcompany.dayflow" // ← Replace with your own ID before publishing
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  // ─────────────────────────────────────────────────────────────────────────
  // RELEASE SIGNING
  // ─────────────────────────────────────────────────────────────────────────
  // No keystore is included in this zip — you must supply your own.
  // Signing is activated automatically when ALL four environment variables
  // below are set. Debug builds work without them.
  //
  // Environment variables:
  //   KEYSTORE_PATH   — absolute path to your .jks / .keystore file
  //   STORE_PASSWORD  — keystore password
  //   KEY_ALIAS       — key alias
  //   KEY_PASSWORD    — key password
  //
  // See documentation/RELEASE_BUILD.md for full instructions.
  // ─────────────────────────────────────────────────────────────────────────
  val hasReleaseKeystore = !System.getenv("KEYSTORE_PATH").isNullOrBlank() &&
    !System.getenv("STORE_PASSWORD").isNullOrBlank() &&
    !System.getenv("KEY_ALIAS").isNullOrBlank() &&
    !System.getenv("KEY_PASSWORD").isNullOrBlank()

  signingConfigs {
    if (hasReleaseKeystore) {
      create("release") {
        storeFile = file(System.getenv("KEYSTORE_PATH")!!)
        storePassword = System.getenv("STORE_PASSWORD")
        keyAlias = System.getenv("KEY_ALIAS")
        keyPassword = System.getenv("KEY_PASSWORD")
      }
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false

      // R8 minification + resource shrinking enabled for release builds.
      // Rules are in app/proguard-rules.pro.
      // If you encounter crashes in release that don't occur in debug, add
      // -keep rules for the specific class shown in the stack trace.
      isMinifyEnabled = true
      isShrinkResources = true

      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      if (hasReleaseKeystore) {
        signingConfig = signingConfigs.getByName("release")
      }
    }
    debug {
      // Debug builds are NOT minified so stack traces are readable and
      // incremental builds remain fast.
      isMinifyEnabled = false
      applicationIdSuffix = ".debug"
    }
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true   // enables java.time on API 24-25
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  // Export Room database schema files to app/schemas/ for version-control diffing.
  // Commit these JSON files — they document every schema change and enable safe migrations.
  ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

dependencies {
  coreLibraryDesugaring(libs.desugar.jdk.libs)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
}
