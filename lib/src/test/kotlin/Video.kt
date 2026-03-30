import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ActionRow
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.ExpanderRow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.adw.components.ToolbarView
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.gtk.components.HorizontalBox
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.ListBox
import io.github.compose4gtk.gtk.components.MediaControls
import io.github.compose4gtk.gtk.components.ScrolledWindow
import io.github.compose4gtk.gtk.components.ToggleButton
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.gtk.components.Video
import io.github.compose4gtk.gtk.components.VideoState
import io.github.compose4gtk.gtk.components.rememberVideoState
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.expand
import io.github.compose4gtk.modifier.horizontalAlignment
import io.github.compose4gtk.modifier.margin
import io.github.compose4gtk.modifier.sizeRequest
import io.github.compose4gtk.modifier.verticalAlignment
import io.github.compose4gtk.shared.components.LocalApplicationWindow
import org.gnome.gio.File
import org.gnome.gio.ListStore
import org.gnome.gobject.GObject
import org.gnome.gtk.Align
import org.gnome.gtk.FileDialog
import org.gnome.gtk.FileFilter
import org.gnome.gtk.SelectionMode
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
            defaultHeight = 800,
            defaultWidth = 1200,
        ) {
            val window = LocalApplicationWindow.current

            var selectedFile by remember { mutableStateOf<File?>(null) }

            ToolbarView(
                topBar = {
                    HeaderBar()
                },
            ) {
                VerticalBox(
                    modifier = Modifier
                        .expand()
                        .horizontalAlignment(Align.CENTER)
                        .verticalAlignment(Align.CENTER)
                        .margin(8),
                ) {
                    if (selectedFile == null) {
                        StatusPage(
                            title = "Video",
                            description = "Select a video file",
                        ) {
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
                        }
                    } else {
                        val videoState = rememberVideoState()
                        ScrolledWindow(
                            modifier = Modifier.expand(),
                            propagateNaturalWidth = true,
                            propagateNaturalHeight = true,
                        ) {
                            VerticalBox(
                                modifier = Modifier.margin(8),
                                spacing = 8,
                            ) {
                                Video(
                                    state = videoState,
                                    file = selectedFile,
                                    modifier = Modifier.sizeRequest(-1, 300),
                                )
                                MediaControls(videoState)
                                StreamInfo(videoState)
                                StreamControls(videoState)
                                Button(
                                    label = "Remove video",
                                    onClick = { selectedFile = null },
                                    modifier = Modifier.cssClasses("destructive-action").expand(false),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StreamInfo(videoState: VideoState, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    ListBox(
        modifier = modifier.cssClasses("boxed-list"),
        selectionMode = SelectionMode.NONE,
    ) {
        ExpanderRow(expanded = expanded, title = "Stream info", onExpand = { expanded = !expanded }) {
            ActionRow(title = "Duration: ${videoState.duration}")
            ActionRow(title = "Ended: ${videoState.ended}")
            ActionRow(
                title = "Error: ${videoState.error?.readMessage() ?: "no errors"}",
                modifier = Modifier.cssClasses(
                    if (videoState.error != null) listOf("error") else emptyList(),
                ),
            )
            ActionRow(title = "Has audio: ${videoState.hasAudio}")
            ActionRow(title = "Has video: ${videoState.hasVideo}")
            ActionRow(title = "Loop: ${videoState.loop}")
            ActionRow(title = "Muted: ${videoState.muted}")
            ActionRow(title = "Volume: %.1f".format(videoState.volume))
            ActionRow(title = "Playing: ${videoState.playing}")
            ActionRow(title = "Prepared: ${videoState.prepared}")
            ActionRow(title = "Seekable: ${videoState.seekable}")
            ActionRow(title = "Seeking: ${videoState.seeking}")
            ActionRow(title = "Timestamp: ${videoState.timestamp}")
        }
    }
}

@Composable
fun StreamControls(videoState: VideoState, modifier: Modifier = Modifier) {
    VerticalBox(modifier = modifier) {
        Label(text = "Play control")
        HorizontalBox(
            modifier = Modifier.expand(),
            spacing = 8,
            homogeneous = true,
        ) {
            Button(label = "Play", onClick = { videoState.play() })
            Button(label = "Pause", onClick = { videoState.pause() })
        }
    }
    VerticalBox {
        Label(text = "Volume control")
        HorizontalBox(
            modifier = Modifier.expand(),
            spacing = 8,
            homogeneous = true,
        ) {
            Button(
                label = "Down",
                onClick = {
                    val currentVolume = videoState.volume
                    videoState.volume = (currentVolume - 0.1).coerceIn(0.0, 1.0)
                },
            )
            Button(
                label = "Up",
                onClick = {
                    val currentVolume = videoState.volume
                    videoState.volume = (currentVolume + 0.1).coerceIn(0.0, 1.0)
                },
            )
        }
    }
    ToggleButton(
        label = "Loop",
        active = videoState.loop,
        onToggle = { videoState.loop = !videoState.loop },
    )
    ToggleButton(
        label = "Mute",
        active = videoState.muted,
        onToggle = { videoState.muted = !videoState.muted },
    )
}
