plugins {
    id("kotlin-kapt")
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
//    kotlin("plugin.serialization") version "1.8.10"
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "dev.marcocattaneo.androidcomposetemplate"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
//        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "dev.kietyo.scrap"
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.compose.navigation.ui.ktx)
    val platform = platform(libs.bom.compose)
    implementation(platform)
    androidTestImplementation(platform)

    implementation(libs.androidx.core)
    implementation(libs.androidx.material)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)fs
    implementation(libs.compose.preview)
    implementation(libs.compose.navigation.core)
    implementation(libs.compose.navigation.hilt)
    implementation(libs.compose.activity)
    implementation(libs.compose.lifecycle.viewmodel)
    implementation(libs.coil)
    implementation(libs.coil.compose)

    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.androidx.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.compose.test.uiJunit4)

    implementation(libs.hilt.android)
    kapt(libs.hilt.androidCompiler)

    debugImplementation(libs.compose.test.uiManifest)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.compose.preview)
}