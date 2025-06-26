package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.gtk.Justification
import org.gnome.gtk.Label
import org.gnome.gtk.NaturalWrapMode
import org.gnome.pango.EllipsizeMode
import org.gnome.pango.WrapMode

private class GtkLabelComposeNode(gObject: Label) : LeafComposeNode<Label>(gObject) {
    var onActivateCurrentLink: SignalConnection<Label.ActivateCurrentLinkCallback>? = null
    var onActivateLink: SignalConnection<Label.ActivateLinkCallback>? = null
    var onCopyClipboard: SignalConnection<Label.CopyClipboardCallback>? = null
}

/**
 * Creates a [org.gnome.gtk.Label] that displays text.
 *
 * @param text The displayed text.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param ellipsize The mode used to ellipsize the text if it does not have enough space.
 * @param justify The alignment of the lines in the text.
 * @param lines The number of lines to which an ellipsized, wrapping label should display before it gets ellipsized.
 * @param maxWidthChars The desired maximum width of the label, in characters.
 * @param naturalWrapMode Selects the line wrapping for the natural size request.
 * @param selectable Whether the text can be selected.
 * @param singleLineMode Whether the label is in single line mode.
 * @param useMarkup Whether the text uses Pango markup.
 * @param useUnderline Whether to use an underscore in the label for mnemonic activation.
 * @param widthChars The desired width of the label, in characters.
 * @param wrap Whether the text wraps if it's too long.
 * @param wrapMode The mode used to wrap the text.
 * @param xAlign The horizontal alignment of the text.
 * @param yAlign The vertical alignment of the text.
 * @param onActivateCurrentLink Callback triggered when a link inside the text is activated using keyboard
 * navigation or accessibility tools.
 * @param onActivateLink Callback triggered when the link is activated. Return `true` to handle the event manually.
 * @param onCopyClipboard Callback triggered when the text is copied.
 */
@Composable
fun Label(
    text: String,
    modifier: Modifier = Modifier,
    // TODO - attributes
    ellipsize: EllipsizeMode = EllipsizeMode.NONE,
    // TODO - extra-menu
    justify: Justification = Justification.LEFT,
    lines: Int = -1,
    maxWidthChars: Int = -1,
    // TODO - mnemonic-widget
    naturalWrapMode: NaturalWrapMode = NaturalWrapMode.INHERIT,
    selectable: Boolean = false,
    singleLineMode: Boolean = false,
    // TODO - tabs
    useMarkup: Boolean = false,
    useUnderline: Boolean = false,
    widthChars: Int = -1,
    wrap: Boolean = false,
    wrapMode: WrapMode = WrapMode.WORD,
    xAlign: Float = .5f,
    yAlign: Float = .5f,
    onActivateCurrentLink: (() -> Unit)? = null,
    onActivateLink: ((uri: String) -> Boolean)? = null,
    onCopyClipboard: (() -> Unit)? = null,
) {
    ComposeNode<GtkLabelComposeNode, GtkApplier>({
        GtkLabelComposeNode(Label.builder().build())
    }) {
        set(modifier) { applyModifier(it) }
        set(text) { this.widget.text = it }
        set(ellipsize) { this.widget.ellipsize = it }
        set(justify) { this.widget.justify = it }
        set(lines) { this.widget.lines = it }
        set(maxWidthChars) { this.widget.maxWidthChars = it }
        set(selectable) { this.widget.selectable = it }
        set(naturalWrapMode) { this.widget.naturalWrapMode = it }
        set(singleLineMode) { this.widget.singleLineMode = it }
        set(useMarkup) { this.widget.useMarkup = it }
        set(useUnderline) { this.widget.useUnderline = it }
        set(widthChars) { this.widget.widthChars = it }
        set(wrap) { this.widget.wrap = it }
        set(wrapMode) { this.widget.wrapMode = it }
        set(xAlign) { this.widget.xalign = it }
        set(yAlign) { this.widget.yalign = it }
        set(onActivateCurrentLink) {
            this.onActivateCurrentLink?.disconnect()
            onActivateCurrentLink?.let { this.widget.onActivateCurrentLink(onActivateCurrentLink) }
        }
        set(onActivateLink) {
            this.onActivateLink?.disconnect()
            onActivateLink?.let { this.widget.onActivateLink(onActivateLink) }
        }
        set(onCopyClipboard) {
            this.onCopyClipboard?.disconnect()
            onCopyClipboard?.let { this.widget.onCopyClipboard(onCopyClipboard) }
        }
    }
}
