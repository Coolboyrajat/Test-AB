import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
    id("com.android.application")
    id(Plugins.Kotlin.serialization)
    id(Plugins.ksp)
}

android {
    namespace = "com.abdownloadmanager.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.abdownloadmanager.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = rootProject.version.toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Add necessary permissions
        manifestPlaceholders["usesCleartextTraffic"] = "true"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    signingConfigs {
        create("release") {
            // Replace with your actual keystore details
            storeFile = file("your_release_keystore.jks")
            storePassword = "your_keystore_password"
            keyAlias = "your_key_alias"
            keyPassword = "your_key_password"
        }
    }
}

dependencies {
    // Project modules
    implementation(project(":shared:app"))
    implementation(project(":shared:app-utils"))
    implementation(project(":shared:utils"))
    implementation(project(":shared:compose-utils"))
    implementation(project(":shared:resources"))
    implementation(project(":shared:config"))
    implementation(project(":downloader:core"))
    implementation(project(":downloader:monitor"))

    // Compose
    implementation(libs.compose.material.rippleEffect)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.runtime)

    // Koin for dependency injection
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.annotations)
    ksp(libs.koin.compiler)

    // Kotlin libraries
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.android)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.datetime)

    // Arrow - Functional programming
    implementation(libs.arrow.core)

    // Decompose for navigation
    implementation(libs.decompose)
    implementation(libs.decompose.jbCompose)

    // Android specific
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}