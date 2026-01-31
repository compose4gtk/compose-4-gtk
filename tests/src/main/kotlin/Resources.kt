import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.components.*
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.margin
import io.github.compose4gtk.modifier.verticalAlignment
import io.github.compose4gtk.useGioResource
import org.gnome.gtk.Align

fun main(args: Array<String>) {
    val possibleIcons = setOf(
        // Custom icons
        ImageSource.Icon("heart-filled-symbolic"),
        ImageSource.Icon("cat-symbolic"),
        ImageSource.forResource("/my/example/hello-app/images/lulu.jpg"),
        // Default icons
        ImageSource.Icon("system-search-symbolic"),
        ImageSource.spinner,
        null,
    )
    useGioResource("resources.gresource") {
        adwApplication("my.example.hello-app", args) {
            ApplicationWindow(
                "Embedded resources",
                onClose = ::exitApplication,
            ) {
                VerticalBox {
                    var icon by remember { mutableStateOf(possibleIcons.first()) }
                    HeaderBar(Modifier.cssClasses("flat"))
                    Row("Embedded icon") {
                        Image(
                            icon,
                            iconSize = ImageSize.Normal,
                        )
                    }
                    Row("Accent icon") {
                        Image(
                            icon,
                            iconSize = ImageSize.Large,
                            modifier = Modifier.cssClasses("accent-colored"),
                        )
                    }
                    Row("Big") {
                        Image(
                            icon,
                            iconSize = ImageSize.Specific(sizePx = 96),
                        )
                    }
                    Row("Styled text") {
                        Label("Big boi", Modifier.cssClasses("big-boi", "accent-colored"))
                    }
                    Button("Randomize", onClick = {
                        icon = (possibleIcons - icon).random()
                    })

                    Label(
                        "The stylesheet and the icons are declared in the test/gresources directory",
                        Modifier.margin(24).cssClasses("dimmed"),
                    )
                }
            }
        }
    }
}

@Composable
fun Row(label: String, content: @Composable () -> Unit) {
    HorizontalBox {
        Label("$label:", Modifier.verticalAlignment(Align.CENTER))
        content()
    }
}
