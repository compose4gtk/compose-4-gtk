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
import org.gnome.adw.CenteringPolicy
import org.gnome.adw.HeaderBar
import org.gnome.gtk.Widget

/**
 * Creates a [org.gnome.adw.HeaderBar] used as a title bar widget.
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param centeringPolicy The policy for aligning the center widget.
 * @param showEndTitleButtons Whether to show title buttons at the end of the header bar.
 * @param showStartTitleButtons Whether to show title buttons at the start of the header bar.
 * @param title Composable widget to display as the title.
 * @param startWidgets Composable widget displayed at the start of the header.
 * @param endWidgets Composable widget displayed at the end of the header.
 */
@Composable
fun HeaderBar(
    modifier: Modifier = Modifier,
    centeringPolicy: CenteringPolicy = CenteringPolicy.LOOSE,
    showEndTitleButtons: Boolean = true,
    showStartTitleButtons: Boolean = true,
    title: (@Composable () -> Unit)? = null,
    startWidgets: @Composable () -> Unit = {},
    endWidgets: @Composable () -> Unit = {},
) {
    ComposeNode<GtkComposeWidget<HeaderBar>, GtkApplier>(
        {
            VirtualComposeNodeContainer(HeaderBar.builder().build())
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(centeringPolicy) { this.widget.centeringPolicy = it }
            set(showEndTitleButtons) { this.widget.showEndTitleButtons = it }
            set(showStartTitleButtons) { this.widget.showStartTitleButtons = it }
        },
        content = {
            Pack({ packStart(it) }, startWidgets)
            if (title != null) {
                Title {
                    title()
                }
            }
            Pack({ packEnd(it) }, endWidgets)
        },
    )
}

@Composable
private fun Pack(
    packer: HeaderBar.(Widget) -> Unit,
    content: @Composable () -> Unit = {},
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        {
            VirtualComposeNode<HeaderBar> { header ->
                GtkContainerComposeNode.appendOnly(
                    header,
                    add = { packer(it) },
                    remove = { remove(it) },
                )
            }
        },
        update = {},
        content = content,
    )
}

@Composable
private fun Title(
    title: @Composable () -> Unit = {},
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        {
            VirtualComposeNode<HeaderBar> { header ->
                SingleChildComposeNode(
                    header,
                    set = { titleWidget = it },
                )
            }
        },
        update = {},
        content = title,
    )
}
