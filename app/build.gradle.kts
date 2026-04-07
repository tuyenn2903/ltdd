// 1. Khối Plugins: Phải luôn nằm ở đầu file
lateinit var jvmTarget: String

plugins {
    id("com.android.application")
    // Thay vì dùng id trực tiếp cho compose, chúng ta để nó đi kèm với kotlin android
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")

    // Thêm dòng này nếu bạn dùng Kotlin 2.0+, đây là cách mới để kích hoạt Compose Compiler
    id("org.jetbrains.kotlin.plugin.compose")
}

// 2. Khối Android: Giữ nguyên như cũ nhưng đảm bảo không có lỗi cú pháp
android {
    namespace = "com.example.usermanagerapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.usermanagerapp"
        minSdk = 26
        //noinspection EditedTargetSdkVersion
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    // Các thư viện Firebase cần thiết
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Các thư viện Compose cơ bản
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Coil để hiển thị ảnh
    implementation("io.coil-kt:coil-compose:2.6.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    implementation("io.coil-kt:coil-compose:2.4.0")
}