
//FlowForms Android config constants
object FlowFormsAndroidConfig {
    const val compileSdk = 31
    const val minSdk = 23
    const val targetSdk = 31
    const val buildToolsVersion = "30.0.3"
    const val versionCode = 1
    const val versionName = "1.0.0"

    const val androidTestInstrumentation = "androidx.test.runner.AndroidJUnitRunner"
    const val proguardConsumerRules =  "consumer-rules.pro"
    const val defaultProguardFile = "proguard-android-optimize.txt"
    const val proguardRulesFile = "proguard-rules.pro"
}

// Android ExampleApp config constants
object ExampleAppAndroidConfig {
    const val compileSdk = FlowFormsAndroidConfig.compileSdk
    const val minSdk = FlowFormsAndroidConfig.minSdk
    const val targetSdk = FlowFormsAndroidConfig.targetSdk
    const val buildToolsVersion = FlowFormsAndroidConfig.buildToolsVersion
    const val versionCode = 1
    const val versionName = "1.0.0"

    const val androidTestInstrumentation = FlowFormsAndroidConfig.androidTestInstrumentation
    const val proguardConsumerRules =  FlowFormsAndroidConfig.proguardConsumerRules
    const val defaultProguardFile = FlowFormsAndroidConfig.defaultProguardFile
    const val proguardRulesFile = FlowFormsAndroidConfig.proguardRulesFile
}

