plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)

    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.easycrypto"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.easycrypto"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://rest.coincap.io/v3/\"")
            buildConfigField("String", "API_KEY", "\"bf98ee802ae134a27a91e141308adc76b0b1be2a1723230720d176089e4aa75c\"")
            // constant that might change for the build type we're building for
            // very often we've different api for debug version, but real production api for release build
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "BASE_URL", "\"https://rest.coincap.io/v3/\"")
            buildConfigField("String", "API_KEY", "\"bf98ee802ae134a27a91e141308adc76b0b1be2a1723230720d176089e4aa75c\"")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true // must for using buildConfigField
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.bundles.koin)

    implementation(libs.bundles.ktor)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    implementation("androidx.navigation:navigation-compose:2.7.7")

    // appwrite
    implementation("io.appwrite:sdk-for-android:8.1.0")

    implementation ("androidx.compose.foundation:foundation:1.8.3")
    implementation("androidx.datastore:datastore-preferences:1.1.7")



}