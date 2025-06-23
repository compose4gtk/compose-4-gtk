package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeNode
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.VirtualComposeNode
import io.github.compose4gtk.VirtualComposeNodeContainer
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.BottomSheet

/**
 * A bottom sheet with an optional bottom bar.
 *
 * @param open Whether the sheet is currently opened.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param align Determines where the sheet is placed along the bottom edge.
 * @param canClose Whether it can be closed by clicking outside the sheet or by dragging the handle.
 * @param canOpen Whether it can be opened by clicking or dragging the handle.
 * @param fullWidth Takes the whole width of its container.
 * @param modal Dims the background and makes it inaccessible while the sheet is opened.
 * @param showDragHandle Whether the drag handle is currently shown.
 * @param onClose Callback triggered when the sheet is closed.
 * @param onOpen Callback triggered when the sheet is opened.
 * @param bottomBar A composable widget displayed as the drag handle.
 * @param sheet A composable widget displayed inside the sheet.
 * @param content A composable widget that's displayed underneath the sheet.
 */
@Composable
fun BottomSheet(
    open: Boolean,
    modifier: Modifier = Modifier,
    align: Float = 0.5f,
    canClose: Boolean = true,
    canOpen: Boolean = true,
    fullWidth: Boolean = true,
    modal: Boolean = true,
    showDragHandle: Boolean = true,
    onClose: () -> Unit = {},
    onOpen: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    sheet: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val bottomSheet = remember { BottomSheet.builder().build() }
    var lastOpen by remember { mutableStateOf(open) }

    ComposeNode<GtkComposeWidget<BottomSheet>, GtkApplier>(
        factory = {
            VirtualComposeNodeContainer(bottomSheet)
        },
        update = {
            set(open) { this.widget.open = it }
            set(modifier) { applyModifier(it) }
            set(align) { this.widget.align = it }
            set(canClose) { this.widget.canClose = it }
            set(canOpen) { this.widget.canOpen = it }
            set(fullWidth) { this.widget.fullWidth = it }
            set(modal) { this.widget.modal = it }
            set(showDragHandle) { this.widget.showDragHandle = it }

            if (lastOpen != open) {
                bottomSheet.open = open
                lastOpen = open
            }

            bottomSheet.onNotify("open") {
                val currentWidgetOpen = bottomSheet.open
                if (currentWidgetOpen != lastOpen) {
                    if (currentWidgetOpen) {
                        onOpen()
                    } else {
                        onClose()
                    }
                    bottomSheet.open = open
                }
            }
        },
        content = {
            BottomBar {
                bottomBar()
            }
            Sheet {
                sheet()
            }
            Content {
                content()
            }
        },
    )
}

@Composable
private fun BottomBar(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<BottomSheet> { bottomSheet ->
                SingleChildComposeNode(
                    bottomSheet,
                    set = { bottomBar = it },
                )
            }
        },
        update = {},
        content = content,
    )
}

@Composable
private fun Sheet(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<BottomSheet> { bottomSheet ->
                SingleChildComposeNode(
                    bottomSheet,
                    set = { sheet = it },
                )
            }
        },
        update = {},
        content = content,
    )
}

@Composable
private fun Content(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<BottomSheet> { bottomSheet ->
                SingleChildComposeNode(
                    bottomSheet,
                    set = { setContent(it) },
                )
            }
        },
        update = {},
        content = content,
    )
}
