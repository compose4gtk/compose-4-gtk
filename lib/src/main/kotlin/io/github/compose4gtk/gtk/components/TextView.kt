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
import org.gnome.gtk.InputHints
import org.gnome.gtk.InputPurpose
import org.gnome.gtk.Justification
import org.gnome.gtk.TextBuffer
import org.gnome.gtk.TextIter
import org.gnome.gtk.WrapMode
import org.javagi.gobject.SignalConnection
import org.gnome.gtk.TextView as GtkTextView

private class GtkTextViewComposeNode(gObject: GtkTextView) : LeafComposeNode<GtkTextView>(gObject) {
    var onTextChange: SignalConnection<TextBuffer.ChangedCallback>? = null
}

// TODO: extra-menu, im-module, overwrite? (insert), tabs
/**
 * Creates a [org.gnome.gtk.TextView], useful for entering large amounts of text.
 *
 * @param text The text content.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param enableUndo Whether support for undoing and redoing changes is allowed.
 * @param acceptsTab Whether `Tab` will result in a tab character being entered.
 * @param bottomMargin The bottom margin for text in the text view.
 * @param cursorVisible If the insertion cursor is shown.
 * @param editable Whether the text can be modified by the user.
 * @param indent Amount to indent the paragraph, in pixels.
 * @param inputHints Additional hints (beyond [inputPurpose]) that allow input methods to fine-tune their behaviour.
 * @param inputPurpose The purpose of this text field.
 * @param justification Left, right, or center justification.
 * @param leftMargin The default left margin for text in the text view.
 * @param monospace Whether text should be displayed in a monospace font.
 * @param pixelsAboveLines Pixels of blank space above paragraphs.
 * @param pixelsBelowLines Pixels of blank space below paragraphs.
 * @param pixelsInsideWrap Pixels of blank space between wrapped lines in a paragraph.
 * @param rightMargin The default right margin for text in the text view.
 * @param topMargin The top margin for text in the text view.
 * @param wrapMode Whether to wrap lines never, at word boundaries, or at character boundaries.
 */
@Composable
fun TextView(
    text: String,
    modifier: Modifier = Modifier,
    enableUndo: Boolean = true,
    acceptsTab: Boolean = true,
    bottomMargin: Int = 0,
    cursorVisible: Boolean = true,
    editable: Boolean = true,
    indent: Int = 0,
    inputHints: Set<InputHints> = setOf(InputHints.NONE),
    inputPurpose: InputPurpose = InputPurpose.FREE_FORM,
    justification: Justification = Justification.LEFT,
    leftMargin: Int = 0,
    monospace: Boolean = false,
    pixelsAboveLines: Int = 0,
    pixelsBelowLines: Int = 0,
    pixelsInsideWrap: Int = 0,
    rightMargin: Int = 0,
    topMargin: Int = 0,
    wrapMode: WrapMode = WrapMode.NONE,
    onTextChange: (String) -> Unit = {},
) {
    val gtkTextView = GtkTextView()
    var pendingChange by remember { mutableIntStateOf(0) }
    var cursorOffset by remember { mutableIntStateOf(0) }

    ComposeNode<GtkTextViewComposeNode, GtkApplier>(
        factory = {
            GtkTextViewComposeNode(gtkTextView)
        },
        update = {
            set(text to pendingChange) {
                val buffer = this.widget.buffer

                val startIter = TextIter()
                val endIter = TextIter()
                buffer.getStartIter(startIter)
                buffer.getEndIter(endIter)

                val current = buffer.getText(startIter, endIter, false)

                if (current != text) {
                    this.onTextChange?.block()

                    buffer.delete(startIter, endIter)
                    buffer.insert(startIter, text, text.length)

                    // Place the cursor where text was added/removed
                    val newOffset = cursorOffset.coerceAtMost(text.length)
                    val textIter = TextIter()
                    buffer.getIterAtOffset(textIter, newOffset)
                    buffer.placeCursor(textIter)

                    this.onTextChange?.unblock()
                }
            }
            set(modifier) { applyModifier(it) }
            set(enableUndo) { this.widget.buffer.enableUndo = it }
            set(acceptsTab) { this.widget.acceptsTab = it }
            set(bottomMargin) { this.widget.bottomMargin = it }
            set(cursorVisible) { this.widget.cursorVisible = it }
            set(editable) { this.widget.editable = it }
            set(indent) { this.widget.indent = it }
            set(inputHints) { this.widget.inputHints = it }
            set(inputPurpose) { this.widget.inputPurpose = it }
            set(justification) { this.widget.justification = it }
            set(leftMargin) { this.widget.leftMargin = it }
            set(monospace) { this.widget.monospace = it }
            set(pixelsAboveLines) { this.widget.pixelsAboveLines = it }
            set(pixelsBelowLines) { this.widget.pixelsBelowLines = it }
            set(pixelsInsideWrap) { this.widget.pixelsInsideWrap = it }
            set(rightMargin) { this.widget.rightMargin = it }
            set(topMargin) { this.widget.topMargin = it }
            set(wrapMode) { this.widget.wrapMode = it }
            set(onTextChange) {
                this.onTextChange?.disconnect()
                val buffer = this.widget.buffer
                this.onTextChange = buffer.onChanged {
                    pendingChange++

                    val startIter = TextIter()
                    val endIter = TextIter()
                    val cursorIter = TextIter()

                    buffer.getStartIter(startIter)
                    buffer.getEndIter(endIter)

                    val mark = buffer.getMark("insert")
                    buffer.getIterAtMark(cursorIter, mark)
                    cursorOffset = cursorIter.offset

                    onTextChange(buffer.getText(startIter, endIter, false))
                }
            }
        },
    )
}
