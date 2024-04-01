plugins {
    id("java")
}

group = "com.frfole"
version = "1.0-SNAPSHOT"

allprojects {
    apply(plugin = "java")

    group = "com.frfole"
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    tasks.test {
        useJUnitPlatform()
    }
}
