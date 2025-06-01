# A Kotlin Compose library for Gtk4 and Adw

![Welcome](https://raw.githubusercontent.com/compose4gtk/compose-4-gtk/main/docs/screenshots/welcome.png)

Compose 4 GTK is a Kotlin library that brings Jetpack Compose-style declarative UI development to native Linux
applications using GTK 4 and Libadwaita (Adw).  
This library enables developers to build modern, responsive desktop applications with Kotlin, leveraging the power of
Compose and the native capabilities of GTK.

### Great for users

Applications built with Compose 4 GTK fit perfectly in with the rest of other applications in Gnome.  
They match in look, feel, customizability, accessibility, etc.

They also perform great, use modern APIs (e.g. Wayland) and benefits from all the optimizations implemented for native
GTK apps.

### Great for developers

Writing synamic UIs with Compose 4 GTK is extremely simple, consistent, and predictable.  
UIs are written using declarative, stateless and side effect free functions, that take care of creating, updating, and
destroying GTK widgets for you.

Jetpack Compose is the default and recommended UI framework for Android, and this library brings some of the same
goodness to the Linux desktop.

## Getting started

![Maven Central](https://img.shields.io/maven-central/v/io.github.compose4gtk/compose-4-gtk.svg?label=Maven%20Central)

This library is still under development. Pre-release versions are
available [on Maven Central](https://central.sonatype.com/artifact/io.github.compose4gtk/compose-4-gtk).

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

More information on how to set up the Compose compiler plugin
on https://developer.android.com/develop/ui/compose/compiler.

## Examples

### Basic window

![Basic window](https://raw.githubusercontent.com/compose4gtk/compose-4-gtk/main/docs/screenshots/basic_window.png)

Source [here](examples/src/main/kotlin/1_BasicWindow.kt).

A window with a header, some text, and a button:

```kotlin
fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("My first window", onClose = ::exitApplication) {
            Box(orientation = Orientation.VERTICAL) {
                HeaderBar(modifier = Modifier.cssClasses("flat"))
                StatusPage(title = "My first component") {
                    Button("My first button", onClick = { println("Clicked!") })
                }
            }
        }
    }
}
```

This first example highlights the structure of Compose 4 GTK projects:

- a call to `adwApplication` to initialize the application
- one or more `ApplicationWindow`
- several nested widgets that make up the window content

All the declarations above (e.g. `ApplicationWindow`, `Box`, `HeaderBar`, etc.) are function calls that take care of
creating, updating and destroying the native GTK/ADW widgets.

### Dynamic window

![Dynamic window](https://raw.githubusercontent.com/compose4gtk/compose-4-gtk/main/docs/screenshots/dynamic_window.gif)

Source [here](examples/src/main/kotlin/2_DynamicWindow.kt).

An interactive button that shows or hides a label when pressing a button:

```kotlin
fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
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

This example demonstrates stateful UI in Compose:

- Application state is stored in an observable container — here, `remember { mutableStateOf(false) }`.
- The UI reacts to changes in state, dynamically creating or removing components using Kotlin’s control flow (
  `if (show)`).

You can read more about how to handle application state
in [the official Compose documentation](https://developer.android.com/jetpack/compose/state#state-in-composables),
although some concepts only apply to Android.

### Entry with text transformation

![Uppercase Entry](https://raw.githubusercontent.com/compose4gtk/compose-4-gtk/main/docs/screenshots/uppercase_entry.png)

Source [here](examples/src/main/kotlin/3_Uppercase_Entry.kt).

An `Entry`, that will transform the text to be in uppercase:

```kotlin
fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("Uppercase window", onClose = ::exitApplication) {
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

This example highlights one of the main properties of Compose: the state is owned by your app, and not by the GTK
Widgets.

This means that the source of truth for what the text should be is the `text` variable.  
Since we ensure only uppercase strings are assigned to `text`, we're guaranteed that the `Entry` will only contain
uppercase text.

See https://developer.android.com/jetpack/compose/state#state-hoisting for more details.

### Dynamic tags

![Demo](https://raw.githubusercontent.com/compose4gtk/compose-4-gtk/main/screenshots/docs/tags.gif)

Source [here](examples/src/main/kotlin/4_DynamicTags.kt).

```kotlin
fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
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
                                modifier = Modifier.margin(8),
                            )
                            FlowBox(homogeneous = true) {
                                val tokens = text.split(' ').filter { it.isNotBlank() }
                                for (token in tokens) {
                                    Button(token, modifier = Modifier.margin(8), onClick = {
                                        dismissAllToasts()
                                        addToast(Toast.builder().setTitle("Clicked on $token").build())
                                    })
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

This example is a more advanced version of the same concepts explained above:

- the entire state of the application is stored in `var text`
- the `Entry`'s `onTextChange` updates `text` as the user types
- `Buttons` are added to the UI tree by simply iterating over the split words.

As a bonus, this example also illustrates how to show toasts using `ToastOverlay`.

## GIO Resources

This library provides a convenience function to load `gresource` files in the JAR's resources:

```kotlin
fun main(args: Array<String>) {
    useGioResource("resources.gresource") {
        adwApplication("my.example.hello-app", args) {
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
