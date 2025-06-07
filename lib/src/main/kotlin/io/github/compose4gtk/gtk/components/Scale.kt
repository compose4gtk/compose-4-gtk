package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.remember
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.gtk.Orientation
import org.gnome.gtk.PositionType
import org.gnome.gtk.Range
import org.gnome.gtk.Scale
import org.gnome.gtk.ScrollType
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

private class GtkScaleComposeNode(gObject: Scale) : LeafComposeNode<Scale>(gObject) {
    var changeValue: SignalConnection<Range.ChangeValueCallback>? = null
}

data class Mark(val value: Double, val position: PositionType, val markup: String? = null)

@Composable
fun Scale(
    value: Double,
    onChange: (ScrollType, Double) -> Unit,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.HORIZONTAL,
    lower: Double = 0.0,
    upper: Double = 100.0,
    digits: Int = 1,
    drawValue: Boolean = false,
    hasOrigin: Boolean = true,
    valuePosition: PositionType = PositionType.TOP,
    fillLevel: Double = Double.MAX_VALUE,
    flippable: Boolean = true,
    increments: Double = 0.0,
    inverted: Boolean = false,
    restrictToFillLevel: Boolean = true,
    roundDigits: Int = -1,
    showFillLevel: Boolean = false,
    marks: Array<Mark> = emptyArray(),
) {
    val scale = remember { Scale.builder().build() }

    ComposeNode<GtkScaleComposeNode, GtkApplier>(
        factory = {
            GtkScaleComposeNode(scale)
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(orientation) { this.widget.orientation = it }
            set(lower) { this.widget.adjustment.lower = it }
            set(upper) { this.widget.adjustment.upper = it }
            set(digits) { this.widget.digits = it }
            set(drawValue) { this.widget.drawValue = it }
            set(hasOrigin) { this.widget.hasOrigin = it }
            set(valuePosition) { this.widget.valuePos = it }
            set(fillLevel) { this.widget.fillLevel = it }
            set(flippable) { this.widget.flippable = it }
            set(increments) { this.widget.setIncrements(it, abs(lower - upper)) }
            set(inverted) { this.widget.inverted = it }
            set(restrictToFillLevel) { this.widget.restrictToFillLevel = it }
            set(roundDigits) { this.widget.roundDigits = it }
            set(showFillLevel) { this.widget.showFillLevel = it }
            set(marks) {
                this.widget.clearMarks()
                for (mark in marks) {
                    this.widget.addMark(mark.value, mark.position, mark.markup)
                }
            }
            set(value) { this.widget.adjustment.value = it }
            set(onChange) {
                this.changeValue?.disconnect()
                this.changeValue = this.widget.onChangeValue { scrollType, newValue ->
                    val max = upper.coerceAtMost(fillLevel)
                    var correctedValue = newValue.coerceIn(lower, max)

                    if (roundDigits >= 0) {
                        val factor = 10.0.pow(roundDigits.toDouble())
                        correctedValue = (correctedValue * factor).roundToInt() / factor
                    }

                    it(scrollType, correctedValue)
                    true
                }
            }
        },
    )
}
