plugins {
    id("com.android.application")
}
android {
    namespace = "com.example.gboardmirrorpassthrough"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.gboardmirrorpassthrough"
        minSdk = 29
        targetSdk = 32
        versionCode = 103
        versionName = "1.0.0-modern-api102-rc3"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        release { isMinifyEnabled = false }
    }
}
dependencies {
    compileOnly("io.github.libxposed:api:102.0.0")
}
