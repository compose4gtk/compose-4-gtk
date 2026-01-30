import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.gtk.components.Box
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.ListBox
import io.github.compose4gtk.gtk.components.ListBoxRow
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.margin

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "List Box Row", onClose = ::exitApplication) {
            Box {
                ListBox {
                    for (i in 0 .. 10) {
                        ListBoxRow {
                            Box(
                                modifier = Modifier.margin(start = 4),
                            ) {
                                Label("$i: ListBoxRow")
                            }
                        }
                    }
                }
            }
        }
    }
}
