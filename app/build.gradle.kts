import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.effort.recycrew"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.effort.recycrew"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // properties 선언 후 local.properties 파일을 읽어온다
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField(
            "String",
            "GOOGLE_SIGN_IN_WEB_CLIENT_ID",
            properties["GOOGLE_SIGN_IN_WEB_CLIENT_ID"].toString()
        )

        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            properties["KAKAO_NATIVE_APP_KEY"].toString()
        )

        addManifestPlaceholders(mapOf("NAVER_MAP_CLIENT_ID" to properties.getProperty("NAVER_MAP_CLIENT_ID")))
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.common.ktx)

    // auth
    implementation(libs.play.services.auth)

    // KakaoShare
    implementation(libs.kakao.share)

    implementation(project(":feature"))
    implementation(project(":presentation"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":local"))
    implementation(project(":remote"))
}
