import androidx.compose.runtime.remember
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.ToastOverlay
import io.github.compose4gtk.gtk.components.CenterBox
import io.github.compose4gtk.gtk.components.DropDown
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.gtk.components.rememberSingleSelectionModel
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.alignment
import io.github.compose4gtk.modifier.expand
import org.gnome.adw.Toast
import org.gnome.gobject.GObject
import org.gnome.gtk.Align

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "DropDown", onClose = ::exitApplication, defaultWidth = 600, defaultHeight = 400) {
            VerticalBox {
                HeaderBar()
                ToastOverlay {
                    val items = remember {
                        List(100) { index ->
                            DropDownItem("Custom item #$index")
                        }
                    }
                    val model = rememberSingleSelectionModel(items)
                    CenterBox(Modifier.expand()) {
                        DropDown(
                            modifier = Modifier.alignment(Align.CENTER),
                            model = model,
                            item = {
                                Label("Dropdown widget for " + it.name)
                            },
                            selectedItem = {
                                Label(it.name)
                            },
                            onSelectionChanges = {
                                addToast(Toast.builder().setTitle("Selected item ${it.name}").build())
                            },
                        )
                    }
                }
            }
        }
    }
}

private data class DropDownItem(val name: String) : GObject()
