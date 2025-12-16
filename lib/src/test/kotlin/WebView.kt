import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.EntryRow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.Frame
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.expand
import io.github.compose4gtk.modifier.margin
import io.github.compose4gtk.webkit.components.WebView

const val DEFAULT_URI = "https://compose4gtk.github.io/compose-4-gtk/"

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Web View", onClose = ::exitApplication, defaultWidth = 1200, defaultHeight = 800) {
            VerticalBox {
                HeaderBar()

                var uri by remember { mutableStateOf(DEFAULT_URI) }
                var savedUri by remember { mutableStateOf(DEFAULT_URI) }

                EntryRow(title = "Uri", text = uri, onTextChange = { uri = it }, onApply = { savedUri = uri })

                Frame(
                    modifier = Modifier.margin(16)
                ) {
                    WebView(modifier = Modifier.expand(), uri = savedUri)
                }
            }
        }
    }
}
