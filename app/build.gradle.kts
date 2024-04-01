import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(rootProject)
    implementation(project(":pack"))

    implementation(libs.imgui)
    implementation(libs.jbannotations)
    implementation(libs.gson)
    implementation(libs.lwjglStb)
    implementation(libs.adventureApi)

    arrayOf("natives-linux", "natives-windows", "natives-macos").forEach {
        implementation("org.lwjgl:lwjgl-stb::$it")
    }
}

tasks {
    application {
        mainClass.set("com.frfole.mrpc.app.Main")
    }

    withType<ShadowJar> {
        archiveFileName.set("mrpc.jar")
    }
}
