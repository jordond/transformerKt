plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.dokka)
    alias(libs.plugins.poko)
    alias(libs.plugins.publish)
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
    api(libs.media3.transformer)
    api(libs.media3.effect)
    api(libs.media3.common)

    /* Tests */
    testImplementation(libs.junit)

    /* Misc */
    dokkaPlugin(libs.dokka.android)
}