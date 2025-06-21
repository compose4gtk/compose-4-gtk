package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.remember
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.LevelBar
import org.gnome.gtk.LevelBarMode
import org.gnome.gtk.Orientation
import kotlin.collections.mutableListOf

data class Offset(val name: String, val value: Double)

private fun LevelBar.syncOffsets(
    offsets: List<Offset>,
    minValue: Double,
    maxValue: Double,
    lastOffsetsNames: MutableList<String>,
) {
    for (name in lastOffsetsNames) this.removeOffsetValue(name)

    lastOffsetsNames.clear()

    for (offset in offsets) {
        if (offset.value in minValue..maxValue) {
            lastOffsetsNames += offset.name
            this.addOffsetValue(offset.name, offset.value)
        }
    }
}

@Composable
fun LevelBar(
    value: Double,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.HORIZONTAL,
    inverted: Boolean = false,
    maxValue: Double = 1.0,
    minValue: Double = 0.0,
    mode: LevelBarMode = LevelBarMode.CONTINUOUS,
    offsets: List<Offset> = emptyList(),
) {
    val lastOffsetsNames = remember { mutableListOf<String>() }

    ComposeNode<GtkComposeWidget<LevelBar>, GtkApplier>(
        factory = {
            LeafComposeNode(LevelBar.builder().build())
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(orientation) { this.widget.orientation = it }
            set(inverted) { this.widget.inverted = it }
            set(maxValue) {
                this.widget.maxValue = it
                this.widget.syncOffsets(offsets, minValue, it, lastOffsetsNames)
            }
            set(minValue) {
                this.widget.minValue = it
                this.widget.syncOffsets(offsets, it, maxValue, lastOffsetsNames)
            }
            set(mode) { this.widget.mode = it }
            set(offsets) {
                this.widget.syncOffsets(it, minValue, maxValue, lastOffsetsNames)
            }
            set(value) { this.widget.value = it }
        },
    )
}
