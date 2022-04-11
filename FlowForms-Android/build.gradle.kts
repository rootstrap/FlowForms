plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdkVersion(FlowFormsAndroidConfig.compileSdk)
    buildToolsVersion(FlowFormsAndroidConfig.buildToolsVersion)

    defaultConfig {
        minSdkVersion(FlowFormsAndroidConfig.minSdk)
        targetSdkVersion(FlowFormsAndroidConfig.targetSdk)
        versionCode = FlowFormsAndroidConfig.versionCode
        versionName = FlowFormsAndroidConfig.versionName

        testInstrumentationRunner = FlowFormsAndroidConfig.androidTestInstrumentation
        consumerProguardFile(FlowFormsAndroidConfig.proguardConsumerRules)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile(FlowFormsAndroidConfig.defaultProguardFile),
                FlowFormsAndroidConfig.proguardRulesFile
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
