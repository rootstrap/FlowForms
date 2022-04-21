plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(ExampleAppAndroidConfig.COMPILE_SDK)
    buildToolsVersion(ExampleAppAndroidConfig.BUILD_TOOLS_VERSION)

    defaultConfig {
        applicationId = "com.rootstrap.flowforms.example"
        minSdkVersion(ExampleAppAndroidConfig.MIN_SDK)
        targetSdkVersion(ExampleAppAndroidConfig.TARGET_SDK)
        versionCode = 1
        versionName = "0.0.1"

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
    //std lib
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    //app libs
    implementation(Dependencies.appLibraries)
    //test libs
    testImplementation(Dependencies.testLibraries)
    androidTestImplementation(Dependencies.androidTestLibraries)
}
