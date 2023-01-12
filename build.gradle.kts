// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
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
    }
}

tasks {
   /* register<Copy>("copyGitHooks") {
        description = "Copies the git hooks from root to the .git folder."
        from("$rootDir") {*/
           // include("**/*.sh")
         //   rename("(.*).sh", "$1")
     //   }
      //  into("$rootDir/.git/hooks")
  //  }
    register<Copy>("copyGitHooks") {
        description = "Copies the git hooks from root to the .git folder."
        from("$rootDir") {
            include("pre-commit")
          //  rename("(.*).sh", "$1")
        }
        into("$rootDir/.git/hooks")
    }

   /* register<Exec>("installGitHooks") {
        description = "Installs the pre-commit git hooks from root."
      //  group = BuildTaskGroups.GIT_HOOKS
        workingDir(rootDir)
        commandLine("chmod")
        args("-R", "+x", ".git/hooks/")
        dependsOn(named("copyGitHooks"))
      /*  onlyIf {
            isLinuxOrMacOs()
        }*/
        doLast {
            logger.info("Git hooks installed successfully.")
        }
    }*/

  /*  register<Delete>("deleteGitHooks") {
        description = "Delete the pre-commit git hooks."
     //   group = BuildTaskGroups.GIT_HOOKS
        delete(fileTree(".git/hooks/"))
    }*/

   /* afterEvaluate {
        tasks["clean"].dependsOn(tasks.named("copyGitHooks"))
    }*/
}

/*task installGitHook(type: Copy) {
    from new File(rootProject.rootDir, 'pre-commit')
    into { new File(rootProject.rootDir, '.git/hooks') }
    fileMode 0777
}*/

project.tasks.getByPath(":FlowForms-Core:preBuild").dependsOn(tasks.named("copyGitHooks"))
//tasks[":app:preBuild"].dependsOn(tasks.named("copyGitHooks"))

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
