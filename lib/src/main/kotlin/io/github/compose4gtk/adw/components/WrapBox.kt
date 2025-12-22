package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.GtkContainerComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.JustifyMode
import org.gnome.adw.LengthUnit
import org.gnome.adw.PackDirection
import org.gnome.adw.WrapPolicy
import org.gnome.gtk.Widget
import org.gnome.adw.WrapBox as AdwWrapBox

private class AdwWrapBoxComposeNode(gObject: AdwWrapBox) : GtkContainerComposeNode<AdwWrapBox>(gObject) {
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
 * Creates a [org.gnome.adw.WrapBox], a box-like widget that can wrap into multiple lines.
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param align The alignment of the children within each line.
 * @param childSpacing The spacing between widgets on the same line.
 * @param childSpacingUnit The length unit for child spacing.
 * @param justify Determines whether and how each complete line should be stretched to fill the entire widget.
 * @param justifyLastLine Whether the last line should be stretched to fill the entire widget.
 * @param lineHomogeneous Whether all lines should take the same amount of space.
 * @param lineSpacing The spacing between lines.
 * @param lineSpacingUnit The length unit for line spacing.
 * @param naturalLineLength Determines the natural size for each line.
 * @param naturalLineLengthUnit The length unit for natural line length.
 * @param packDirection The direction children are packed in each line.
 * @param wrapPolicy The policy for line wrapping.
 * @param wrapReverse Whether wrap direction should be reversed.
 * @param content The composable content to display.
 */
@Composable
fun WrapBox(
    modifier: Modifier = Modifier,
    align: Float = 0f,
    childSpacing: Int = 0,
    childSpacingUnit: LengthUnit = LengthUnit.PX,
    justify: JustifyMode = JustifyMode.NONE,
    justifyLastLine: Boolean = false,
    lineHomogeneous: Boolean = false,
    lineSpacing: Int = 0,
    lineSpacingUnit: LengthUnit = LengthUnit.PX,
    naturalLineLength: Int = -1,
    naturalLineLengthUnit: LengthUnit = LengthUnit.PX,
    packDirection: PackDirection = PackDirection.START_TO_END,
    wrapPolicy: WrapPolicy = WrapPolicy.NATURAL,
    wrapReverse: Boolean = false,
    content: @Composable () -> Unit,
) {
    ComposeNode<AdwWrapBoxComposeNode, GtkApplier>(
        factory = {
            AdwWrapBoxComposeNode(AdwWrapBox())
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(align) { this.widget.align = it }
            set(childSpacing) { this.widget.childSpacing = it }
            set(childSpacingUnit) { this.widget.childSpacingUnit = it }
            set(justify) { this.widget.justify = it }
            set(justifyLastLine) { this.widget.justifyLastLine = it }
            set(lineHomogeneous) { this.widget.lineHomogeneous = it }
            set(lineSpacing) { this.widget.lineSpacing = it }
            set(lineSpacingUnit) { this.widget.lineSpacingUnit = it }
            set(naturalLineLength) { this.widget.naturalLineLength = it }
            set(naturalLineLengthUnit) { this.widget.naturalLineLengthUnit = it }
            set(packDirection) { this.widget.packDirection = it }
            set(wrapPolicy) { this.widget.wrapPolicy = it }
            set(wrapReverse) { this.widget.wrapReverse = it }
        },
        content = content,
    )
}
