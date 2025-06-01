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
