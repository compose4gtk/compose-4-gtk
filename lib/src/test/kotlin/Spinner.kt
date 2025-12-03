import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.HorizontalBox
import io.github.compose4gtk.adw.components.Spinner as AdwSpinner
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.gtk.components.Spinner as GtkSpinner
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.alignment
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.expand
import io.github.compose4gtk.modifier.sizeRequest
import org.gnome.gtk.Align

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Spinner", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar(modifier = Modifier.cssClasses("flat"))

                StatusPage(title = "Spinner", description = "Useful to display a loading state") {
                    HorizontalBox(modifier = Modifier.expand().alignment(Align.CENTER), spacing = 8) {
                        AdwSpinner(modifier = Modifier.sizeRequest(48, 48))
                        GtkSpinner(modifier = Modifier.sizeRequest(48, 48), spinning = true)
                    }
                }
            }
        }
    }
}