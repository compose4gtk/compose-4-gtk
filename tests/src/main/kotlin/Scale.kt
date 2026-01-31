import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.components.HorizontalBox
import io.github.compose4gtk.gtk.components.IconButton
import io.github.compose4gtk.gtk.components.Mark
import io.github.compose4gtk.gtk.components.Scale
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.alignment
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.useGioResource
import org.gnome.gtk.Align
import org.gnome.gtk.PositionType

fun main(args: Array<String>) {
    useGioResource("resources.gresource") {
        adwApplication("my.example.hello-app", args) {
            ApplicationWindow(title = "Scale", onClose = ::exitApplication) {
                VerticalBox {
                    HeaderBar(modifier = Modifier.cssClasses("flat"))

                    var scaleValue by remember { mutableDoubleStateOf(25.5) }

                    val marks = remember {
                        mutableStateListOf(
                            Mark(15.0, PositionType.BOTTOM, "15"),
                            Mark(40.0, PositionType.BOTTOM, "40"),
                            Mark(100.0, PositionType.BOTTOM, "100"),
                        )
                    }

                    StatusPage(
                        title = "Scale",
                        description = "Slider control to select a value from a range",
                    ) {
                        VerticalBox(
                            spacing = 16,
                        ) {
                            Scale(
                                value = scaleValue,
                                onChange = { scrollType, newValue ->
                                    println(newValue)
                                    scaleValue = newValue
                                },
                                lower = -10.0,
                                upper = 110.0,
                                digits = 2,
                                drawValue = true,
                                fillLevel = 90.0,
                                stepIncrements = 10.0,
                                pageIncrements = 25.0,
                                showFillLevel = true,
                                marks = marks.toList(),
                            )

                            HorizontalBox(
                                spacing = 8,
                                modifier = Modifier.alignment(Align.CENTER),
                            ) {
                                IconButton(ImageSource.Icon("left-small-symbolic"), {
                                    val newValue = marks[1].value - 5
                                    marks[1] = Mark(newValue, marks[1].position, newValue.toInt().toString())
                                })
                                IconButton(ImageSource.Icon("right-small-symbolic"), {
                                    val newValue = marks[1].value + 5
                                    marks[1] = Mark(newValue, marks[1].position, newValue.toInt().toString())
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}
