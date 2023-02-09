plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("com.android.library")
    `maven-publish`
}

group = "com.rootstrap"
version = "1.0.0"

kotlin {
    android {
        publishLibraryVariants("release")
    }
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementations(Dependencies.kotlinLibraries)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementations(Dependencies.commonTestLibraries)
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val androidMain by getting {
            dependencies {
                implementations(Dependencies.flowFormsAndroidLibraries)
            }
        }
        val androidTest by getting
    }
}

val rootPkg = "com.rootstrap.flowforms"

kover {
    isDisabled.set(false)
    engine.set(kotlinx.kover.api.DefaultJacocoEngine)
}

koverMerged {
    enable()
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
        targetSdk = 32
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

// utility functions

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.implementations(list : List<String>) {
    list.forEach {
        implementation(it)
    }
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
    }
}
