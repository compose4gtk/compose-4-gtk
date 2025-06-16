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
    drawValue: Boolean = true,
    hasOrigin: Boolean = true,
    valuePosition: PositionType = PositionType.TOP,
    fillLevel: Double = Double.MAX_VALUE,
    flippable: Boolean = true,
    stepIncrements: Double = 0.0,
    pageIncrements: Double = 0.0,
    inverted: Boolean = false,
    showFillLevel: Boolean = false,
    marks: List<Mark> = emptyList(),
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
            set(stepIncrements) { this.widget.adjustment.stepIncrement = it }
            set(pageIncrements) { this.widget.adjustment.pageIncrement = it }
            set(inverted) { this.widget.inverted = it }
            set(showFillLevel) { this.widget.showFillLevel = it }
            set(marks.toList()) {
                this.widget.clearMarks()
                for (mark in marks) {
                    this.widget.addMark(mark.value, mark.position, mark.markup)
                }
            }
            set(value) { this.widget.adjustment.value = it }
            set(onChange) {
                this.changeValue?.disconnect()
                this.changeValue = this.widget.onChangeValue { scrollType, newValue ->
                    it(scrollType, newValue)
                    true
                }
            }
        },
    )
}
