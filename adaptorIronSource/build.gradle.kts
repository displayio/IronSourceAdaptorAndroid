import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("com.android.library")
}

version = "5.7.1"

android {
    namespace = "com.ironsource.adapters.custom.dio"
    compileSdk = 36

    defaultConfig {
        minSdk = 21

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    implementation("com.google.android.material:material:1.11.0")
    implementation ("com.brandio.ads:sdk:5.7.1")
    implementation ("com.ironsource.sdk:mediationsdk:9.2.0")
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
