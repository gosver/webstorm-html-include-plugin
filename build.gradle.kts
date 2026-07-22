plugins {
    id("org.jetbrains.intellij.platform") version "2.7.0"
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
}

group = "custom.html.include"
version = "1.0.0"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        webstorm("2025.2")
    }
}

tasks {
    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("999.*")
    }
}