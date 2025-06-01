import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.gtk.components.HorizontalBox
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.ListView
import io.github.compose4gtk.gtk.components.ScrolledWindow
import io.github.compose4gtk.gtk.components.SelectionMode
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.gtk.components.rememberMultiSelectionModel
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.expand
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.gobject.GObject

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("Test", onClose = ::exitApplication, defaultWidth = 800, defaultHeight = 800) {
            VerticalBox {
                HeaderBar(title = { Label("ListView") })
                var itemSize by remember { mutableIntStateOf(5) }
                val items = remember(itemSize) {
                    List(itemSize) { index ->
                        ListItem("Custom item #$index")
                    }
                }
                var show by remember { mutableStateOf(true) }
                HorizontalBox(Modifier.expand()) {
                    if (show) {
                        Panel("Base model (single selection)") {
                            ListView(
                                items = itemSize,
                                selectionMode = SelectionMode.Single,
                                onActivate = { position -> logger.info { "activated item #$position" } },
                            ) { index ->
                                Label("Item #$index")
                            }
                        }
                        Panel("Custom model (multiple selection)") {
                            ListView(
                                model = rememberMultiSelectionModel(items),
                                onActivate = { position -> logger.info { "activated item #$position" } },
                            ) { customItem ->
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
                Button(if (show) "hide" else "show", onClick = {
                    show = !show
                })
            }
        }
    }
}

private data class ListItem(val name: String) : GObject()

@Composable
private fun Panel(title: String, content: @Composable () -> Unit) {
    VerticalBox(Modifier.expand()) {
        HeaderBar(title = { Label(title) }, showEndTitleButtons = false, showStartTitleButtons = false)
        ScrolledWindow(Modifier.expand()) {
            content()
        }
    }
}
