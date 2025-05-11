plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  kotlin("kapt")
}

android {
  namespace = "com.ilya.data"
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
}

dependencies {
  // Project
  implementation(project(":core"))

  // Retrofit
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")

  // Moshi
  implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
  implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

  // Hilt
  implementation("com.google.dagger:hilt-android:2.46")
  kapt("com.google.dagger:hilt-android-compiler:2.46")

  // OneTap
  implementation("com.vk.id:onetap-compose:1.0.0")

  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.11.0")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
