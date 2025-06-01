package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeNode
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.GtkContainerComposeNode
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.VirtualComposeNode
import io.github.compose4gtk.VirtualComposeNodeContainer
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Adjustment
import org.gnome.gtk.ListBox
import org.gnome.gtk.SelectionMode
import org.gnome.gtk.Widget

private class GtkListBoxComposeNode(gObject: ListBox) : GtkContainerComposeNode<ListBox>(gObject) {
    override fun addNode(index: Int, child: GtkComposeWidget<Widget>) {
        widget.insert(child.widget, index)
        super.addNode(index, child)
    }

    override fun removeNode(index: Int) {
        widget.remove(children[index])
        super.removeNode(index)
    }

    override fun clearNodes() {
        widget.removeAll()
        super.clearNodes()
    }
}

/**
 * TODO:
 *  - filter func
 *  - header func
 *  - placeholder
 *  - sort func
 *  - selections
 */
@Composable
fun ListBox(
    modifier: Modifier = Modifier,
    activateOnSingleClick: Boolean = true,
    adjustment: Adjustment? = null,
    selectionMode: SelectionMode = SelectionMode.SINGLE,
    showSeparators: Boolean = false,
    placeholder: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeWidget<ListBox>, GtkApplier>(
        factory = {
            VirtualComposeNodeContainer(ListBox.builder().build())
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(activateOnSingleClick) { this.widget.activateOnSingleClick = activateOnSingleClick }
            set(adjustment) { this.widget.adjustment = adjustment }
            set(selectionMode) { this.widget.selectionMode = selectionMode }
            set(showSeparators) { this.widget.showSeparators = showSeparators }
        },
        content = {
            ListBoxPlaceHolder(placeholder)
            ListBoxContent(content)
        },
    )
}

@Composable
private fun ListBoxPlaceHolder(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<ListBox> { listBox ->
                SingleChildComposeNode(listBox) {
                    setPlaceholder(it)
                }
            }
        },
        update = {},
        content = content,
    )
}

@Composable
private fun ListBoxContent(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<ListBox> { listBox ->
                GtkListBoxComposeNode(listBox)
            }
        },
        update = {},
        content = content,
    )
}
