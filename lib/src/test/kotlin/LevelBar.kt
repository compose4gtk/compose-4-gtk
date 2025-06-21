import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.HorizontalClamp
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.gtk.components.HorizontalBox
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.LevelBar
import io.github.compose4gtk.gtk.components.Offset
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.alignment
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.useGioResource
import org.gnome.gtk.Align
import org.gnome.gtk.LevelBarMode
import org.gnome.gtk.Orientation

val defaultOffsets = listOf(
    Offset("full", 10.0),
    Offset("half", 5.0),
    Offset("low", 2.5),
)

fun main(args: Array<String>) {
    useGioResource("resources.gresource") {
        adwApplication("my.example.hello-app", args) {
            ApplicationWindow(title = "Level Bar", onClose = ::exitApplication) {
                VerticalBox {
                    HeaderBar(modifier = Modifier.cssClasses("flat"))

                    var level by remember { mutableDoubleStateOf(5.0) }
                    val offsets = remember {
                        mutableStateListOf<Offset>().apply { addAll(defaultOffsets) }
                    }

                    StatusPage(
                        title = "Level Bar",
                        description = "A bar widget used as a level indicator",
                    ) {
                        VerticalBox(
                            spacing = 16,
                        ) {
                            LevelBar(
                                value = level,
                                maxValue = 10.0,
                                mode = LevelBarMode.CONTINUOUS,
                                offsets = offsets.toList(),
                            )

                            HorizontalClamp(
                                maximumSize = 100,
                            ) {
                                LevelBar(
                                    value = level,
                                    orientation = Orientation.VERTICAL,
                                    inverted = true,
                                    maxValue = 10.0,
                                    mode = LevelBarMode.DISCRETE,
                                    offsets = offsets.toList(),
                                )
                            }

                            HorizontalBox(
                                modifier = Modifier.alignment(Align.CENTER),
                                spacing = 8,
                            ) {
                                Button("-", {
                                    if (level > 0.0) {
                                        level--
                                    }
                                })
                                Label(level.toString())
                                Button("+", {
                                    if (level < 10.0) {
                                        level++
                                    }
                                })
                            }

                            HorizontalBox(
                                modifier = Modifier.alignment(Align.CENTER),
                                spacing = 8,
                            ) {
                                Button("Remove offset", {
                                    if (offsets.isNotEmpty()) {
                                        offsets.removeLast()
                                    }
                                })
                                Button("Reset", {
                                    offsets.clear()
                                    offsets.addAll(defaultOffsets)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}
