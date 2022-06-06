plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = "com.github.rootstrap"
version = "0.0.1-SNAPSHOT"

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
