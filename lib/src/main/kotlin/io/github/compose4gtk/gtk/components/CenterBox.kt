package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeNode
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.VirtualComposeNode
import io.github.compose4gtk.VirtualComposeNodeContainer
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.BaselinePosition
import org.gnome.gtk.CenterBox
import org.gnome.gtk.Orientation
import org.gnome.gtk.Widget

/**
 * Creates a [org.gnome.gtk.CenterBox] that arranges three children in a row,
 * keeping the middle child centered as well as possible.
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param orientation The axis on which the children are aligned.
 * @param start The start composable widget.
 * @param end The end composable widget.
 * @param baselinePosition The baseline position of the center box.
 * @param shrinkCenterLast Whether to shrink the center widget after other children.
 * @param center The center composable widget.
 */
@Composable
fun CenterBox(
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.HORIZONTAL,
    start: @Composable () -> Unit = {},
    end: @Composable () -> Unit = {},
    baselinePosition: BaselinePosition = BaselinePosition.CENTER,
    shrinkCenterLast: Boolean = true,
    center: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeWidget<CenterBox>, GtkApplier>(
        factory = {
            VirtualComposeNodeContainer(CenterBox.builder().build())
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(orientation) { this.widget.orientation = it }
            set(baselinePosition) { this.widget.baselinePosition = it }
            set(shrinkCenterLast) { this.widget.shrinkCenterLast = it }
        },
        content = {
            Child({ startWidget = it }, content = start)
            Child({ centerWidget = it }, content = center)
            Child({ endWidget = it }, content = end)
        },
    )
}

@Composable
private fun Child(
    setter: CenterBox.(Widget?) -> Unit,
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<CenterBox> { overlay ->
                SingleChildComposeNode(
                    overlay,
                    set = { setter(it) },
                )
            }
        },
        update = { },
        content = content,
    )
}
