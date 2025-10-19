package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Updater
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.adw.PreferencesRow
import org.gnome.gtk.Editable
import org.gnome.adw.EntryRow as AdwEntryRow

private class AdwEntryRowComposeNode(gObject: AdwEntryRow) : LeafComposeNode<AdwEntryRow>(gObject) {
    var onApply: SignalConnection<AdwEntryRow.ApplyCallback>? = null
    var onEntryActivated: SignalConnection<AdwEntryRow.EntryActivatedCallback>? = null
    var onTextChanged: SignalConnection<Editable.ChangedCallback>? = null
}

@Composable
private fun <W : GtkComposeWidget<PreferencesRow>> BasePreferenceRow(
    creator: () -> W,
    updater: Updater<W>.() -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    content: @Composable () -> Unit = {},
) {
    ComposeNode<W, GtkApplier>(
        factory = creator,
        update = {
            set(title) { this.widget.title = it }
            set(modifier) { applyModifier(it) }
            set(activatable) { this.widget.activatable = it }
            set(titleSelectable) { this.widget.titleSelectable = it }
            set(useMarkup) { this.widget.useMarkup = it }
            set(useUnderline) { this.widget.useUnderline = it }
            updater()
        },
        content = content,
    )
}

/**
 * Creates a [org.gnome.adw.EntryRow], a list box row that contains a text entry.
 *
 * [org.gnome.adw.EntryRow] is a child of [org.gnome.adw.PreferencesRow]
 * which are usually used for preferences/settings inside an application.
 *
 * @param text The text displayed in the entry.
 * @param title The title for this row.
 * @param subtitle The subtitle for this row.
 * @param modifier Compose [io.github.compose4gtk.modifier.Modifier] for layout and styling.
 * @param onEntryActivate Callback triggered when the entry is activated (pressing Enter).
 * @param onApply Callback triggered when the apply button is pressed.
 * @param activatable Whether the component can be activated.
 * @param titleSelectable Whether the title is selectable.
 * @param useMarkup Whether to use Pango markup for the title and subtitle.
 * @param useUnderline Whether an embedded underline in the title or subtitle indicates a mnemonic.
 * @param subtitleLines The number of lines at the end of which the subtitle label will be ellipsized.
 * @param subtitleSelectable Whether the subtitle is selectable.
 */
@Composable
fun EntryRow(
    text: String,
    title: String,
    modifier: Modifier = Modifier,
    onEntryActivate: () -> Unit = {},
    onApply: () -> Unit = {},
    onTextChange: (String) -> Unit = {},
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    showApplyButton: Boolean = true,
) {
    val entryRow = remember { AdwEntryRow() }
    var pendingChange by remember { mutableIntStateOf(0) }

    BasePreferenceRow(
        creator = {
            AdwEntryRowComposeNode(entryRow)
        },
        updater = {
            set(text to pendingChange) { (t, _) ->
                this.onTextChanged?.block()
                if (entryRow.text != t) {
                    entryRow.text = t
                }
                this.onTextChanged?.unblock()
            }
            set(onEntryActivate) {
                this.onEntryActivated?.disconnect()
                this.onEntryActivated = entryRow.onEntryActivated {
                    onEntryActivate()
                }
            }
            set(onTextChange) {
                this.onTextChanged?.disconnect()
                this.onTextChanged = entryRow.onChanged {
                    pendingChange += 1
                    onTextChange(entryRow.text)
                }
            }
            set(showApplyButton) {
                if (entryRow.showApplyButton != it) {
                    entryRow.showApplyButton = it
                }
            }
            set(onApply) {
                this.onApply?.disconnect()
                this.onApply = entryRow.onApply {
                    onApply()
                }
            }
        },
        title = title,
        modifier = modifier,
        activatable = activatable,
        titleSelectable = titleSelectable,
        useMarkup = useMarkup,
        useUnderline = useUnderline,
    )
}
