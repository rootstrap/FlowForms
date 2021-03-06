plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlinx.kover") version "0.5.1"
    `maven-publish`
}

group = "com.rootstrap"
version = "0.0.1"

kotlin {
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
    }
}

val rootPkg = "com.rootstrap.flowforms"

tasks.koverMergedHtmlReport {
    excludes = listOf("${rootPkg}.core.common.StatusCodes","${rootPkg}.util.*")
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
