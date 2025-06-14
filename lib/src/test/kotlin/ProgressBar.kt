import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.gtk.components.ProgressBar
import io.github.compose4gtk.gtk.components.ToggleButton
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Progress Bar", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar(modifier = Modifier.cssClasses("flat"))

                var pulsate by remember { mutableStateOf(false) }

                StatusPage(
                    title = "Progress Bar",
                    description = "Display the progress of a long running operation",
                ) {
                    VerticalBox(spacing = 8) {
                        ProgressBar(fraction = 0.25, showText = true)
                        ProgressBar(pulse = pulsate, pulseStep = 0.25)
                        ToggleButton(label = "Pulsate", active = pulsate, onToggle = { pulsate = !pulsate })
                    }
                }
            }
        }
    }
}
