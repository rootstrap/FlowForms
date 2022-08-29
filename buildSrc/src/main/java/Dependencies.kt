import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {

    // Kotlin
    private const val KOTLIN_STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"
    private const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES_CORE}"

    // Android
    private const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
    private const val CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    private const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
    private const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    private const val LIFECYCLE_COMMON = "android.arch.lifecycle:common-java8:${Versions.LIFECYCLE_COMMON}"
    private const val LIFECYCLE_KAPT = "android.arch.lifecycle:compiler:${Versions.LIFECYCLE_COMMON}"
    private const val LIFECYCLE_RUNTIME = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}"
    private const val LIFECYCLE_LIVEDATA = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE}"
    private const val LIFECYCLE_EXTENSIONS = "androidx.lifecycle:lifecycle-extensions:${Versions.LIFECYCLE_EXTENSIONS}"
    private const val LIFECYCLE_VIEW_MODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE}"
    private const val GUAVA = "com.google.guava:guava:${Versions.GUAVA}"

    // Test libs
    private const val COROUTINES_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES_TEST}"
    private const val TURBINE = "app.cash.turbine:turbine:${Versions.TURBINE}"
    private const val MOCKK = "io.mockk:mockk:${Versions.MOCKK}"
    private const val J_UNIT = "junit:junit:${Versions.J_UNIT}"
    private const val EXT_J_UNIT = "androidx.test.ext:junit:${Versions.EXT_J_UNIT}"
    private const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}"

    val kotlinLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(COROUTINES_CORE)
    }

    val flowFormsAndroidLibraries = arrayListOf<String>().apply {
        add(CORE_KTX)
        add(APP_COMPAT)
        add(MATERIAL)
        add(LIFECYCLE_COMMON)
        add(LIFECYCLE_RUNTIME)
    }

    val appLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(CORE_KTX)
        add(APP_COMPAT)
        add(MATERIAL)
        add(CONSTRAINT_LAYOUT)
        add(LIFECYCLE_COMMON)
        add(LIFECYCLE_KAPT)
        add(LIFECYCLE_RUNTIME)
        add(LIFECYCLE_LIVEDATA)
        add(LIFECYCLE_EXTENSIONS)
        add(LIFECYCLE_VIEW_MODEL)
        add(GUAVA)
    }

    val commonTestLibraries = arrayListOf<String>().apply {
        add(COROUTINES_TEST)
        add(TURBINE)
        add(MOCKK)
    }

    val jvmTestLibraries = arrayListOf<String>().apply {
        add(J_UNIT)
    }

    val androidTestLibraries = arrayListOf<String>().apply {
        add(EXT_J_UNIT)
        add(ESPRESSO_CORE)
    }
}

// util functions for adding the different type dependencies from build.gradle file
fun DependencyHandler.kapt(list: List<String>) {
    list.forEach { dependency ->
        add("kapt", dependency)
    }
}

fun DependencyHandler.implementation(list: List<String>) {
    list.forEach { dependency ->
        add("implementation", dependency)
    }
}

fun DependencyHandler.androidTestImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("androidTestImplementation", dependency)
    }
}

fun DependencyHandler.testImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("testImplementation", dependency)
    }
}
