package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Adjustment
import org.javagi.gobject.SignalConnection
import org.gnome.adw.SpinRow as AdwSpinRow
import org.gnome.gtk.SpinButton as GtkSpinButton

private class AdwSpinRowComposeNode(gObject: AdwSpinRow) : LeafComposeNode<AdwSpinRow>(gObject) {
    var valueChanged: SignalConnection<GtkSpinButton.ValueChangedCallback>? = null
    var onActivate: SignalConnection<GtkSpinButton.ActivateCallback>? = null
}

/**
 * Creates a [org.gnome.adw.SpinRow] that displays numeric values
 *
 * [org.gnome.adw.SpinRow] is a child of [org.gnome.adw.ActionRow] and [org.gnome.adw.PreferencesRow] which are usually
 * used for preferences/settings inside and application.
 *
 * @param value Number displayed in the component.
 * @param title The title for this row.
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
 * @param activatable Whether the component can be activated.
 * @param titleSelectable Whether the title is selectable.
 * @param useMarkup Whether to use Pango markup for the title and subtitle.
 * @param useUnderline Whether an embedded underline in the title or subtitle indicates a mnemonic.
 * @param subtitle The subtitle for this row.
 * @param subtitleLines The number of lines at the end of which the subtitle label will be ellipsized.
 * @param subtitleSelectable Whether the subtitle is selectable.
 * @param titleLines The number of lines at the end of which the title label will be ellipsized.
 */
@Composable
fun SpinRow(
    value: Double,
    title: String,
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
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    subtitle: String? = null,
    subtitleLines: Int = 0,
    subtitleSelectable: Boolean = false,
    titleLines: Int = 0,
) {
    val adjustment = remember {
        Adjustment.builder()
            .setValue(value)
            .setLower(lower)
            .setUpper(upper)
            .setStepIncrement(stepIncrement)
            .setPageIncrement(pageIncrement)
            .build()
    }
    val spinRow = remember { AdwSpinRow.builder().setAdjustment(adjustment).build() }
    val spinButton = remember { spinRow.firstChild.lastChild.firstChild as GtkSpinButton }
    var pendingChange by remember { mutableIntStateOf(0) }

    BaseActionRow(
        creator = { AdwSpinRowComposeNode(spinRow) },
        updater = {
            set(value to pendingChange) { (value, _) ->
                this.valueChanged?.block()
                this.widget.value = value
                this.valueChanged?.unblock()
            }
            set(onActivate) {
                this.onActivate?.disconnect()
                this.onActivate = spinButton.onActivate {
                    pendingChange += 1
                    onActivate()
                }
            }
            set(onValueChange) {
                this.valueChanged?.disconnect()
                this.valueChanged = spinButton.onValueChanged {
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
        title = title,
        modifier = modifier,
        activatable = activatable,
        titleSelectable = titleSelectable,
        useMarkup = useMarkup,
        useUnderline = useUnderline,
        subtitle = subtitle,
        subtitleLines = subtitleLines,
        subtitleSelectable = subtitleSelectable,
        titleLines = titleLines,
    )
}
