plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.exampleappandroidcompose"
    compileSdk = ExampleAppAndroidConfig.COMPILE_SDK

    defaultConfig {
        applicationId = "com.example.exampleappandroidcompose"
        minSdk = ExampleAppAndroidConfig.MIN_SDK
        targetSdk = ExampleAppAndroidConfig.TARGET_SDK
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(Dependencies.appComposeLibraries)
    implementation(platform(Dependencies.COMPOSE_BOM))
    debugImplementation(Dependencies.COMPOSE_TOOLING)
    debugImplementation(Dependencies.COMPOSE_MANIFEST)
}
