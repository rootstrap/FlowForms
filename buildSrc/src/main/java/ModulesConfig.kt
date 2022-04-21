
//FlowForms Android config constants
object FlowFormsAndroidConfig {
    const val COMPILE_SDK = 31
    const val MIN_SDK = 23
    const val TARGET_SDK = 31
    const val BUILD_TOOLS_VERSION = "30.0.3"

    const val ANDROID_TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
    const val PROGUARD_CONSUMER_RULES =  "consumer-rules.pro"
    const val DEFAULT_PROGUARD_FILE = "proguard-android-optimize.txt"
    const val PROGUARD_RULES_FILE = "proguard-rules.pro"
}

// Android ExampleApp config constants
object ExampleAppAndroidConfig {
    const val COMPILE_SDK = FlowFormsAndroidConfig.COMPILE_SDK
    const val MIN_SDK = FlowFormsAndroidConfig.MIN_SDK
    const val TARGET_SDK = FlowFormsAndroidConfig.TARGET_SDK
    const val BUILD_TOOLS_VERSION = FlowFormsAndroidConfig.BUILD_TOOLS_VERSION

    const val ANDROID_TEST_INSTRUMENTATION_RUNNER = FlowFormsAndroidConfig.ANDROID_TEST_INSTRUMENTATION_RUNNER
    const val PROGUARD_CONSUMER_RULES =  FlowFormsAndroidConfig.PROGUARD_CONSUMER_RULES
    const val DEFAULT_PROGUARD_FILE = FlowFormsAndroidConfig.DEFAULT_PROGUARD_FILE
    const val PROGUARD_RULES_FILE = FlowFormsAndroidConfig.PROGUARD_RULES_FILE
}

