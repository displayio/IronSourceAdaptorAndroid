import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("com.android.library")
}

version = "5.0.0"

android {
    namespace = "com.ironsource.adapters.custom.dio"
    compileSdk = 34

    defaultConfig {
        minSdk = 16

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.brandio.ads:sdk:5.0.0")
    implementation ("com.ironsource.sdk:mediationsdk:7.9.0")
    implementation ("com.ironsource:adqualitysdk:7.17.0")
}

//   ./gradlew makeAar
tasks.register<Copy>("makeAar") {
    mustRunAfter("clearAar")
    mustRunAfter("clearBuildFolder")
    dependsOn("clearAar")
    dependsOn("clearBuildFolder")
    dependsOn("build")

    from("build/outputs/aar/adaptorIronSource-release.aar")
    into(layout.projectDirectory.dir("outputs"))
    rename("adaptorIronSource-release.aar", "adapter-ironsource-${version}.aar")
    doLast {
        println("Aar build success")
    }
}

tasks.register<Delete>("clearAar") {
    delete(fileTree("outputs").matching {
        include("**/*.aar")
    })
}

tasks.register<Delete>("clearBuildFolder") {
    dependsOn("clean")
}
