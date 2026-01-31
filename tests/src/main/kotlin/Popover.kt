import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.ToastOverlay
import io.github.compose4gtk.gtk.components.Box
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.gtk.components.Frame
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.Popover
import io.github.compose4gtk.gtk.components.PopoverScope
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.click
import io.github.compose4gtk.modifier.expand
import io.github.compose4gtk.modifier.expandHorizontally
import io.github.compose4gtk.modifier.margin
import org.gnome.adw.Toast
import org.gnome.gdk.Gdk
import org.gnome.gdk.Rectangle

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Popover", onClose = ::exitApplication, defaultWidth = 500, defaultHeight = 400) {
            ToastOverlay {
                VerticalBox {
                    HeaderBar()

                    VerticalBox(
                        spacing = 8,
                        modifier = Modifier.margin(8),
                    ) {
                        ClickablePopoverFrame(
                            label = "Left Click Me",
                            button = Gdk.BUTTON_PRIMARY,
                            onClose = {
                                addToast(
                                    Toast.builder()
                                        .setTitle("Popover closed")
                                        .setButtonLabel("Undo")
                                        .onButtonClicked { popup() }
                                        .build(),
                                )
                            },
                        ) {
                            VerticalBox {
                                Label(text = "Hello World")
                            }
                        }
                        ClickablePopoverFrame(
                            label = "Right Click Me",
                            button = Gdk.BUTTON_SECONDARY,
                            autoHide = false,
                        ) {
                            VerticalBox {
                                Button(
                                    label = "Close",
                                    onClick = { popdown() },
                                )
                            }
                        }

                        var position by remember { mutableStateOf(0 to 0) }
                        Box {
                            Popover(
                                trigger = {
                                    Frame(
                                        modifier = Modifier.expand(),
                                    ) {
                                        Label(
                                            text = "Click Anywhere",
                                            modifier = Modifier.click { _, x, y ->
                                                setPointingTo(
                                                    Rectangle(x.toInt(), y.toInt(), 0, 0),
                                                )
                                                position = x.toInt() to y.toInt()
                                                popup()
                                            },
                                        )
                                    }
                                },
                            ) {
                                Label(text = "x: ${position.first}, y: ${position.second}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClickablePopoverFrame(
    label: String,
    button: Int = Gdk.BUTTON_PRIMARY,
    autoHide: Boolean = true,
    onClose: PopoverScope.() -> Unit = {},
    content: @Composable PopoverScope.() -> Unit,
) {
    Box {
        Popover(
            trigger = {
                Frame(
                    modifier = Modifier.expandHorizontally(),
                ) {
                    Label(
                        text = label,
                        modifier = Modifier.click(
                            button = button,
                        ) {
                            popup()
                        },
                    )
                }
            },
            autoHide = autoHide,
            onClose = onClose,
            content = content,
        )
    }
}
