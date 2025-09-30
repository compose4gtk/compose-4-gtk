import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.components.Entry
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.ToggleButton
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.margin
import io.github.compose4gtk.useGioResource

fun main(args: Array<String>) {
    useGioResource("resources.gresource") {
        adwApplication("my.example.hello-app", args) {
            ApplicationWindow(title = "Entry", onClose = ::exitApplication) {
                VerticalBox(
                    spacing = 8,
                    modifier = Modifier.margin(8),
                ) {
                    HeaderBar(modifier = Modifier.cssClasses("flat"))

                    var tempInfo by remember { mutableStateOf("") }
                    var info by remember { mutableStateOf("") }
                    var visible by remember { mutableStateOf(true) }

                    Label(text = "Saved info: $info")
                    Label(text = "Press enter to save", modifier = Modifier.cssClasses("dimmed"))

                    Entry(
                        text = tempInfo,
                        onTextChange = { tempInfo = it },
                        onActivate = { info = tempInfo },
                        primaryIcon = if (visible) ImageSource.Icon("heart-filled-symbolic") else null,
                        onPrimaryIconPress = { println("Liked \"$tempInfo\"!") },
                        secondaryIcon = if (visible) ImageSource.Icon("system-search-symbolic") else null,
                        onSecondaryIconPress = { println("Searching for \"$tempInfo\"...") },
                    )

                    ToggleButton(active = visible, label = "Toggle icons", onToggle = { visible = !visible })
                }
            }
        }
    }
}
