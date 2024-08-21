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
            setUrl("https://storage.zego.im/maven")   // <- Add this line.
        }
        maven {
            setUrl("https://www.jitpack.io")          // <- Add this line.
        }
    }
}

rootProject.name = "ChatVista"
include(":app")
