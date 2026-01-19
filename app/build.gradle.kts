plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.faltenreich.camaps"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.faltenreich.camaps"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "2.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    testImplementation(libs.junit)

    implementation(platform(libs.compose))
    debugImplementation(libs.compose.tooling)
    implementation(libs.material3)
    implementation(libs.material.icons)
    implementation(libs.navigation)

    implementation(kotlin("reflect"))

    implementation(libs.ktor.auth)
    implementation(libs.ktor.contentnegotiation)
    implementation(libs.ktor.core)
    implementation(libs.ktor.json)
    implementation(libs.ktor.okhttp)
}