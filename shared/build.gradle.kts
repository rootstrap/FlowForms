plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

fun flowFormsCoreProject() = project(":FlowForms-Core")

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries {
            framework {
                export(flowFormsCoreProject())
                baseName = "shared"
            }
            sharedLib {
                export(flowFormsCoreProject())
            }
        }

    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(flowFormsCoreProject())
                implementations(Dependencies.kotlinLibraries)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementations(Dependencies.appLibraries)
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = "com.rootstrap.flowforms.shared"
    compileSdk = FlowFormsAndroidConfig.COMPILE_SDK
    defaultConfig {
        minSdk = FlowFormsAndroidConfig.MIN_SDK
        targetSdk = FlowFormsAndroidConfig.TARGET_SDK
    }
}

// utility functions

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.implementations(list : List<String>) {
    list.forEach {
        implementation(it)
    }
}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}
