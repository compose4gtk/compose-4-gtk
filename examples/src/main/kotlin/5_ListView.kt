import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.ListView
import io.github.compose4gtk.gtk.components.ScrolledWindow
import io.github.compose4gtk.gtk.components.SelectionMode
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.expand

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("Test", onClose = ::exitApplication, defaultWidth = 800, defaultHeight = 800) {
            VerticalBox {
                HeaderBar(title = { Label("ListView with 10 thousand items") })
                ScrolledWindow(Modifier.expand()) {
                    ListView(
                        items = 10000,
                        selectionMode = SelectionMode.Multiple,
                    ) { index ->
                        Label("Item #$index")
                    }
                }
            }
        }
    }
}
