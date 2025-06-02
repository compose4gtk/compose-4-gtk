import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.HorizontalClamp
import io.github.compose4gtk.adw.components.ToastOverlay
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.gtk.components.Entry
import io.github.compose4gtk.gtk.components.FlowBox
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.margin
import org.gnome.adw.Toast

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("Test", onClose = ::exitApplication) {
            ToastOverlay {
                VerticalBox {
                    HeaderBar()

                    HorizontalClamp {
                        VerticalBox {
                            var text by remember { mutableStateOf("") }
                            Entry(
                                text = text,
                                onTextChange = { text = it },
                                placeholderText = "Inset text here",
                                modifier = Modifier.margin(margin = 8),
                            )
                            FlowBox(homogeneous = true) {
                                val tokens = text.split(' ').filter { it.isNotBlank() }
                                for (token in tokens) {
                                    Button(token, modifier = Modifier.margin(margin = 8), onClick = {
                                        dismissAllToasts()
                                        addToast(Toast.builder().setTitle("Clicked on $token").build())
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
