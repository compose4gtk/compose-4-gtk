# A Kotlin Compose library for Gtk4 and Adw

![Welcome](https://raw.githubusercontent.com/compose4gtk/compose-4-gtk/main/screenshots/welcome.png)

This library provides a Kotlin Compose interface to interact with native Gtk4 and Adw (Libadwaita) components.

TL;DR: with this library, you can declaratively and dynamically create UIs with the GTK and Adw frameworks.

## Usage

![Maven Central](https://img.shields.io/maven-central/v/io.github.compose4gtk/compose-4-gtk.svg?label=Maven%20Central)

This library is still under development, and only pre-release versions are available [on Maven Central](https://central.sonatype.com/artifact/io.github.compose4gtk/compose-4-gtk).

JDK 22 or newer and the Kotlin Compose compiler plugin are required.

For example, on your `build.gradle.kts`:

```kotlin
plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    application
}

kotlin {
    jvmToolchain(22)
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("io.github.compose4gtk:compose-4-gtk:<latest-version>")
}

application {
    mainClass = "org.example.MainKt"
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}
```

More information on how to set up the Compose compiler plugin on https://developer.android.com/develop/ui/compose/compiler

## The basics

An empty window:

```kotlin
fun main(args: Array<String>) {
    application("my.example.HelloApp", args) {
        ApplicationWindow("Test", onClose = ::exitApplication) {
            // Your content here
        }
    }
}
```

----

A window with a header, and three buttons inside:

```kotlin
fun main(args: Array<String>) {
    application("my.example.HelloApp", args) {
        ApplicationWindow("Test", onClose = ::exitApplication) {
            Box(orientation = Orientation.VERTICAL) {
                HeaderBar()

                Button("Button 1", onClick = { println("Clicked!") })
                Button("Button 2", onClick = { println("Clicked!") })
                Button("Button 3", onClick = { println("Clicked!") })
            }
        }
    }
}
```

----

An interactive button that shows or hides a label:

```kotlin
fun main(args: Array<String>) {
    application("my.example.HelloApp", args) {
        ApplicationWindow("Test", onClose = ::exitApplication) {
            Box(orientation = Orientation.VERTICAL) {
                HeaderBar()

                var show by remember { mutableStateOf(false) }
                Button(
                    label = if (show) "Hide" else "Show",
                    onClick = { show = !show },
                )
                if (show) {
                    Label("A random label that can be hidden")
                }
            }
        }
    }
}
```

To manage the app's state, this library uses the same principles as Kotlin Compose,
see https://developer.android.com/jetpack/compose/state#state-in-composables

----

An `Entry`, that will make all text uppercase:

```kotlin
fun main(args: Array<String>) {
    application("my.example.HelloApp", args) {
        ApplicationWindow("Test", onClose = ::exitApplication) {
            Box(orientation = Orientation.VERTICAL) {
                HeaderBar()

                var text by remember { mutableStateOf("") }
                Entry(
                    text = text,
                    placeholderText = "All text will be uppercase",
                    onTextChange = { text = it.uppercase() },
                )
            }
        }
    }
}
```

This example highlights one of the main properties of Compose:
the state is owned by your app, and not by the GTK Widgets.
This means that the source of truth for what the text should be is the `text` variable.
See https://developer.android.com/jetpack/compose/state#state-hoisting for more details.

In this example, at any point the `Entry` could ever contain a text that is no uppercase.

----

## Example

![Demo](https://raw.githubusercontent.com/compose4gtk/compose-4-gtk/main/screenshots/demo.gif)

```kotlin
fun main(args: Array<String>) {
    application("my.example.HelloApp", args) {
        ApplicationWindow("Test", onClose = ::exitApplication) {
            ToastOverlay {
                VerticalBox {
                    HeaderBar()

                    HorizontalClamp {
                        VerticalBox {
                            var text by remember { mutableStateOf("") }
                            Entry(
                                text = text,
                                onTextChange = { text = it },
                                placeholderText = "Inset text here",
                                modifier = Modifier.margin(8)
                            )
                            ScrolledWindow {
                                HorizontalBox {
                                    val tokens = text.split(' ').filter { it.isNotBlank() }
                                    tokens.forEach { token ->
                                        Button(
                                            label = token,
                                            modifier = Modifier.margin(8),
                                            onClick = {
                                                dismissAllToasts()
                                                addToast(Toast.builder().setTitle("Clicked on $token").build())
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
```

----

## GIO Resources

This library provides a convenience function to load `gresource` files in the JAR's resources:

```kotlin
fun main(args: Array<String>) {
    useGioResource("resources.gresource") {
        application("my.example.hello-app", args) {
            ApplicationWindow("Embedded picture", onClose = ::exitApplication) {
                Picture(
                    ImageSource.forResource("/my/example/hello-app/images/lulu.jpg"),
                    contentFit = ContentFit.COVER,
                    modifier = Modifier.expand(),
                )
            }
        }
    }
}
```

In this example, `resources.gresource` is a GIO resource bundle compiled with `glib-compile-resources`, and included in
the JAR. 
