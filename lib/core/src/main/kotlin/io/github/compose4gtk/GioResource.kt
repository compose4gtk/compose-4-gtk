package io.github.compose4gtk

import org.gnome.gio.Gio
import org.gnome.gio.Resource

/**
 * Loads a Java resource as a `gresource` file.
 * Useful to load the GIO resources embedded into a JAR.
 */
fun Class<*>.getGioResource(javaResourceName: String): Resource {
    val data = getResourceAsStream(javaResourceName).use { inputStream ->
        inputStream!!.readAllBytes()
    }
    return Resource.fromData(data)
}

/**
 * Registers the resource globally, and then invokes [f].
 * The resource is unregistered before returning
 */
fun <T> Resource.use(f: () -> T): T {
    Gio.resourcesRegister(this)
    try {
        return f()
    } finally {
        Gio.resourcesUnregister(this)
    }
}

/**
 * Loads and registers the given Java resource as a GIO resource, then invokes [f].
 */
fun <T> useGioResource(javaResourceName: String, f: () -> T): T {
    val resource = f.javaClass.getGioResource(javaResourceName)
    return resource.use(f)
}
