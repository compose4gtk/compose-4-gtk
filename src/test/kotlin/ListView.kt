import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.application
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.*
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.expand
import org.gnome.gio.ListStore
import org.gnome.gobject.GObject
import org.gnome.gtk.MultiSelection
import org.gnome.gtk.SelectionModel

fun main(args: Array<String>) {
    application("my.example.hello-app", args) {
        ApplicationWindow("Test", onClose = ::exitApplication, defaultWidth = 800, defaultHeight = 800) {
            VerticalBox {
                HeaderBar(title = { Label("ListView") })
                var itemSize by remember { mutableIntStateOf(5) }
                var show by remember { mutableStateOf(true) }
                HorizontalBox(Modifier.expand()) {
                    if (show) {
                        Panel("Default model (no selection)") {
                            ListView(items = itemSize) { index ->
                                Label("Item #$index")
                            }
                        }
                        Panel("Custom model (multiple selection)") {
                            val model = createModel(itemSize)
                            ListView(model) { customItem ->
                                Label(customItem.name)
                            }
                        }
                    }
                }
                Button("Add one", onClick = {
                    itemSize += 1
                })
                Button("Remove last", onClick = {
                    if (itemSize > 0) {
                        itemSize -= 1
                    }
                })
                Button("Double items", onClick = {
                    itemSize *= 2
                })
                Button("Halve items", onClick = {
                    itemSize /= 2
                })
                Button("Remove all items", onClick = {
                    itemSize = 0
                })
                Button(if (show)"hide" else "show", onClick = {
                    show = !show
                })
            }
        }
    }
}

private class CustomItem(val name: String) : GObject()

@Composable
private fun createModel(count: Int): SelectionModel<CustomItem> {
    val model = remember { ListStore<CustomItem>() }
    while (model.size > count) {
        model.removeLast()
    }
    while (model.size < count) {
        model.append(CustomItem("Custom item #${model.size}"))
    }
    return remember { MultiSelection(model) }
}

@Composable
private fun Panel(title: String, content: @Composable () -> Unit) {
    VerticalBox(Modifier.expand()) {
        HeaderBar(title = { Label(title) }, showEndTitleButtons = false, showStartTitleButtons = false)
        ScrolledWindow(Modifier.expand()) {
            content()
        }
    }
}
