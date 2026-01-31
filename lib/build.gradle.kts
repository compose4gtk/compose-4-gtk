import org.jreleaser.model.Active

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
    `maven-publish`
    id("org.jreleaser") version "1.20.0"
    alias(libs.plugins.versioning)
    alias(libs.plugins.detekt)
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}

dependencies {
    dokka(project(":lib:core"))
    dokka(project(":lib:gtk"))
    dokka(project(":lib:adw"))
}

group = "io.github.compose4gtk"
version = "0.0-SNAPSHOT"
gitVersioning.apply {
    refs {
        tag("v(?<version>.*)") {
            version = $$"${ref.version}"
        }
        branch(".+") {
            version = $$"${ref}-SNAPSHOT"
        }
    }
    rev {
        version = $$"${commit}"
    }
}

kotlin {
    jvmToolchain(22)
}

java {
    sourceCompatibility = JavaVersion.VERSION_22
    targetCompatibility = JavaVersion.VERSION_22
}

val readMeToDocIndexTask = tasks.register<Copy>("readmeToDocIndex") {
    group = "dokka"
    val inputFile = layout.projectDirectory.file("${rootProject.projectDir}/README.md")
    from(inputFile)
    into(layout.buildDirectory.dir("generated-doc"))
    filter { line ->
        if (line.startsWith("Documentation is available on")) {
            ""
        } else {
            line
                .replace(
                    "](examples/",
                    "](https://github.com/compose4gtk/compose-4-gtk/blob/main/examples/",
                )
        }
    }
    rename { "main.md" }
}

tasks.named("dokkaGeneratePublicationHtml") {
    dependsOn(readMeToDocIndexTask)
}

dokka {
    moduleName.set("Compose 4 GTK")
    dokkaPublications.html {
        failOnWarning.set(true)
        includes.from(layout.buildDirectory.dir("generated-doc/main.md"))
    }
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaGeneratePublicationHtml)
    from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
    archiveClassifier = "javadoc"
}

subprojects {
    group = "io.github.compose4gtk"

    plugins.withId("maven-publish") {
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["kotlin"])
                    artifactId = project.name
                    artifact(tasks["kotlinSourcesJar"])

                    pom {
                        inceptionYear = "2023"
                        url = "https://github.com/compose4gtk/compose-4-gtk"
                        licenses {
                            license {
                                name = "GNU Lesser General Public License v3.0"
                                url = "https://www.gnu.org/licenses/lgpl-3.0.en.html"
                            }
                        }
                        developers {
                            developer {
                                name = "Marco Marangoni"
                                email = "marco.marangoni1@gmail.com"
                            }
                        }
                        contributors {
                            contributor {
                                name = "Thomas Lavoie"
                                email = "lavoiethomas17@gmail.com"
                            }
                        }
                        scm {
                            connection = "scm:git:git://github.com/compose4gtk/compose-4-gtk.git"
                            developerConnection = "scm:git:ssh://github.com:compose4gtk/compose-4-gtk.git"
                            url = "https://github.com/compose4gtk/compose-4-gtk"
                        }
                    }
                }
            }
            repositories {
                maven {
                    setUrl(layout.buildDirectory.dir("staging-deploy"))
                }
            }
        }
    }
}

jreleaser {
    strict = true
    gitRootSearch = true
    signing {
        active = Active.ALWAYS
        armored = true
        verify = true
    }
    release {
        github {
            skipTag = true
            releaseNotes {
                enabled = true
            }
            changelog {
                enabled = false
            }
        }
    }
    deploy {
        maven {
            mavenCentral.create("release-deploy") {
                active = Active.RELEASE
                url = "https://central.sonatype.com/api/v1/publisher"
                retryDelay = 60
                setAuthorization("Basic")
                stagingRepository("build/staging-deploy")
            }
            // TODO: make snapshots work
            // mavenCentral.create("snapshot-deploy") {
            //     active = Active.SNAPSHOT
            //     url = "https://central.sonatype.com/api/v1/publisher"
            //     retryDelay = 60
            //     setAuthorization("Basic")
            //     snapshotSupported = true
            //     stagingRepository("build/staging-deploy")
            // }
        }
    }
}

tasks.register("publishAll") {
    dependsOn(subprojects.map { it.tasks.named("publish") })
}

tasks.named("jreleaserFullRelease") {
    dependsOn("publishAll")
}

tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}
