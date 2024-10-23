plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.araneta.mood_tracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.araneta.mood_tracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation ("org.tensorflow:tensorflow-lite:2.14.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("org.tensorflow:tensorflow-lite-task-text:0.3.0")
    implementation ("com.android.volley:volley:1.2.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}