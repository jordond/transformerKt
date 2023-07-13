plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinKsp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.dependencies)
    alias(libs.plugins.binaryCompatibility)
}

apiValidation {
    ignoredProjects.add("demo")
}