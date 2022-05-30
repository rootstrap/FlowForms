plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = (FlowFormsAndroidConfig.COMPILE_SDK)
    buildToolsVersion = (FlowFormsAndroidConfig.BUILD_TOOLS_VERSION)

    defaultConfig {
        minSdk = (FlowFormsAndroidConfig.MIN_SDK)
        targetSdk = (FlowFormsAndroidConfig.TARGET_SDK)
        version = "0.0.1-SNAPSHOT"

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
    testImplementation(Dependencies.jvmTestLibraries)

}
