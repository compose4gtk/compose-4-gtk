import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.application
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.BottomSheet
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.HorizontalClamp
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.gtk.components.ToggleButton
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses

fun main(args: Array<String>) {
    application("my.example.hello-app", args) {
        ApplicationWindow(
            title = "Bottom Sheet",
            onClose = ::exitApplication,
            defaultWidth = 800,
            defaultHeight = 640,
        ) {
            var open by remember { mutableStateOf(false) }
            var canClose by remember { mutableStateOf(true) }
            var canOpen by remember { mutableStateOf(true) }
            var fullWidth by remember { mutableStateOf(true) }
            var modal by remember { mutableStateOf(true) }
            var showDragHandle by remember { mutableStateOf(true) }

            BottomSheet(
                open = open,
                canClose = canClose,
                canOpen = canOpen,
                fullWidth = fullWidth,
                modal = modal,
                showDragHandle = showDragHandle,
                sheet = {
                    StatusPage(
                        title = "Sheet",
                    ) {
                        HorizontalClamp {
                            VerticalBox {
                                ToggleButton(
                                    label = "Can Close",
                                    active = canClose,
                                    onToggle = {
                                        canClose = !canClose
                                    }
                                )
                                ToggleButton(
                                    label = "Show Drag Handle",
                                    active = showDragHandle,
                                    onToggle = {
                                        showDragHandle = !showDragHandle
                                    }
                                )
                                ToggleButton(
                                    label = "Modal",
                                    active = modal,
                                    onToggle = {
                                        modal = !modal
                                    }
                                )
                            }
                        }
                    }
                }
            ) {
                VerticalBox {
                    HeaderBar(modifier = Modifier.cssClasses("flat"))

                    StatusPage(
                        title = "Bottom Sheet",
                        description = "Display content with a bottom sheet",
                    ) {
                        HorizontalClamp {
                            VerticalBox {
                                ToggleButton(
                                    label = "Full Width",
                                    active = fullWidth,
                                    onToggle = {
                                        fullWidth = !fullWidth
                                    },
                                )
                                ToggleButton(
                                    label = "Can Open",
                                    active = canOpen,
                                    onToggle = {
                                        canOpen = !canOpen
                                    }
                                )
                                ToggleButton(
                                    label = "Open",
                                    active = open,
                                    onToggle = {
                                        open = !open
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}