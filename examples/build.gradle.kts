plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.detekt)
}

kotlin {
    jvmToolchain(22)
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(project(":lib:core"))
    implementation(project(":lib:gtk"))
    implementation(project(":lib:adw"))
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)
}

tasks.named("assemble") {
    dependsOn("compileGResources")
}

detekt {
    config.setFrom(file("../config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

tasks.register<Exec>("compileGResources") {
    workingDir("src/main/gresources")
    commandLine("glib-compile-resources", "--target=../resources/resources.gresource", "resources.gresource.xml")
}

tasks.named("processResources") {
    dependsOn("compileGResources")
}

tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}
