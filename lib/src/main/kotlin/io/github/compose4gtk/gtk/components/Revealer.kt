package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Revealer
import org.gnome.gtk.RevealerTransitionType

/**
 * Creates a [org.gnome.gtk.Revealer] that animates the transition of its child's visibility.
 *
 * @param reveal Whether the child is visible or invisible.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param transitionType The type of animation that will be used.
 * @param transitionDuration The duration, in milliseconds, of the animation.
 * @param content The composable content to display.
 */
@Composable
fun Revealer(
    reveal: Boolean,
    modifier: Modifier = Modifier,
    transitionType: RevealerTransitionType = RevealerTransitionType.NONE,
    transitionDuration: Int = 0,
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeWidget<Revealer>, GtkApplier>(
        factory = {
            SingleChildComposeNode(Revealer.builder().build()) {
                child = it
            }
        },
        update = {
            set(reveal) { this.widget.revealChild = it }
            set(modifier) { applyModifier(it) }
            set(transitionType) { this.widget.transitionType = it }
            set(transitionDuration) { this.widget.transitionDuration = it }
        },
        content = content,
    )
}
