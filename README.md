# FlowForms

[![](https://jitpack.io/v/rootstrap/FlowForms.svg)](https://jitpack.io/#rootstrap/FlowForms) [![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](code_of_conduct.md) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)


WIP badges :
- ![Build Status](https://github.com/rootstrap/FlowForms/workflows/CI/badge.svg)
- [![Maintainability](https://api.codeclimate.com/v1/badges/FlowForms/maintainability)](https://codeclimate.com/github/rootstrap/FlowForms/maintainability)
- [![Test Coverage](https://api.codeclimate.com/v1/badges/FlowForms/test_coverage)](https://codeclimate.com/github/rootstrap/FlowForms/test_coverage)

KMP library for form management

---

#### :warning: Considerations :warning:
- This project works on top of [KMP](https://kotlinlang.org/docs/multiplatform.html) (currently in alpha) and `ExperimentalCoroutinesApi`, so use it with caution.

---

## Installation (using gradle)
- Add the JitPack repository to your root build.gradle file, at the end of repositories :
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

- Add FlowForms dependency :
```
dependencies {
  ...
  implementation("com.github.rootstrap:FlowForms:feature~core-publishing-SNAPSHOT")
  ...
}
```

- Only FlowForms Core is available at the moment. It's a kotlin only library so you can use it whenever you use Kotlin.

---

## Usage
- WIP 🚧

---

## Contributing
Bug reports (please use Issues) and pull requests are welcome on GitHub at https://github.com/rootstrap/FlowForms. This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

For more information check the [official notion page](https://www.notion.so/rootstrap/FlowForms-KMP-library-for-form-management-starting-with-Android-43ee69a08a17450a89cf8db695ec1bd9), where you can see the project's goals, architecture and desired usability.

### Prerequisites
- At the moment, the project can only be ran inside Android Studio due to Intellij's lack of support for the Android Gradle Plugin (AGP) 7.+. A GIT issue will be created to reduce the AGP version to the latest supported by the IntelliJ IDEA IDE. As MPP projects are intended to be ran using Intellij IDEA.

### Unit testing

We expect to have at least 90% of the code unit tested (with the ideal goal of 100%) in all modules except the Example apps. So please ensure to make unit tests on new code and keep all of them working.

#### FlowForms Core
As the current implementation is only a jvm one, to run the tests on the module **FlowForms Core** we need to write the following command in a Terminal at the project's root folder (or directly in the IDE's Terminal)
- `./gradlew FlowForms-Core:jvmTest`

**FlowForms Core** uses [Coroutines-test](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/README.md), [Mockk](https://github.com/mockk/mockk), and [Turbine](https://github.com/cashapp/turbine) for testing the common kotlin module.

#### FlowForms Android Ext
WIP 🚧

---

## License
The library is available as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).

## Credits
**FlowForms** is maintained by [Rootstrap](http://www.rootstrap.com) with the help of our [contributors](https://github.com/rootstrap/FlowForms/contributors).

[<img src="https://s3-us-west-1.amazonaws.com/rootstrap.com/img/rs.png" width="100"/>](http://www.rootstrap.com)
