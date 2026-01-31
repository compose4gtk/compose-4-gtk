package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Editable
import org.javagi.gobject.SignalConnection
import org.gnome.adw.EntryRow as AdwEntryRow
import org.gnome.adw.PasswordEntryRow as AdwPasswordEntryRow

private class AdwPasswordEntryRowComposeNode(gObject: AdwPasswordEntryRow) :
    LeafComposeNode<AdwPasswordEntryRow>(gObject) {
    var onApply: SignalConnection<AdwEntryRow.ApplyCallback>? = null
    var onEntryActivated: SignalConnection<AdwEntryRow.EntryActivatedCallback>? = null
    var onTextChanged: SignalConnection<Editable.ChangedCallback>? = null
}

/**
 * Creates a [org.gnome.adw.PasswordEntryRow], an entry row that hides its content.
 *
 * [org.gnome.adw.PasswordEntryRow] is a child of [org.gnome.adw.EntryRow] and [org.gnome.adw.PreferencesRow]
 * which are usually used for preferences/settings inside an application.
 *
 * @param text The text displayed in the entry.
 * @param title The title for this row.
 * @param modifier Compose [io.github.compose4gtk.modifier.Modifier] for layout and styling.
 * @param onEntryActivate Callback triggered when the entry is activated (pressing Enter).
 * @param onApply Callback triggered when the apply button is pressed.
 * @param onTextChange Callback triggered when the text changes.
 * @param selectable Whether the row can be selected.
 * @param activatable Whether the component can be activated.
 * @param titleSelectable Whether the title is selectable.
 * @param useMarkup Whether to use Pango markup for the title and subtitle.
 * @param useUnderline Whether an embedded underline in the title or subtitle indicates a mnemonic.
 * @param showApplyButton Whether to show the apply button.
 *
 * TODO: Prefix/suffix
 */
@Composable
fun PasswordEntryRow(
    text: String,
    title: String,
    modifier: Modifier = Modifier,
    onEntryActivate: () -> Unit = {},
    onApply: () -> Unit = {},
    onTextChange: (String) -> Unit = {},
    selectable: Boolean = true,
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    showApplyButton: Boolean = true,
) {
    val passwordEntryRow = remember { AdwPasswordEntryRow() }
    var pendingChange by remember { mutableIntStateOf(0) }

    PreferencesRow(
        creator = {
            AdwPasswordEntryRowComposeNode(passwordEntryRow)
        },
        updater = {
            set(text to pendingChange) { (text, _) ->
                this.onTextChanged?.block()
                if (passwordEntryRow.text != text) {
                    passwordEntryRow.text = text
                }
                this.onTextChanged?.unblock()
            }
            set(onEntryActivate) {
                this.onEntryActivated?.disconnect()
                this.onEntryActivated = passwordEntryRow.onEntryActivated {
                    onEntryActivate()
                }
            }
            set(onTextChange) {
                this.onTextChanged?.disconnect()
                this.onTextChanged = passwordEntryRow.onChanged {
                    pendingChange += 1
                    onTextChange(passwordEntryRow.text)
                }
            }
            set(showApplyButton) {
                if (passwordEntryRow.showApplyButton != it) {
                    passwordEntryRow.showApplyButton = it
                }
            }
            set(onApply) {
                this.onApply?.disconnect()
                this.onApply = passwordEntryRow.onApply {
                    onApply()
                }
            }
        },
        title = title,
        modifier = modifier,
        selectable = selectable,
        activatable = activatable,
        titleSelectable = titleSelectable,
        useMarkup = useMarkup,
        useUnderline = useUnderline,
    )
}
