plugins {
    id(Plugins.androidApplication)
    kotlin(Plugins.kotlinAndroid)
    kotlin(Plugins.kotlinKapt)
    id(Plugins.daggerHilt)
    id(Plugins.spotless)
}

android {
    compileSdkVersion(Configs.compileSdkVersion)
    buildToolsVersion(Configs.buildToolsVersion)

    defaultConfig {
        applicationId = Configs.applicationId
        minSdkVersion(Configs.minSdkVersion)
        targetSdkVersion(Configs.targetSdkVersion)
        versionCode = Configs.versionCode
        versionName = Configs.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android.txt"))
        }
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        resources.excludes.addAll(listOf("/META-INF/AL2.0", "/META-INF/LGPL2.1"))
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        useIR = true
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
    }

    buildFeatures {
        compose = true
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("$buildDir/**/*.kt")
        targetExclude("bin/**/*.kt")
        ktlint(Versions.ktlint)
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
        licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
    }
    kotlinGradle {
        target("**/*.gradle.kts", "*.gradle.kts")
        ktlint(Versions.ktlint)
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}

dependencies {
    // Kotlin
    implementation(Libs.Kotlin.stdLib)

    // Android
    implementation(Libs.Android.coreKtx)
    implementation(Libs.Android.appcompat)
    implementation(Libs.Android.materialDesign)

    // Coroutines
    implementation(Libs.Coroutines.core)
    implementation(Libs.Coroutines.android)

    // Compose
    implementation(Libs.Compose.foundation)
    implementation(Libs.Compose.layout)
    implementation(Libs.Compose.ui)
    implementation(Libs.Compose.uiUtil)
    implementation(Libs.Compose.material)
    implementation(Libs.Compose.animation)
    implementation(Libs.Compose.iconsExtended)
    implementation(Libs.Compose.tooling)
    implementation(Libs.Compose.composeLifecycle)
    implementation(Libs.Activity.activityCompose)
    implementation(Libs.ConstraintLayout.constraintLayoutCompose)

    // Hilt + Dagger
    implementation(Libs.Dagger.hiltAndroid)
    implementation(Libs.Dagger.hiltViewModel)
    implementation(Libs.Dagger.hiltNavigation)
    kapt(Libs.Dagger.daggerCompiler)
    kapt(Libs.Dagger.hiltCompiler)

    // Architecture Components
    implementation(Libs.Lifecycle.viewModelCompose)
    implementation(Libs.Lifecycle.liveData)
    implementation(Libs.Lifecycle.runtime)
    implementation(Libs.Lifecycle.process)
    implementation(Libs.Navigation.compose)

    // EventBus
    implementation(Libs.EventBus.eventBus)

    // Accompanist
    implementation(Libs.Accompanist.coil)
    implementation(Libs.Accompanist.insets)
}
