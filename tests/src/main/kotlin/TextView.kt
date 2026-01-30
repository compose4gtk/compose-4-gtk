import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.ScrolledWindow
import io.github.compose4gtk.gtk.components.TextView
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.expand

const val MAX_CHARS = 64

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Text", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar()

                var text by remember { mutableStateOf("") }

                Label(text = "${text.length} / $MAX_CHARS")

                ScrolledWindow(modifier = Modifier.expand()) {
                    TextView(
                        text = text,
                        modifier = Modifier.expand(),
                        onTextChange = { if (it.length <= MAX_CHARS) text = it },
                    )
                }
            }
        }
    }
}
