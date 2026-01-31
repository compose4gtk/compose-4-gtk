package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Switch
import org.javagi.gobject.SignalConnection

private class GtkSwitchComposeNode(
    gObject: Switch,
) : LeafComposeNode<Switch>(gObject) {
    var onStateSet: SignalConnection<Switch.StateSetCallback>? = null
}

/**
 * Creates a [org.gnome.gtk.Switch] used for toggle states.
 *
 * @param active The state of the switch.
 * @param onToggle Callback triggered when the switch is toggled.
 * @param modifier Compose [Modifier] for layout and styling.
 */
@Composable
fun Switch(
    active: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pendingChange by remember { mutableIntStateOf(0) }
    ComposeNode<GtkSwitchComposeNode, GtkApplier>({
        GtkSwitchComposeNode(Switch.builder().build())
    }) {
        set(active to pendingChange) { (active, _) ->
            this.onStateSet?.block()
            this.widget.state = active
            this.widget.active = active
            this.onStateSet?.unblock()
        }
        set(onToggle) {
            this.onStateSet?.disconnect()
            this.onStateSet = this.widget.onStateSet { newState ->
                pendingChange += 1
                it(newState)
                true
            }
        }
        set(modifier) { applyModifier(it) }
    }
}
