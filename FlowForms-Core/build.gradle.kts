plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("com.android.library")
    `maven-publish`
}

group = "com.rootstrap"
version = "1.4.1"

kotlin {
    android {
        publishLibraryVariants("release")
    }
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "FlowForms-Core"
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }
        val commonMain by getting {
            dependencies {
                implementations(Dependencies.kotlinLibraries)
            }
        }
        val commonTest by getting
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementations(Dependencies.commonTestLibraries)
            }
        }
        val androidMain by getting {
            dependencies {
                implementations(Dependencies.flowFormsAndroidLibraries)
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

val rootPkg = "com.rootstrap.flowforms"

kover {
    isDisabled.set(false)
    engine.set(kotlinx.kover.api.DefaultJacocoEngine)
}

koverMerged {
    enable()

    xmlReport {
        //onCheck.set(false)
        reportFile.set(layout.buildDirectory.file("$buildDir/reports/kover/result.xml"))
    }
    htmlReport {
       // onCheck.set(false)
        reportDir.set(layout.buildDirectory.dir("$buildDir/reports/kover/html-result"))
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
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

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}
