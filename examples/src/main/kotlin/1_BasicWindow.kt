import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.gtk.components.Box
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import org.gnome.gtk.Orientation

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
