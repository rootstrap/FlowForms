plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(ExampleAppAndroidConfig.compileSdk)
    buildToolsVersion(ExampleAppAndroidConfig.buildToolsVersion)

    defaultConfig {
        applicationId = "com.rootstrap.flowforms.example"
        minSdkVersion(ExampleAppAndroidConfig.minSdk)
        targetSdkVersion(ExampleAppAndroidConfig.targetSdk)
        versionCode = ExampleAppAndroidConfig.versionCode
        versionName = ExampleAppAndroidConfig.versionName

        testInstrumentationRunner = ExampleAppAndroidConfig.androidTestInstrumentation
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile(ExampleAppAndroidConfig.defaultProguardFile),
                ExampleAppAndroidConfig.proguardRulesFile
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
