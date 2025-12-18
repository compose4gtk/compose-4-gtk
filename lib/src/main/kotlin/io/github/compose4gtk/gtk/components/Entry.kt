package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import io.github.compose4gtk.Gtk
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.modifier.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.gnome.gobject.GObject
import org.gnome.gobject.GObjects
import org.gnome.gtk.Editable
import org.gnome.gtk.Entry
import org.gnome.gtk.EntryIconPosition
import org.gnome.gtk.Image
import org.gnome.gtk.InputHints
import org.gnome.gtk.InputPurpose
import org.gnome.pango.AttrList
import org.gnome.pango.TabArray
import org.javagi.gobject.SignalConnection

private class GtkEntryComposeNode(
    gObject: Entry,
    val onDeleteSignalHandler: SignalConnection<Editable.DeleteTextCallback>,
    val onInsertSignalHandler: SignalConnection<Editable.InsertTextCallback>,
) : LeafComposeNode<Entry>(gObject) {
    var onActivate: SignalConnection<Entry.ActivateCallback>? = null
    var onPrimaryIconPress: SignalConnection<Entry.IconPressCallback>? = null
    var onSecondaryIconPress: SignalConnection<Entry.IconPressCallback>? = null
}

private data class TentativeCursorPosition(
    val position: Int,
    val condition: (String) -> Boolean,
)

private val emptyAttributes = AttrList()

private data class PendingDelete(val startPos: Int, val endPos: Int) {

    fun countDeletedCharacters(end: Int): Int {
        return if (end > startPos) {
            (end - startPos).coerceAtMost(endPos - startPos)
        } else {
            0
        }
    }

    fun apply(str: String): String {
        return when {
            startPos == str.length -> str
            endPos < 0 -> str.take(startPos)
            else -> str.removeRange(startPos until endPos)
        }
    }
}

/**
 * Creates a [org.gnome.gtk.Entry] used to input text.
 *
 * @param text The text in the entry.
 * @param onTextChange Callback triggered when the text is changed.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param attributes A list of Pango attributes to apply to the text of the entry.
 * @param placeholderText The text displayed when the entry is empty and unfocused.
 * @param editable Whether the text can be edited.
 * @param visibility Whether the contents of the entry are visible.
 * @param activatesDefault Whether pressing `Enter` in will activate the default widget
 * for the window containing the entry.
 * @param alignment The alignment for the contents of the entry.
 * @param hasFrame Whether the entry has a frame.
 * @param inputHints Additional hints which allow input methods to fine-tune their behavior.
 * @param inputPurpose The input purpose which can be used by input methods to adjust their behavior.
 * @param invisibleChar The character to use in place of the actual text when visibility is `false`.
 * @param maxLength The maximum allowed length of the contents.
 * @param progressFraction The level at which the progress indicator of the entry is filled.
 * @param tabs A list of tabstops to apply to the text of the entry.
 * @param enableUndo Whether changes to this entry will be saved for undo/redo actions.
 * @param maxWidthChars The desired maximum width of the entry, in characters.
 * @param widthChars Number of characters to leave space for in the entry.
 *
 * TODO:
 *  - setExtraMenu
 *  - overwriteMode
 *  - pulse
 */
@Composable
fun Entry(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    attributes: AttrList = emptyAttributes,
    onActivate: () -> Unit = {},
    primaryIcon: ImageSource.Icon? = null,
    primaryIconSensitive: Boolean = true,
    onPrimaryIconPress: () -> Unit = {},
    secondaryIcon: ImageSource.Icon? = null,
    secondaryIconSensitive: Boolean = true,
    onSecondaryIconPress: () -> Unit = {},
    placeholderText: String? = null,
    editable: Boolean = true,
    visibility: Boolean = true,
    activatesDefault: Boolean = false,
    alignment: Float = 0f,
    hasFrame: Boolean = true,
    inputHints: InputHints = InputHints.NONE,
    inputPurpose: InputPurpose = InputPurpose.FREE_FORM,
    invisibleChar: Char? = null,
    maxLength: Int = 0,
    progressFraction: Double = 0.0,
    tabs: TabArray? = null,
    enableUndo: Boolean = true,
    maxWidthChars: Int = -1,
    widthChars: Int = -1,
) {
    val cs = rememberCoroutineScope { Dispatchers.Gtk }
    val text by rememberUpdatedState(text)
    val onTextChange by rememberUpdatedState(onTextChange)
    var tentativeCursorPosition by remember { mutableStateOf<TentativeCursorPosition?>(null) }
    var pendingDelete by remember { mutableStateOf<PendingDelete?>(null) }

    fun process() {
        val pd = pendingDelete
        if (pd != null) {
            val newText = pd.apply(text)
            tentativeCursorPosition = TentativeCursorPosition(
                position = pd.startPos,
                condition = { it.length == newText.length },
            )
            pendingDelete = null
            onTextChange(newText)
        }
    }

    ComposeNode<GtkEntryComposeNode, GtkApplier>({
        val entry = Entry.builder().build()
        val editable = entry.delegate!!

        val onInsertText = editable.onInsertText { textToInsert, _, position ->
            val pd = pendingDelete
            var prevText = text
            var newPosition = position.get()
            if (pd != null) {
                prevText = pd.apply(prevText)
                newPosition -= pd.countDeletedCharacters(newPosition)
                pendingDelete = null
            }
            val newText = buildString(prevText.length + textToInsert.length) {
                append(prevText)
                insert(newPosition, textToInsert)
            }
            onTextChange(newText)
            tentativeCursorPosition = TentativeCursorPosition(
                position = newPosition + textToInsert.length,
                condition = { it.length == newText.length },
            )
            GObjects.signalStopEmissionByName(editable as GObject, "insert-text")
        }

        val onDeleteText = editable.onDeleteText { startPos, endPos ->
            require(pendingDelete == null)
            pendingDelete = PendingDelete(startPos, endPos)
            // onInsertText might get called immediately after.
            // I need to be able to handle those things in quick succession.
            cs.launch { process() }
            GObjects.signalStopEmissionByName(editable as GObject, "delete-text")
        }

        GtkEntryComposeNode(entry, onDeleteText, onInsertText)
    }) {
        set(modifier) { applyModifier(it) }
        set(text to tentativeCursorPosition) { (text, pos) ->
            if (this.widget.text != text) {
                this.onDeleteSignalHandler.block()
                this.onInsertSignalHandler.block()
                this.widget.text = text
                this.onInsertSignalHandler.unblock()
                this.onDeleteSignalHandler.unblock()
                if (pos != null && pos.condition(text)) {
                    this.widget.position = pos.position
                }
            }
            tentativeCursorPosition = null
        }
        set(attributes) { this.widget.attributes = it }
        set(onActivate) {
            this.onActivate?.disconnect()
            this.onActivate = this.widget.onActivate {
                this.onActivate?.block()
                onActivate()
                this.onActivate?.unblock()
            }
        }
        set(primaryIcon to primaryIconSensitive to onPrimaryIconPress) {
            applyIcon(primaryIcon, primaryIconSensitive, onPrimaryIconPress, EntryIconPosition.PRIMARY)
        }
        set(secondaryIcon to secondaryIconSensitive to onSecondaryIconPress) {
            applyIcon(secondaryIcon, secondaryIconSensitive, onSecondaryIconPress, EntryIconPosition.SECONDARY)
        }
        set(placeholderText) { this.widget.placeholderText = it }
        set(editable) { this.widget.editable = it }
        set(visibility) { this.widget.visibility = it }
        set(activatesDefault) { this.widget.activatesDefault = it }
        set(alignment) { this.widget.alignment = it }
        set(hasFrame) { this.widget.hasFrame = it }
        set(inputHints) { this.widget.setInputHints(it) }
        set(inputPurpose) { this.widget.inputPurpose = it }
        set(invisibleChar) {
            if (it == null) {
                this.widget.unsetInvisibleChar()
            } else {
                this.widget.invisibleChar = it.code
            }
        }
        set(maxLength) { this.widget.maxLength = it }
        set(progressFraction) { this.widget.progressFraction = it }
        set(tabs) { this.widget.tabs = it }
        set(enableUndo) { this.widget.enableUndo = it }
        set(maxWidthChars) { this.widget.maxWidthChars = it }
        set(widthChars) { this.widget.widthChars = it }
    }
}

private fun GtkEntryComposeNode.applyIcon(
    icon: ImageSource.Icon?,
    sensitive: Boolean,
    onPress: () -> Unit,
    entryIconPosition: EntryIconPosition,
) {
    val child = if (entryIconPosition == EntryIconPosition.PRIMARY) this.widget.firstChild else this.widget.lastChild
    if (icon != null) {
        if (child is Image) child.visible = true
        widget.setIconFromIconName(entryIconPosition, icon.iconName)
    } else {
        if (child is Image) child.visible = false
        widget.setIconFromIconName(entryIconPosition, null)
    }
    widget.setIconSensitive(entryIconPosition, sensitive)

    when (entryIconPosition) {
        EntryIconPosition.PRIMARY -> {
            onPrimaryIconPress?.disconnect()
            onPrimaryIconPress = widget.onIconPress { position ->
                if (position == EntryIconPosition.PRIMARY) {
                    onPrimaryIconPress?.block()
                    onPress()
                    onPrimaryIconPress?.unblock()
                }
            }
        }
        EntryIconPosition.SECONDARY -> {
            onSecondaryIconPress?.disconnect()
            onSecondaryIconPress = widget.onIconPress { position ->
                if (position == EntryIconPosition.SECONDARY) {
                    onSecondaryIconPress?.block()
                    onPress()
                    onSecondaryIconPress?.unblock()
                }
            }
        }
    }
}
