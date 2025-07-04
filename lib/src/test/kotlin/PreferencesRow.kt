import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.ButtonRow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.components.ListBox
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.margin
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.gtk.SelectionMode

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("Preferences", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar()

                ListBox(
                    selectionMode = SelectionMode.NONE,
                    modifier = Modifier
                        .margin(16)
                        .cssClasses("boxed-list"),
                ) {
                    ButtonRow(
                        title = "Search",
                        startIcon = ImageSource.Icon("system-search-symbolic"),
                    )
                    ButtonRow(
                        title = "Delete",
                        modifier = Modifier.cssClasses("destructive-action"),
                        endIcon = ImageSource.Icon("edit-delete-symbolic"),
                        onActivate = {
                            logger.info { "I deleted stuff" }
                        }
                    )
                }
            }
        }
    }
}