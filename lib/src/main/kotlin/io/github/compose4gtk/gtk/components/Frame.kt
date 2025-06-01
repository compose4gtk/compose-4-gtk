package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeNode
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.VirtualComposeNode
import io.github.compose4gtk.VirtualComposeNodeContainer
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Frame

@Composable
fun Frame(
    modifier: Modifier = Modifier,
    labelXAlign: Float = 0f,
    label: (@Composable () -> Unit)? = null,
    child: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeWidget<Frame>, GtkApplier>(
        factory = {
            VirtualComposeNodeContainer(Frame.builder().build())
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(labelXAlign) { this.widget.labelAlign = labelXAlign }
        },
        content = {
            if (label != null) {
                Label(label)
            }
            Child(child)
        },
    )
}

@Composable
private fun Child(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<Frame> { overlay ->
                SingleChildComposeNode(overlay) { child = it }
            }
        },
        update = { },
        content = content,
    )
}

@Composable
private fun Label(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<Frame> { overlay ->
                SingleChildComposeNode(overlay) { labelWidget = it }
            }
        },
        update = { },
        content = content,
    )
}
