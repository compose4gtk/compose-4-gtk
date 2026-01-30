import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.gtk.components.Box
import io.github.compose4gtk.gtk.components.LinkButton
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.alignment
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.margin
import org.gnome.gtk.Align

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("Test", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar()

                StatusPage(title = "Link Button", description = "Links to travel the web") {
                    VerticalBox(spacing = 16) {
                        Box(modifier = Modifier.cssClasses("card").alignment(Align.CENTER)) {
                            VerticalBox(modifier = Modifier.margin(16)) {
                                val uri = "https://github.com/compose4gtk/compose-4-gtk"
                                LinkButton(
                                    "Link to the GitHub repo",
                                    uri = uri,
                                )
                            }
                        }

                        Box(modifier = Modifier.cssClasses("card").alignment(Align.CENTER)) {
                            VerticalBox(modifier = Modifier.margin(16)) {
                                val uri = "https://docs.gtk.org/gtk4/"
                                LinkButton(
                                    "Link to the GTK documentation",
                                    uri = uri,
                                ) {
                                    println("Visiting $uri")
                                    false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
