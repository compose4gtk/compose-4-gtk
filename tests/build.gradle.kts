plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
    application
}

dependencies {
    implementation(project(":lib:core"))
    implementation(project(":lib:gtk"))
    implementation(project(":lib:adw"))
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)
    implementation(libs.kotlin.logging)
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    jvmToolchain(22)
}

tasks.named("assemble") {
    dependsOn("compileGResources")
}

tasks.register<Exec>("compileGResources") {
    workingDir("src/main/gresources")
    commandLine("glib-compile-resources", "--target=../resources/resources.gresource", "resources.gresource.xml")
}

tasks.named("processResources") {
    dependsOn("compileGResources")
}

tasks.test {
    useJUnitPlatform()
}
