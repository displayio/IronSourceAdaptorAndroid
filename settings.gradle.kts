pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.display.io/")
        }
        maven {
            url = uri("https://android-sdk.is.com/")
        }
    }
}

rootProject.name = "IronSource_Android"
include(":app")
 