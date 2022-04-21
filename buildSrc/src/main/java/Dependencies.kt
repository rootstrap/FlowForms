import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {
    // Kotlin
    private const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"
    private const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES_CORE}"

    // Android ui

    private const val appcompat = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
    private const val coreKtx = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    private const val material = "com.google.android.material:material:${Versions.MATERIAL}"
    private const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"

    // Test libs

    private const val junit = "junit:junit:${Versions.J_UNIT}"
    private const val extJUnit = "androidx.test.ext:junit:${Versions.EXT_J_UNIT}"
    private const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}"

    val kotlinLibraries = arrayListOf<String>().apply {
        add(kotlinStdLib)
        add(coroutinesCore)
    }

    val flowFormsAndroidLibraries = arrayListOf<String>().apply {
        add(coreKtx)
        add(appcompat)
        add(material)
    }

    val appLibraries = arrayListOf<String>().apply {
        add(kotlinStdLib)
        add(coreKtx)
        add(appcompat)
        add(material)
        add(constraintLayout)
    }

    val androidTestLibraries = arrayListOf<String>().apply {
        add(extJUnit)
        add(espressoCore)
    }

    val testLibraries = arrayListOf<String>().apply {
        add(junit)
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
