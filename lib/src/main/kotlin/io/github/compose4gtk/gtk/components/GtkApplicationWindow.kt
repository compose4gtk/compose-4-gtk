package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.shared.components.InitializeApplicationWindow
import org.gnome.gtk.ApplicationWindow
import org.gnome.gtk.CssProvider

/**
 * Creates a [org.gnome.gtk.ApplicationWindow], a freeform application window.
 *
 * @param title The title of the window.
 * @param onClose Callback triggered when the window is closed.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param styles A list of [CssProvider] used to style the window.
 * @param decorated Whether the window should have a frame.
 * @param defaultHeight The default height of the window.
 * @param defaultWidth The default width of the window.
 * @param deletable Whether the window frame should have a close button.
 * @param fullscreen Whether the window is currently fullscreen.
 * @param maximized Whether the window is currently maximized.
 * @param handleMenubarAccel Whether the window frame should handle `F10` for activating menu bars.
 * @param modal Determines if the window is a modal.
 * @param resizable Whether the window can be resized.
 * @param init Function called when the component is initialized.
 * @param content The composable content to display.
 */
@Composable
fun GtkApplicationWindow(
    title: String?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    styles: List<CssProvider> = emptyList(),
    decorated: Boolean = true,
    defaultHeight: Int = 0,
    defaultWidth: Int = 0,
    deletable: Boolean = true,
    fullscreen: Boolean = false,
    maximized: Boolean = false,
    handleMenubarAccel: Boolean = true,
    modal: Boolean = false,
    resizable: Boolean = true,
    init: ApplicationWindow.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    InitializeApplicationWindow(
        builder = {
            ApplicationWindow.builder()
        },
        setContent = { this.child = it },
        title = title,
        modifier = modifier,
        styles = styles,
        deletable = deletable,
        onClose = onClose,
        decorated = decorated,
        defaultHeight = defaultHeight,
        defaultWidth = defaultWidth,
        fullscreen = fullscreen,
        maximized = maximized,
        handleMenubarAccel = handleMenubarAccel,
        modal = modal,
        resizable = resizable,
        init = init,
        content = { content() },
    )
}
