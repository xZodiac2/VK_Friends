plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  kotlin("kapt")
}

android {
  namespace = "com.ilya.friendsview"
  compileSdk = 34

  defaultConfig {
    minSdk = 26

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")

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
  implementation(project(":paging"))
  implementation(project(":core"))
  implementation(project(":theme"))

  // Hilt
  implementation("com.google.dagger:hilt-android:2.46")
  implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
  kapt("com.google.dagger:hilt-android-compiler:2.46")

  // Coil Compose
  implementation("io.coil-kt:coil-compose:2.5.0")

  // Paging
  implementation("androidx.paging:paging-runtime:3.2.1")
  implementation("androidx.paging:paging-compose:3.3.0-alpha05")

  // OneTap
  implementation("com.vk.id:onetap-compose:1.0.0")

  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
  implementation("androidx.activity:activity-compose:1.8.2")
  implementation(platform("androidx.compose:compose-bom:2024.02.01"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.material:material:1.6.7")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.01"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}