package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Spinner as GtkSpinner

/**
 * Creates a [org.gnome.gtk.Spinner] that displays an icon-size spinning animation.
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param spinning Whether the spinner should be spinning.
 */
@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    spinning: Boolean = false,
) {
    ComposeNode<GtkComposeWidget<GtkSpinner>, GtkApplier>({
        LeafComposeNode(GtkSpinner.builder().build())
    }) {
        set(modifier) { applyModifier(it) }
        set(spinning) { this.widget.spinning = it }
    }
}
