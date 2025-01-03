import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.effort.remote"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // properties 선언 후 local.properties 파일을 읽어온다
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        //buildConfigField("String", "NAVER_CLIENT_ID", properties["NAVER_CLIENT_ID"].toString())
        //buildConfigField("String", "NAVER_CLIENT_SECRET", properties["NAVER_CLIENT_SECRET"].toString())
        buildConfigField("String", "KAKAO_API_KEY", properties["KAKAO_API_KEY"].toString())
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)

    // Serialize
    implementation(libs.kotlin.serialization.json)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlin.serialization.converter)

    // OkHttp
    implementation(platform(libs.okhttp.bom))
    implementation (libs.okhttp.logging.interceptor)

    //Location
    implementation(libs.play.services.location)

    implementation(project(":data"))
}