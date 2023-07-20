// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.GRADLE}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        gradlePluginPortal()
    }
}

tasks {
    register<Copy>("copyGitHooks") {
        description = "Copies the git hooks from root to the .git folder."
        from("$rootDir") {
            include("pre-commit")
          //  rename("(.*).sh", "$1")
        }
        into("$rootDir/.git/hooks")
        eachFile {
            fileMode = 0b111101101
        }
    }

    register<Delete>("deletePreviousGitHooks") {
        description = "Deleting previous gitHook."

        val preCommit = "${rootProject.rootDir}/.git/hooks/pre-commit"
        if (file(preCommit).exists()) {
            delete(preCommit)
        }
    }
}

project.tasks.getByPath("copyGitHooks").dependsOn(tasks.named("deletePreviousGitHooks"))
project.tasks.getByPath(":FlowForms-Core:preBuild").dependsOn(tasks.named("copyGitHooks"))

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
