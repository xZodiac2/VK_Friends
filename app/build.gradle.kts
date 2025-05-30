plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  id("org.jetbrains.kotlin.plugin.serialization")
  kotlin("kapt")
}

android {
  namespace = "com.ilya.vkfriends"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.ilya.vkfriends"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
    addManifestPlaceholders(
      mapOf(
        "VKIDRedirectHost" to "vk.com",
        "VKIDRedirectScheme" to "vk51848121",
        "VKIDClientID" to "51848121",
        "VKIDClientSecret" to "6HSuOmK8zGfCaXw0ZlR8"
      )
    )
  }
  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      signingConfig = signingConfigs.getByName("debug")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.6"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}


dependencies {
  // Project
  implementation(project(":feature:auth"))
  implementation(project(":feature:friendsView"))
  implementation(project(":feature:profileView"))
  implementation(project(":feature:search"))
  implementation(project(":core"))
  implementation(project(":theme"))

  // Hilt
  implementation("com.google.dagger:hilt-android:2.46")
  implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
  kapt("com.google.dagger:hilt-android-compiler:2.46")

  // Navigation
  implementation("androidx.navigation:navigation-compose:2.8.0-alpha08")

  // Serialization
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

  // OneTap
  implementation("com.vk.id:onetap-compose:1.0.0")

  // Coil Compose
  implementation("io.coil-kt:coil-compose:2.5.0")

  // Moshi
  implementation("com.squareup.moshi:moshi:1.15.0")
  implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

  implementation("androidx.core:core-splashscreen:1.0.1")

  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
  implementation("androidx.activity:activity-compose:1.8.2")
  implementation(platform("androidx.compose:compose-bom:2023.08.00"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.compose.material3:material3")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}