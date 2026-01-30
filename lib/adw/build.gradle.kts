plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    api(compose.runtime)
    api(libs.javagi.gtk)
    api(libs.javagi.adw)
    api(project(":lib:core"))
    api(project(":lib:gtk"))
    implementation(libs.slf4j.api)
    implementation(libs.kotlin.logging)

    testImplementation(kotlin("test"))
    testImplementation(libs.slf4j.simple)
}

kotlin {
    jvmToolchain(22)
}

tasks.test {
    useJUnitPlatform()
}