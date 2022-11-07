![FlowForms Logo](https://github.com/rootstrap/FlowForms/blob/pages/docs/images/logotype-FlowForms-small-background.png?raw=true)

[![](https://jitpack.io/v/rootstrap/FlowForms.svg)](https://jitpack.io/#rootstrap/FlowForms) [![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](code_of_conduct.md) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![Maintained : Yes!](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/rootstrap/FlowForms/graphs/commit-activity) [![Documentation](https://readthedocs.org/projects/ansicolortags/badge/?version=latest)](https://rootstrap.github.io/FlowForms/) ![Build Status](https://github.com/rootstrap/FlowForms/actions/workflows/gradle.yml/badge.svg)

### KMP library for form management

---

## What is FlowForms?
FlowForms is a declarative and reactive Kotlin multiplatform library for Form management

```kotlin
class SignUpViewModel {

    val formModel = SignUpFormModel()

    val form = flowForm {
        field(NAME, Required { formModel.name })
        field(EMAIL,
            Required { formModel.email },
            BasicEmailFormat { formModel.email },
            EmailDoesNotExistsInRemoteStorage(async = true) { formModel.email }
        )
        field(NEW_PASSWORD,
            Required { formModel.newPassword },
            MinLength(MIN_PASSWORD_LENGTH) { formModel.newPassword }
        )
        field(CONFIRM_PASSWORD,
            Required { formModel.confirmPassword },
            MinLength(MIN_PASSWORD_LENGTH) { formModel.confirmPassword }
        ) {
            onBlur(Match { formModel.newPassword to formModel.confirmPassword })
        }
        field(CONFIRMATION, RequiredTrue { formModel.confirm.value })
        dispatcher = Dispatchers.IO // your async dispatcher of preference, this one is from Android
    }

    companion object {
        const val NAME = "name"
        const val EMAIL = "email"
        const val CONFIRMATION = "confirmation"
        const val NEW_PASSWORD = "new_password"
        const val CONFIRM_PASSWORD = "confirm_password"
        const val MIN_PASSWORD_LENGTH = 6
    }
}
```

It aims to reduce all the boiler plate needed to work with application forms by allowing the developer to directly declare the form and its fields with their respective validations, allowing to mix both synchronous and asynchronous validations quickly and easily, while also exposing a simple yet powerful API to react to the form and its field status changes under different circumstances.

For example, in the above snippet we are declaring the whole sign up form behavior, and now we only need to care about connecting it with our UI, which may  vary per platform and is explained in the "[Excellent! Lets get started](excellent-lets-get-started)" section.

## Sounds good, how can I get it?
Add the JitPack repository to your root build.gradle file, at the end of repositories :
```kotlin
allprojects {
  repositories {
    ..
    maven { url 'https://jitpack.io' }
  }
}
```

Based on your project, add FlowForms dependency in your module's build.gradle file :
```kotlin
dependencies {
  ..
  val flowFormsVersion = "0.0.3"
    
  // On KMP projects
  implementation("com.github.rootstrap.FlowForms:FlowForms-Core:$flowFormsVersion")

  // On android projects :
  implementation("com.github.rootstrap.FlowForms:FlowForms-Core-android:$flowFormsVersion")

  // On JVM projects :
  implementation("com.github.rootstrap.FlowForms:FlowForms-Core-jvm:$flowFormsVersion")
  ..
}
```

## Excellent! Lets get started
To start creating forms at lightning speed please refer to one of our quickstart guides below :
 - [Android quickstart guide](https://rootstrap.github.io/FlowForms/pages/android-quickstart)
 - [Kotlin Multi-Platform quickstart guide](https://rootstrap.github.io/FlowForms/pages/kmp-quickstart)

For additional features and advanced use cases please refer to our Documentation index
 - [documentation index](https://rootstrap.github.io/FlowForms/pages/documentation-index)

---

## Contributing
Bug reports (please use Issues) and pull requests are welcome on GitHub at https://github.com/rootstrap/FlowForms. This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

### Prerequisites
- At the moment, the project can only be ran inside Android Studio due to Intellij's lack of support for the Android Gradle Plugin (AGP) 7.+. You can follow [this GIT issue](https://github.com/rootstrap/FlowForms/issues/9) to know when we will add IntelliJ IDEA IDE support (you can collaborate too 😉), As MPP projects are intended to be ran using Intellij IDEA.

### Unit testing

We expect to have at least 90% of the code unit tested (with the ideal goal of 100%) in all modules except the Example apps. So please ensure to make unit tests on new code and keep all of them working.

#### FlowForms Core
To run the tests on the module **FlowForms Core** we need to write the following command in a Terminal at the project's root folder (or directly in the IDE's Terminal)
- `./gradlew FlowForms-Core:check`
The above command executes all the configured tests in the FlowForms-Core module while also generating coverage reports.

**FlowForms Core** uses [Kover](https://github.com/Kotlin/kotlinx-kover) for code coverage and [Coroutines-test](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/README.md), [Mockk](https://github.com/mockk/mockk), and [Turbine](https://github.com/cashapp/turbine) for testing the common kotlin module.

---

## License
The library is available as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).

## Credits
**FlowForms** is maintained by [Rootstrap](http://www.rootstrap.com) with the help of our [contributors](https://github.com/rootstrap/FlowForms/contributors).

[<img src="https://s3-us-west-1.amazonaws.com/rootstrap.com/img/rs.png" width="100"/>](http://www.rootstrap.com)
