plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // If you need Kotlin Parcelize (for passing objects safely between components)
    // id("kotlin-parcelize")
}

android {
    namespace = "com.yourcompany.wellnessjourney" // <<< Make sure this is YOUR CORRECT PACKAGE NAME
    compileSdk = 35 // Updated to latest stable API Level

    defaultConfig {
        applicationId = "com.yourcompany.wellnessjourney" // <<< Make sure this is YOUR CORRECT APPLICATION ID
        minSdk = 24 // Keep this as you intended (e.g., API 24: Android 7.0 Nougat)
        targetSdk = 35 // Should match compileSdk
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
    // Kotlin BOM (Bill of Materials) - For managing Kotlin versions across libraries
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.0")) // Updated Kotlin BOM to latest stable (September 2025)

    // AndroidX Core KTX - Essential Android extensions
    implementation("androidx.core:core-ktx:1.13.1") // Updated to latest stable

    // AppCompat - For backward compatibility of Material Design features
    implementation("androidx.appcompat:appcompat:1.6.1") // This is still the latest stable

    // Material Design - For Google's Material Design components (BottomNavigationView, etc.)
    implementation("com.google.android.material:material:1.12.0") // Updated to latest stable

    // ConstraintLayout - For flexible and responsive layouts
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // This is still the latest stable

    // Activity KTX - Extensions for Activity class
    implementation("androidx.activity:activity-ktx:1.9.0") // Updated to latest stable

    // Fragment KTX - Extensions for Fragment class
    implementation("androidx.fragment:fragment-ktx:1.7.0") // Updated to latest stable

    // Navigation Component - For handling app navigation (NavHostFragment, NavController)
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7") // Updated to latest stable
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")     // Updated to latest stable

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.code.gson:gson:2.10.1")
}