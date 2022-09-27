---
layout: default
title: Flow Forms, Declarative and reactive form management library
---

<div class="rs-row center-main-axis"> 
  <div class="rs-column center-second-axis"> 
      <a href="http://www.rootstrap.com"> <img src="https://s3-us-west-1.amazonaws.com/rootstrap.com/img/rs.png" width="100"/> </a>
      <h1 class="index-title"> FlowForms </h1>
      <div class="rs-row center-main-axis badges-container"> 
        <a href="https://jitpack.io/#rootstrap/FlowForms"> <img src="https://jitpack.io/v/rootstrap/FlowForms.svg" /> </a> 
        <a href="https://www.contributor-covenant.org/version/2/1/code_of_conduct/"> <img src="https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg" /> </a>
        <a href="https://opensource.org/licenses/MIT"> <img src="https://img.shields.io/badge/License-MIT-yellow.svg" /> </a>
      </div>
      <h3> KMP library for form management </h3>
  </div>
</div>

## What is FlowForms?
FlowForms is a declarative and reactive Kotlin multiplatform library for Form management

## Why?
It aims to reduce all the boiler plate needed to work with application forms by allowing the developer to directly declare the form and its fields with their respective validations _(being them synchronous or asynchronous)_, while also exposing a simple yet powerful API to react to the form and field status changes

## Sounds good, how can I get it?
Add the JitPack repository to your root build.gradle file, at the end of repositories :
<pre><code class="kotlin">
allprojects {
  repositories {
    ..
    maven { url 'https://jitpack.io' }
  }
}
</code></pre>

Add FlowForms dependency in your module's build.gradle file :
<pre><code class="kotlin">
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
</code></pre>

## Excellent! Lets get started
To start creating forms at lighting speed please refer to one of our quickstart guides below :
 - [Android quickStart guide](pages/android-quickstart)
 - [Kotlin Multi-Platform quickstart guide](pages/kmp-quickstart)

For additional features and advanced use cases please refer to our Documentation index
 - [documentation index](pages/documentation-index)