import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.*
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.sizeRequest
import org.gnome.gtk.Orientation

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("Test", onClose = ::exitApplication) {
            Box(orientation = Orientation.VERTICAL) {
                HeaderBar()

                var show by remember { mutableStateOf(false) }
                ToggleButton("Show  text", show, {
                    show = !show
                })
                if (show) {
                    Label("A random label that can be hidden", modifier = Modifier.sizeRequest(300, 150))
                }
            }
        }
    }
}
