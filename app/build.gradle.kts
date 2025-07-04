import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FileInputStream
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.1.10"

    //apollo
    id("com.apollographql.apollo") version "4.3.1"

}

android {
    namespace = "com.example.m_commerce_admin"
    compileSdk = 35

    val file = rootProject.file("local.properties")
    val properties = Properties()
    properties.load(FileInputStream(file))

    val localProperties = Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }
    val adminToken: String = localProperties.getProperty("ADMIN_TOKEN", "")
    val shopDomain: String = localProperties.getProperty("SHOP_DOMAIN", "")
    val shopDomainRest: String = localProperties.getProperty("SHOP_DOMAIN_REST", "")
    val locationID: String = localProperties.getProperty("LOCATION_ID", "82774655225")


    defaultConfig {
        applicationId = "com.example.m_commerce_admin"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "adminToken", properties.getProperty("ADMIN_TOKEN"))
        buildConfigField("String", "shopDomain", shopDomain)
        buildConfigField("String", "locationID", locationID)
        buildConfigField("String", "shopDomainRest", shopDomainRest)
        resValue("string", "ADMIN_TOKEN", adminToken)


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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.espresso.core)
    testImplementation(libs.junit.jupiter)
    val room_version = "2.6.1"
    val nav_version = "2.8.8"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //!Database
    //*Room
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    //?==================================================

    //!Network

    //*Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //*Shopify
    implementation("com.shopify.mobilebuysdk:buy3:2025.4.0")

    //?==================================================

    //!UI
    //*Coil
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
    implementation("io.coil-kt.coil3:coil-svg:3.0.4")

    //?==================================================

    //!Dependency Injection
    //*hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")

    //!Navigation
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")


    // extended icons
    implementation("androidx.compose.material:material-icons-extended:1.6.1")

    // view model
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    //dataStore
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    //APOLLO
    implementation("com.apollographql.apollo:apollo-runtime:4.3.1")

    //JSON parsing for Shopify API
    implementation("org.json:json:20231013")

    //lotti

    implementation("com.airbnb.android:lottie-compose:6.3.0")


// JUnit + Coroutine Test
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")

// Robolectric
    testImplementation("org.robolectric:robolectric:4.11.1")

// AndroidX Test
    testImplementation("androidx.test.ext:junit-ktx:1.1.3")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")

// Mockk
    testImplementation("io.mockk:mockk-android:1.13.17")
    testImplementation("io.mockk:mockk-agent:1.13.17")

// Hamcrest
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")





    apollo {
        service("service") {
            packageName.set("com.example.m_commerce_admin")
        }
    }
}

