package io.github.compose4gtk.gtk

import androidx.compose.runtime.Composable
import io.github.compose4gtk.ApplicationScope
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.gtk.components.GtkApplicationWindow
import io.github.compose4gtk.initializeApplication
import org.gnome.gio.Application
import org.gnome.gio.ApplicationFlags
import kotlin.run

@Deprecated(
    "Use adwApplication instead",
    replaceWith = ReplaceWith(
        expression = "gtkApplication(appId, args) { content() }",
        imports = ["io.github.compose4gtk.gtk.gtkApplication"],
    ),
)
fun application(
    appId: String,
    args: Array<String>,
    content: @Composable ApplicationScope.() -> Unit,
) = adwApplication(appId, args, content = content)

/**
 * This is the entry point of LibAdwaita applications.
 *
 * This will start an application. [GtkApplicationWindow] can be added inside the [content] lambda.
 *
 * @param appId the GTK application id. If not null, it must be valid, see [Application.idIsValid].
 * @param args the application arguments. Usually the same as the ones in your `main`. See [Application.run].
 * @param flags the flags used when creating the application. See [ApplicationFlags].
 * @param content the lambda where your application is defined. You can start by adding a [GtkApplicationWindow].
 */
fun gtkApplication(
    appId: String,
    args: Array<String>,
    flags: Set<ApplicationFlags> = setOf(ApplicationFlags.DEFAULT_FLAGS),
    content: @Composable ApplicationScope.() -> Unit,
) {
    val app = org.gnome.gtk.Application(appId, flags)
    app.initializeApplication(args, content)
}
