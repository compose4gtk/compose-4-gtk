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
import org.gnome.adw.ToolbarStyle
import org.gnome.adw.ToolbarView
import org.gnome.gtk.Widget

/**
 * Creates a [org.gnome.adw.ToolbarView] that contains a page, as well as top and/or bottom bars.
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param topBarStyle Appearance of the top bars.
 * @param revealTopBars Whether top bars are revealed.
 * @param extendContentToTopEdge Whether the content widget can extend behind top bars.
 * @param topBar Composable widget displayed as the top bar.
 * @param bottomBarStyle Appearance of the bottom bars.
 * @param revealBottomBars Whether bottom bars are visible.
 * @param extendContentToBottomEdge Whether the content widget can extend behind bottom bars.
 * @param bottomBar Composable widget displayed as the bottom bar.
 * @param content The composable content to display inside the view.
 */
@Composable
fun ToolbarView(
    modifier: Modifier = Modifier,
    // Top  bar
    topBarStyle: ToolbarStyle = ToolbarStyle.FLAT,
    revealTopBars: Boolean = true,
    extendContentToTopEdge: Boolean = false,
    topBar: @Composable () -> Unit = {},
    // Bottom bar
    bottomBarStyle: ToolbarStyle = ToolbarStyle.FLAT,
    revealBottomBars: Boolean = true,
    extendContentToBottomEdge: Boolean = false,
    bottomBar: @Composable () -> Unit = {},
    // Content
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeWidget<ToolbarView>, GtkApplier>(
        {
            VirtualComposeNodeContainer(ToolbarView.builder().build())
        },
        update = {
            set(modifier) { applyModifier(it) }
            // Top bar
            set(topBarStyle) { this.widget.topBarStyle = it }
            set(revealTopBars) { this.widget.revealTopBars = it }
            set(extendContentToTopEdge) { this.widget.extendContentToTopEdge = it }
            // Bottom bar
            set(bottomBarStyle) { this.widget.bottomBarStyle = it }
            set(revealBottomBars) { this.widget.revealBottomBars = it }
            set(extendContentToBottomEdge) { this.widget.extendContentToBottomEdge = it }
        },
        content = {
            ToolbarViewBar({ addTopBar(it) }, topBar)
            ToolbarViewContent(content)
            ToolbarViewBar({ addBottomBar(it) }, bottomBar)
        },
    )
}

@Composable
private fun ToolbarViewBar(
    appendBar: ToolbarView.(Widget) -> Unit,
    content: @Composable () -> Unit = {},
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        {
            VirtualComposeNode<ToolbarView> { toolbar ->
                GtkContainerComposeNode.appendOnly(
                    toolbar,
                    add = { appendBar(it) },
                    remove = { remove(it) },
                )
            }
        },
        update = {},
        content = content,
    )
}

@Composable
private fun ToolbarViewContent(
    content: @Composable () -> Unit = {},
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        {
            VirtualComposeNode<ToolbarView> { toolbar ->
                SingleChildComposeNode(
                    widget = toolbar,
                    set = { this.content = it },
                )
            }
        },
        update = {},
        content = content,
    )
}
