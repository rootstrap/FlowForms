plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = (ExampleAppAndroidConfig.COMPILE_SDK)
    buildToolsVersion = (ExampleAppAndroidConfig.BUILD_TOOLS_VERSION)

    defaultConfig {
        applicationId = "com.rootstrap.flowforms.example"
        minSdk = (ExampleAppAndroidConfig.MIN_SDK)
        targetSdk = (ExampleAppAndroidConfig.TARGET_SDK)
        versionCode = 1
        versionName = "1.3.0"

        testInstrumentationRunner = ExampleAppAndroidConfig.ANDROID_TEST_INSTRUMENTATION_RUNNER
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile(ExampleAppAndroidConfig.DEFAULT_PROGUARD_FILE),
                ExampleAppAndroidConfig.PROGUARD_RULES_FILE
            )
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        resources.excludes += "DebugProbesKt.bin"
    }
}

dependencies {
    implementation(project(":FlowForms-Core"))
    //std lib
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    //app libs
    implementation(Dependencies.appLibraries)
    //test libs
    testImplementation(Dependencies.jvmTestLibraries)
    androidTestImplementation(Dependencies.androidTestLibraries)
    implementation("com.google.guava:guava:31.1-jre")
}
