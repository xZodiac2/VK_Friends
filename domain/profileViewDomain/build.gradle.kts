plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  id("kotlin-parcelize")
  kotlin("kapt")
}

android {
  namespace = "com.ilya.profileviewdomain"
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
  implementation(project(":data"))
  implementation(project(":core"))
  implementation(project(":paging"))

  // Hilt
  implementation("com.google.dagger:hilt-android:2.46")
  kapt("com.google.dagger:hilt-android-compiler:2.46")

  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.12.0")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}