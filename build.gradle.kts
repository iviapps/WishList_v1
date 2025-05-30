// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false // 👈 SOLO UNA VEZ
}
afterEvaluate {
    tasks.register<Copy>("copyDebugApkToDownloads") {
        val apkDir = layout.buildDirectory.dir("outputs/apk/debug")
        val apkFile = apkDir.map { it.file("app-debug.apk") }
        from(apkFile)
        into(System.getProperty("user.home") + "/Downloads")
        doLast {
            println("✅ APK copiada a la carpeta Descargas.")
        }
    }
}
