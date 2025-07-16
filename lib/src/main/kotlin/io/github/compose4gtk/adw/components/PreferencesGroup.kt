package io.github.compose4gtk.adw.components

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
import org.gnome.gtk.Widget
import org.gnome.adw.PreferencesGroup as AdwPreferencesGroup

private class AdwPreferencesGroupContentComposeNode(gObject: AdwPreferencesGroup) :
    GtkContainerComposeNode<AdwPreferencesGroup>(gObject) {
    override fun addNode(index: Int, child: GtkComposeWidget<Widget>) {
        when (index) {
            children.size -> widget.add(child.widget)
            0 -> widget.insertAfter(child.widget, null)
            else -> widget.insertAfter(child.widget, children[index - 1])
        }
        super.addNode(index, child)
    }

    override fun removeNode(index: Int) {
        val child = children[index]
        widget.remove(child)
        super.removeNode(index)
    }

    override fun clearNodes() {
        children.forEach { widget.remove(it) }
        super.clearNodes()
    }
}

/**
 * Creates a [org.gnome.adw.PreferencesGroup] used to group [org.gnome.adw.PreferencesRow] widgets.
 *
 * @param title The title of the group.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param description The description of the group.
 * @param headerSuffix Composable component displayed after the title and description.
 * @param separateRows Whether the rows are separated.
 * @param content The composable components used as rows inside the group.
 */
@Composable
fun PreferencesGroup(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    headerSuffix: @Composable () -> Unit = {},
    separateRows: Boolean = false,
    content: @Composable () -> Unit = {},
) {
    ComposeNode<GtkComposeWidget<AdwPreferencesGroup>, GtkApplier>(
        factory = {
            VirtualComposeNodeContainer(AdwPreferencesGroup())
        },
        update = {
            set(title) { this.widget.title = it }
            set(modifier) { applyModifier(it) }
            set(description) { this.widget.description = it }
            set(separateRows) { this.widget.separateRows = it }
        },
        content = {
            HeaderSuffix {
                headerSuffix()
            }
            Content {
                content()
            }
        },
    )
}

@Composable
private fun HeaderSuffix(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<AdwPreferencesGroup> { preferencesGroup ->
                SingleChildComposeNode(
                    preferencesGroup,
                    set = { headerSuffix = it },
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
            VirtualComposeNode<AdwPreferencesGroup> { preferencesGroup ->
                AdwPreferencesGroupContentComposeNode(
                    preferencesGroup,
                )
            }
        },
        update = {},
        content = content,
    )
}
