package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.LevelBar
import org.gnome.gtk.LevelBarMode
import org.gnome.gtk.Orientation

data class Offset(val name: String, val value: Double)

@Composable
fun LevelBar(
    value: Double,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.HORIZONTAL,
    inverted: Boolean = false,
    maxValue: Double = 1.0,
    minValue: Double = 0.0,
    mode: LevelBarMode = LevelBarMode.CONTINUOUS,
    offsets: Array<Offset> = emptyArray(),
) {
    ComposeNode<GtkComposeWidget<LevelBar>, GtkApplier>(
        factory = {
            LeafComposeNode(LevelBar.builder().build())
        },
        update = {
            set(value) { this.widget.value = it }
            set(modifier) { applyModifier(it) }
            set(orientation) { this.widget.orientation = it }
            set(inverted) { this.widget.inverted = it }
            set(maxValue) { this.widget.maxValue = it }
            set(minValue) { this.widget.minValue = it }
            set(mode) { this.widget.mode = it }
            set(offsets) {
                for (offset in offsets) {
                    if (offset.value in minValue..maxValue) {
                        this.widget.addOffsetValue(offset.name, offset.value)
                    }
                }
            }
        },
    )
}
