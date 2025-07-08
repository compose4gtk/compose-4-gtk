package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeNode
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.VirtualComposeNode
import io.github.compose4gtk.VirtualComposeNodeContainer
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.PreferencesGroup

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
    ComposeNode<GtkComposeWidget<PreferencesGroup>, GtkApplier>(
        factory = {
            VirtualComposeNodeContainer(PreferencesGroup.builder().build())
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
            VirtualComposeNode<PreferencesGroup> { preferencesGroup ->
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
            VirtualComposeNode<PreferencesGroup> { preferencesGroup ->
                SingleChildComposeNode(
                    preferencesGroup,
                    set = { add(it) },
                )
            }
        },
        update = {},
        content = content,
    )
}
