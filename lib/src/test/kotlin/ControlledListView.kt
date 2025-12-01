import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.gtk.components.ControlledListView
import io.github.compose4gtk.gtk.components.HorizontalBox
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.ScrolledWindow
import io.github.compose4gtk.gtk.components.ToggleButton
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.expand
import io.github.compose4gtk.modifier.margin
import org.gnome.gobject.GObject

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(
            title = "Controlled ListView",
            onClose = ::exitApplication,
            defaultWidth = 800,
            defaultHeight = 800) {
            VerticalBox {
                HeaderBar(modifier = Modifier.cssClasses("flat"))

                var selectionEnabled by remember { mutableStateOf(true) }

                var itemSize by remember { mutableIntStateOf(20) }
                val items = remember(itemSize) {
                    List(itemSize) { index ->
                        DataItem("Custom item #$index")
                    }
                }

                val selectedItems = remember { mutableStateSetOf(2, 4) }
                var selectedItem by remember { mutableStateOf<Int?>(1) }

                HorizontalBox(modifier = Modifier.expand()) {
                    Panel("Single Selection") {
                        ControlledListView(
                            items = items,
                            selectedItem = selectedItem,
                            onSelectionChange = {
                                if (selectionEnabled) {
                                    selectedItem = it
                                }
                            },
                        ) { item ->
                            Label(text = item.name)
                        }
                    }
                    Panel("Multiple Selection") {
                        ControlledListView(
                            items = items,
                            selectedItems = selectedItems,
                            enableRubberband = true,
                            onSelectionChange = {
                                if (selectionEnabled) {
                                    selectedItems.clear()
                                    selectedItems.addAll(it)
                                }
                            }
                        ) { item ->
                            Label(text = item.name)
                        }
                    }
                    Panel("No Selection") {
                        ControlledListView(
                            items = items,
                        ) { item ->
                            Label(text = item.name)
                        }
                    }
                }
                VerticalBox(modifier = Modifier.margin(8), spacing = 8) {
                    ToggleButton(label = "Toggle Selection", active = selectionEnabled, onToggle = {
                        selectionEnabled = !selectionEnabled
                    })
                    Button(label = "Unselect Everything", onClick = {
                        selectedItem = null
                        selectedItems.clear()
                    })
                }
            }
        }
    }
}

private data class DataItem(val name: String) : GObject()

@Composable
private fun Panel(title: String, content: @Composable () -> Unit) {
    VerticalBox(Modifier.expand()) {
        HeaderBar(title = { Label(title) }, showEndTitleButtons = false, showStartTitleButtons = false)
        ScrolledWindow(Modifier.expand()) {
            content()
        }
    }
}