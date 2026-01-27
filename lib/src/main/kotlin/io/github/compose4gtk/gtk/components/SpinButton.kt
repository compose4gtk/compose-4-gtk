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
import org.gnome.gtk.Adjustment
import org.javagi.gobject.SignalConnection
import org.gnome.gtk.SpinButton as GtkSpinButton

private class GtkSpinButtonComposeNode(gObject: GtkSpinButton) : LeafComposeNode<GtkSpinButton>(gObject) {
    var onActivate: SignalConnection<GtkSpinButton.ActivateCallback>? = null
    var onValueChange: SignalConnection<GtkSpinButton.ValueChangedCallback>? = null
}

/**
 * Creates a [org.gnome.gtk.SpinButton] to display numeric values.
 *
 * @param value Number displayed in the component.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param onActivate Callback triggered when the component is activated (pressing "enter").
 * @param onValueChange Callback triggered when the value changes.
 * @param lower Minimum allowed value.
 * @param upper Maximum allowed value.
 * @param stepIncrement How much the value increments or decrements by when using the buttons or the arrow keys.
 * @param pageIncrement How much the value increments or decrements by when using "PageUp" or "PageDown".
 * @param climbRate The acceleration rate when holding down an arrow button or key.
 * @param digits The number of decimal places to display.
 * @param numeric Whether non-numeric characters are ignored.
 */
@Composable
fun SpinButton(
    value: Double,
    modifier: Modifier = Modifier,
    onActivate: () -> Unit = {},
    onValueChange: (Double) -> Unit = {},
    lower: Double = 0.0,
    upper: Double = 0.0,
    stepIncrement: Double = 0.0,
    pageIncrement: Double = 0.0,
    climbRate: Double = 0.0,
    digits: Int = 0,
    numeric: Boolean = false,
) {
    var pendingChange by remember { mutableIntStateOf(0) }
    val adjustment = remember { Adjustment() }

    ComposeNode<GtkSpinButtonComposeNode, GtkApplier>(
        factory = {
            GtkSpinButtonComposeNode(GtkSpinButton.builder().setAdjustment(adjustment).build())
        },
        update = {
            set(value to pendingChange) {
                this.onValueChange?.block()
                this.widget.value = value
                this.onValueChange?.unblock()
            }
            set(modifier) { applyModifier(it) }
            set(onActivate) {
                this.onActivate?.disconnect()
                this.onActivate = this.widget.onActivate {
                    onActivate()
                }
            }
            set(onValueChange) {
                this.onValueChange?.disconnect()
                this.onValueChange = this.widget.onValueChanged {
                    pendingChange += 1
                    onValueChange(this.widget.value)
                }
            }
            set(lower) { this.widget.adjustment.lower = it }
            set(upper) { this.widget.adjustment.upper = it }
            set(stepIncrement) { this.widget.adjustment.stepIncrement = it }
            set(pageIncrement) { this.widget.adjustment.pageIncrement = it }
            set(climbRate) { this.widget.climbRate = it }
            set(digits) { this.widget.digits = it }
            set(numeric) { this.widget.numeric = it }
        },
    )
}
