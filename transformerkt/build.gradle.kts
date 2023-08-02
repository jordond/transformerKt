plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.dokka)
    alias(libs.plugins.poko)
    id("maven-publish")
}

android {
    namespace = "dev.transformerkt"
    compileSdk = libs.versions.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xexplicit-api=strict"
        jvmTarget = "17"
    }
}

dependencies {

    /* Android */
    implementation(libs.core.ktx)
    implementation(libs.appcompat)

    /* Kotlin */
    api(libs.kotlinx.coroutines.android)

    /* Media3 Transformer */
//    api(libs.media3.transformer)
//    api(libs.media3.effect)
//    api(libs.media3.common)
    api(project(":media3-lib-transformer"))
    api(project(":media3-lib-effect"))
    api(project(":media3-lib-common"))

    /* Tests */
    testImplementation(libs.junit)

    /* Misc */
    dokkaPlugin(libs.dokka.android)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "dev.transformerkt"
            artifactId = "transformerkt"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}