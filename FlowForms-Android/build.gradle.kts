plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdkVersion(FlowFormsAndroidConfig.COMPILE_SDK)
    buildToolsVersion(FlowFormsAndroidConfig.BUILD_TOOLS_VERSION)

    defaultConfig {
        minSdkVersion(FlowFormsAndroidConfig.MIN_SDK)
        targetSdkVersion(FlowFormsAndroidConfig.TARGET_SDK)
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = FlowFormsAndroidConfig.ANDROID_TEST_INSTRUMENTATION_RUNNER
        consumerProguardFile(FlowFormsAndroidConfig.PROGUARD_CONSUMER_RULES)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile(FlowFormsAndroidConfig.DEFAULT_PROGUARD_FILE),
                FlowFormsAndroidConfig.PROGUARD_RULES_FILE
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
    //kotlin libs
    implementation(Dependencies.kotlinLibraries)
    //android libs
    implementation(Dependencies.flowFormsAndroidLibraries)
    //test libs
    testImplementation(Dependencies.testLibraries)

}
