import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.ToolbarView
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.gtk.components.Video
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.expand
import io.github.compose4gtk.modifier.horizontalAlignment
import io.github.compose4gtk.modifier.margin
import io.github.compose4gtk.modifier.verticalAlignment
import io.github.compose4gtk.shared.components.LocalApplicationWindow
import org.gnome.gio.File
import org.gnome.gio.ListStore
import org.gnome.gobject.GObject
import org.gnome.gtk.Align
import org.gnome.gtk.FileDialog
import org.gnome.gtk.FileFilter
import java.lang.foreign.MemorySegment

val filter: FileFilter = FileFilter.builder().setName("Video files").setMimeTypes(arrayOf("video/mp4")).build()

val filters = ListStore<FileFilter>().apply {
    append(filter)
}

val fileDialog: FileDialog = FileDialog.builder().setFilters(filters).build()

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(
            title = "Video",
            onClose = ::exitApplication,
            defaultHeight = 600,
            defaultWidth = 800,
        ) {
            val window = LocalApplicationWindow.current

            var selectedFile by remember { mutableStateOf<File?>(null) }

            ToolbarView(
                topBar = {
                    HeaderBar()
                },
            ) {
                VerticalBox(
                    modifier = Modifier.expand().horizontalAlignment(Align.CENTER).verticalAlignment(Align.CENTER)
                        .margin(8),
                ) {
                    if (selectedFile == null) {
                        Button(
                            label = "Open…",
                            onClick = {
                                fileDialog.open(
                                    window,
                                    null,
                                ) { _: GObject?, result, _: MemorySegment? ->
                                    var file: File? = null
                                    try {
                                        file = fileDialog.openFinish(result)
                                    } catch (_: Throwable) {
                                    }
                                    if (file == null) return@open
                                    selectedFile = file
                                }
                            },
                            modifier = Modifier.cssClasses("pill", "suggested-action"),
                        )
                    } else {
                        Video(file = selectedFile)
                    }
                }
            }
        }
    }
}
