import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.SpinButton
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.margin

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("Spin Button", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar(modifier = Modifier.cssClasses("flat"))

                var value by remember { mutableDoubleStateOf(0.0) }
                var savedValue by remember { mutableDoubleStateOf(value) }

                VerticalBox(
                    spacing = 8,
                    modifier = Modifier.margin(16),
                ) {
                    SpinButton(
                        value = value,
                        onActivate = {
                            savedValue = value
                        },
                        onValueChange = { newValue ->
                            value = newValue
                        },
                        upper = 100.0,
                        stepIncrement = 0.25,
                        pageIncrement = 10.0,
                        digits = 2,
                        numeric = true,
                    )

                    Label(text = "Saved value: $savedValue")
                    Label(text = "Press enter to save a value", modifier = Modifier.cssClasses("dimmed"))
                }
            }
        }
    }
}
