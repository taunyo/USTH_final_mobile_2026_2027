plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "vn.edu.usth.final_mobile_2026_2027"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "vn.edu.usth.final_mobile_2026_2027"
        minSdk = 37
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.volley)
    implementation(libs.glide)
    testImplementation(libs.junit)
    // The org.json of Android only works on a phone: local unit tests need the real library
    testImplementation(libs.json)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}