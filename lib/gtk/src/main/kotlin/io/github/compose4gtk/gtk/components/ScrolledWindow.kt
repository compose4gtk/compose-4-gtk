package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.CornerType
import org.gnome.gtk.PolicyType
import org.gnome.gtk.ScrolledWindow

/**
 * Creates a [org.gnome.gtk.ScrolledWindow] that makes its child scrollable.
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param horizontalScrollbarPolicy Determines when the horizontal scrollbar should appear.
 * @param verticalScrollbarPolicy Determines when the vertical scrollbar should appear.
 * @param kineticScrolling Whether the scroll window has kinetic scrolling.
 * @param minContentHeight The minimum height of the content.
 * @param maxContentHeight The maximum height of the content.
 * @param minContentWidth The minimum width of the content.
 * @param maxContentWidth The maximum width of the content.
 * @param overlayScrolling Whether the scroll window has overlay scrolling.
 * @param placement the placement of the contents with respect to the scrollbars.
 * @param propagateNaturalHeight Whether the natural height of the child should be calculated
 * and propagated through the scrolled window’s requested natural height.
 * @param propagateNaturalWidth Whether the natural width of the child should be calculated
 * and propagated through the scrolled window’s requested natural width.
 * @param content The composable content to display.
 */
@Composable
fun ScrolledWindow(
    modifier: Modifier = Modifier,
    horizontalScrollbarPolicy: PolicyType = PolicyType.AUTOMATIC,
    verticalScrollbarPolicy: PolicyType = PolicyType.AUTOMATIC,
    kineticScrolling: Boolean = true,
    minContentHeight: Int? = null,
    maxContentHeight: Int? = null,
    minContentWidth: Int? = null,
    maxContentWidth: Int? = null,
    overlayScrolling: Boolean = true,
    placement: CornerType = CornerType.TOP_LEFT,
    propagateNaturalHeight: Boolean = false,
    propagateNaturalWidth: Boolean = false,
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeWidget<ScrolledWindow>, GtkApplier>(
        factory = {
            SingleChildComposeNode(
                ScrolledWindow.builder().build(),
                set = { child = it },
            )
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(horizontalScrollbarPolicy to verticalScrollbarPolicy) { (h, v) -> this.widget.setPolicy(h, v) }
            set(kineticScrolling) { this.widget.kineticScrolling = it }
            set(minContentHeight) { this.widget.minContentHeight = it ?: -1 }
            set(maxContentHeight) { this.widget.maxContentHeight = it ?: -1 }
            set(minContentWidth) { this.widget.minContentWidth = it ?: -1 }
            set(maxContentWidth) { this.widget.maxContentWidth = it ?: -1 }
            set(overlayScrolling) { this.widget.overlayScrolling = it }
            set(placement) { this.widget.placement = it }
            set(propagateNaturalHeight) { this.widget.propagateNaturalHeight = it }
            set(propagateNaturalWidth) { this.widget.propagateNaturalWidth = it }
        },
        content = content,
    )
}
