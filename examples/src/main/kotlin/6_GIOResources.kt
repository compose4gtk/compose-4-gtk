import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.components.IconButton
import io.github.compose4gtk.gtk.components.Picture
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.expand
import io.github.compose4gtk.useGioResource
import org.gnome.gtk.ContentFit

fun main(args: Array<String>) {
    useGioResource("resources.gresource") {
        adwApplication("my.example.hello-app", args) {
            ApplicationWindow("Lulù", onClose = ::exitApplication, defaultWidth = 400, defaultHeight = 400) {
                VerticalBox {
                    HeaderBar(
                        startWidgets = {
                            IconButton(
                                // The vector icon is embedded into the gresources file
                                icon = ImageSource.Icon("heart-filled-symbolic"),
                                // The "accent-colored" CSS class is defined in the gresources file
                                modifier = Modifier.cssClasses("accent-colored"),
                                onClick = { println("TODO: pet the dog") },
                            )
                        },
                    )
                    Picture(
                        // The image is embedded into the gresources file
                        ImageSource.forResource("/my/example/hello-app/images/lulu.jpg"),
                        contentFit = ContentFit.COVER,
                        modifier = Modifier.expand(),
                    )
                }
            }
        }
    }
}
