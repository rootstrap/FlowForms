plugins {
    kotlin("multiplatform")
    id("kotlin-kapt")
}

group = "com.rootstrap.flowforms.core"
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
                implementation(Dependencies.KOTLIN_STD_LIB)
                implementation(Dependencies.COROUTINES_CORE)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
