plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // If using Kotlin Android Extensions or Parcelize
    // id("org.jetbrains.kotlin.android.extensions") // Older - not recommended
    // id("kotlin-parcelize")
}

android {
    namespace = "com.yourcompany.wellnessjourney" // Make sure this matches your package name
    compileSdk = 34 // Or latest API level you are targeting

    defaultConfig {
        applicationId = "com.yourcompany.wellnessjourney"
        minSdk = 24 // Or whatever minimum SDK you chose
        targetSdk = 34 // Should generally match compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true // Recommended for accessing views in fragments/activities
    }
}

dependencies {
    // Existing dependencies already here
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0")) // Adjust Kotlin BOM version if needed
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.fragment:fragment-ktx:1.5.7")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}