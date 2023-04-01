plugins {
    id("kotlin-kapt")
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
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
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_16.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    val platform = platform(libs.bom.compose)
    implementation(platform)
    androidTestImplementation(platform)

    implementation(libs.androidx.core)
    implementation(libs.androidx.material)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.preview)
    implementation(libs.compose.navigation.core)
    implementation(libs.compose.navigation.hilt)
    implementation(libs.compose.activity)
    implementation(libs.coil)
    implementation(libs.coil.compose)

    testImplementation(libs.androidx.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.compose.test.uiJunit4)

    implementation(libs.hilt.android)
    kapt(libs.hilt.androidCompiler)

    debugImplementation(libs.compose.test.uiManifest)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.compose.preview)
}