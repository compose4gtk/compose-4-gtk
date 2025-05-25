import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.application
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.ToggleButton
import io.github.compose4gtk.gtk.components.VerticalBox
import kotlin.random.Random

fun main(args: Array<String>) {
    application("my.example.hello-app", args) {
        ApplicationWindow("Test", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar()
                var show by remember { mutableStateOf(false) }
                var allowClose by remember { mutableStateOf(true) }
                var fullscreen by remember { mutableStateOf(false) }
                var maximized by remember { mutableStateOf(false) }
                var title by remember { mutableStateOf("Random window") }

                Button("Show normal", onClick = { show = !show })
                ToggleButton("Allow close", allowClose, { allowClose = !allowClose })
                ToggleButton("Fullscreen", fullscreen, { fullscreen = !fullscreen })
                ToggleButton("Maximized", maximized, { maximized = !maximized })
                Button("Randomize title", onClick = {
                    title = "Random Window ${Random.nextInt()}"
                })

                if (show) {
                    ApplicationWindow(
                        title = title,
                        fullscreen = fullscreen,
                        maximized = maximized,
                        onClose = {
                            if (allowClose) {
                                show = false
                            }
                        },
                    ) {
                        VerticalBox {
                            HeaderBar()
                            Label("This is a window!")
                        }
                    }
                }
            }
        }
    }
}
