@file:Suppress("UnstableApiUsage")

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
    }
}

rootProject.name = "TransformerKt"
include(":demo")
include(":transformerkt")

gradle.extra.set("androidxMediaModulePrefix", "media3-")
apply(from = file("./media3/core_settings.gradle"))