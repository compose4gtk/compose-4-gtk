import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.application
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.ToolbarView
import io.github.compose4gtk.gtk.components.Box
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.ToggleButton
import org.gnome.gtk.Orientation

fun main(args: Array<String>) {
    application("my.example.hello-app", args) {
        ApplicationWindow("Test", onClose = ::exitApplication) {
            var topBarRevealed by remember { mutableStateOf(true) }
            var doubleBar by remember { mutableStateOf(false) }
            ToolbarView(
                revealTopBars = topBarRevealed,
                topBar = {
                    HeaderBar(title = { Label("Header 1") })
                    if (doubleBar) {
                        HeaderBar(title = { Label("Header 2") })
                    }
                },
            ) {
                Box(orientation = Orientation.VERTICAL) {
                    ToggleButton("Reveal top app bar", topBarRevealed, { topBarRevealed = !topBarRevealed })
                    ToggleButton("Double app bar", doubleBar, { doubleBar = !doubleBar })
                }
            }
        }
    }
}
