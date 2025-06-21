import androidx.compose.runtime.LaunchedEffect
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
import io.github.compose4gtk.gtk.components.rememberProgressBarState
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import kotlinx.coroutines.delay

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Progress Bar", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar(modifier = Modifier.cssClasses("flat"))

                var pulsate by remember { mutableStateOf(false) }
                val progressBarState = rememberProgressBarState(0.25)
                val pulsatingProgressBarState = rememberProgressBarState(0.0)

                LaunchedEffect(pulsate) {
                    while (pulsate) {
                        pulsatingProgressBarState.pulse()
                        delay(250L)
                    }
                    pulsatingProgressBarState.reset()
                }

                StatusPage(
                    title = "Progress Bar",
                    description = "Display the progress of a long running operation",
                ) {
                    VerticalBox(spacing = 8) {
                        ProgressBar(state = progressBarState, showText = true, text = "Progressbar fixed at 25%")
                        ProgressBar(state = pulsatingProgressBarState, pulseStep = 0.25)
                        ToggleButton(label = "Pulsate", active = pulsate, onToggle = { pulsate = !pulsate })
                    }
                }
            }
        }
    }
}
