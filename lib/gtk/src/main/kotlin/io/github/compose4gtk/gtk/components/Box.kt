package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.GtkContainerComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Orientation
import org.gnome.gtk.Widget
import org.gnome.gtk.Box as GtkBox

private class GtkBoxComposeNode(gObject: GtkBox) : GtkContainerComposeNode<GtkBox>(gObject) {
    override fun addNode(index: Int, child: GtkComposeWidget<Widget>) {
        when (index) {
            children.size -> widget.append(child.widget)
            0 -> widget.insertChildAfter(child.widget, null)
            else -> widget.insertChildAfter(child.widget, children[index - 1])
        }
        super.addNode(index, child)
    }

    override fun removeNode(index: Int) {
        val child = children[index]
        widget.remove(child)
        super.removeNode(index)
    }

    override fun clearNodes() {
        children.forEach { widget.remove(it) }
        super.clearNodes()
    }
}

/**
 * A vertically aligned [Box]
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param spacing The space between the child widgets.
 * @param homogeneous Whether all children are given equal space in the box.
 * @param content The composable content to display.
 */
@Composable
fun VerticalBox(
    modifier: Modifier = Modifier,
    spacing: Int = 0,
    homogeneous: Boolean = false,
    content: @Composable () -> Unit,
) {
    Box(modifier, Orientation.VERTICAL, spacing, homogeneous, content)
}

/**
 * A horizontally aligned [Box]
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param spacing The space between the child widgets.
 * @param homogeneous Whether all children are given equal space in the box.
 * @param content The composable content to display.
 */
@Composable
fun HorizontalBox(
    modifier: Modifier = Modifier,
    spacing: Int = 0,
    homogeneous: Boolean = false,
    content: @Composable () -> Unit,
) {
    Box(modifier, Orientation.HORIZONTAL, spacing, homogeneous, content)
}

/**
 * Creates a [org.gnome.gtk.Box] that arranges child widgets into a single row or column.
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param orientation The axis on which the children are aligned.
 * @param spacing The space between the child widgets.
 * @param homogeneous Whether all children are given equal space in the box.
 * @param content The composable content to display.
 */
@Composable
fun Box(
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.HORIZONTAL,
    spacing: Int = 0,
    homogeneous: Boolean = false,
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeWidget<GtkBox>, GtkApplier>(
        factory = {
            GtkBoxComposeNode(GtkBox.builder().build())
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(homogeneous) { this.widget.homogeneous = it }
            set(orientation) { this.widget.orientation = it }
            set(spacing) { this.widget.spacing = it }
        },
        content = content,
    )
}
