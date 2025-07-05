package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.adw.ButtonRow

private class AdwButtonRowComposeNode(gObject: ButtonRow) : LeafComposeNode<ButtonRow>(gObject) {
    var activated: SignalConnection<ButtonRow.ActivatedCallback>? = null
}

/**
 * Creates a [org.gnome.adw.ButtonRow], a list box row that looks like a button and triggers a callback when clicked.
 *
 * [org.gnome.adw.ButtonRow] is a child of [org.gnome.adw.PreferencesRow] which is usually used for
 * preferences/settings inside an application.
 *
 * @param title The text shown in the button.
 * @param titleSelectable Whether the title can be selected.
 * @param useMarkup Whether to use Pango markup for the title.
 * @param useUnderline Whether an embedded underline in the title indicates a mnemonic.
 * @param endIcon The icon shown after the title.
 * @param startIcon The icon shown before the title.
 * @param onActivate Callback triggered when the button is activated.
 */
@Composable
fun ButtonRow(
    title: String,
    modifier: Modifier = Modifier,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    endIcon: ImageSource.Icon? = null,
    startIcon: ImageSource.Icon? = null,
    onActivate: () -> Unit = {},
) {
    ComposeNode<AdwButtonRowComposeNode, GtkApplier>(
        factory = {
            AdwButtonRowComposeNode(ButtonRow.builder().build())
        },
        update = {
            set(title) { this.widget.title = it }
            set(modifier) { applyModifier(it) }
            set(titleSelectable) { this.widget.titleSelectable = it }
            set(useMarkup) { this.widget.useMarkup = it }
            set(useUnderline) { this.widget.useUnderline = it }
            set(endIcon) { this.widget.endIconName = it?.iconName }
            set(startIcon) { this.widget.startIconName = it?.iconName }
            set(onActivate) {
                this.activated?.disconnect()
                this.activated = this.widget.onActivated(it)
            }
        },
    )
}
