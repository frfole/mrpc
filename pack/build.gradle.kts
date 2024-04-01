import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(rootProject)

    implementation(libs.gson)
    implementation(libs.jbannotations)
    implementation(libs.adventureKey)
    implementation(libs.adventureApi)
    implementation(libs.adventureSGson)
    implementation(libs.adventureSLegacy)
}

tasks {
    application {
        mainClass.set("com.frfole.mrpc.app.Main")
    }

    withType<ShadowJar> {
        archiveFileName.set("mrpc.jar")
    }
}
