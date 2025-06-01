import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.Switch
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.alignment
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.sensitive
import org.gnome.gtk.Align

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Switch", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar(modifier = Modifier.cssClasses("flat"))

                StatusPage(title = "Switch", description = "A simple on/off control") {
                    VerticalBox(spacing = 16) {
                        var isSwitchActive by remember { mutableStateOf(true) }

                        VerticalBox(spacing = 8) {
                            Switch(
                                onToggle = { newState ->
                                    isSwitchActive = newState
                                },
                                active = isSwitchActive,
                                modifier = Modifier.alignment(Align.CENTER),
                            )
                            Label(if (isSwitchActive) "On" else "Off")
                        }

                        VerticalBox(spacing = 8) {
                            Switch(
                                active = isSwitchActive,
                                onToggle = { newState ->
                                    println("Doesn't change state")
                                },
                                modifier = Modifier.alignment(Align.CENTER),
                            )
                            Label("Mirror & read-only")
                        }

                        VerticalBox(spacing = 8) {
                            Switch(
                                active = isSwitchActive,
                                onToggle = {},
                                modifier = Modifier
                                    .alignment(Align.CENTER)
                                    .sensitive(false),
                            )
                            Label("Disabled")
                        }
                    }
                }
            }
        }
    }
}
