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
import org.gnome.adw.ActionRow
import org.gnome.adw.SwitchRow
import org.gnome.gtk.Switch

@Composable
private fun <W : GtkComposeWidget<ActionRow>> GenericActionRow(
    creator: () -> W,
    updater: Updater<W>.() -> Unit,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    subtitleLines: Int = 0,
    subtitleSelectable: Boolean = false,
    titleLines: Int = 0,
) {
    ComposeNode<W, GtkApplier>(
        factory = creator,
        update = {
            set(title) { this.widget.title = it }
            set(subtitle) { this.widget.subtitle = it }
            set(modifier) { applyModifier(it) }
            set(titleSelectable) { this.widget.titleSelectable = it }
            set(useMarkup) { this.widget.useMarkup = it }
            set(useUnderline) { this.widget.useUnderline = it }
            set(subtitleLines) { this.widget.subtitleLines = it }
            set(subtitleSelectable) { this.widget.subtitleSelectable = it }
            set(titleLines) { this.widget.titleLines = it }
            updater()
        },
    )
}

private class AdwSwitchRowComposeNode(gObject: SwitchRow) : LeafComposeNode<SwitchRow>(gObject) {
    var activated: SignalConnection<Switch.StateSetCallback>? = null
}

/**
 * Creates a [org.gnome.adw.ActionRow], a list box row that contains a switch.
 *
 * [org.gnome.adw.ActionRow] is a child of [org.gnome.adw.PreferencesRow] which is usually used for
 * preferences/settings inside and application.
 *
 * @param active Whether the switch is active.
 * @param title The title for this row.
 * @param subtitle The subtitle for this row.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param onActivate Callback triggered when this row is activated.
 * @param titleSelectable Whether the title is selectable.
 * @param useMarkup Whether to use Pango markup for the title and subtitle.
 * @param useUnderline Whether an embedded underline in the title or subtitle indicates a mnemonic.
 * @param subtitleLines The number of lines at the end of which the subtitle label will be ellipsized.
 * @param subtitleSelectable Whether the subtitle is selectable.
 * @param titleLines The number of lines at the end of which the title label will be ellipsized.
 */
@Composable
fun SwitchRow(
    active: Boolean,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onActivate: () -> Unit = {},
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    subtitleLines: Int = 0,
    subtitleSelectable: Boolean = false,
    titleLines: Int = 0,
) {
    val switchRow = remember { SwitchRow.builder().build() }
    val switch = remember { switchRow.firstChild.lastChild.firstChild as Switch }
    var pendingChange by remember { mutableIntStateOf(0) }

    GenericActionRow(
        creator = { AdwSwitchRowComposeNode(switchRow) },
        updater = {
            set(active to pendingChange) { (active, _) ->
                this.activated?.block()
                this.widget.active = active
                switch.active = active
                switch.state = active
                this.activated?.unblock()
            }
            set(onActivate) {
                this.activated?.disconnect()
                this.activated = switch.onStateSet {
                    pendingChange += 1
                    it()
                    true
                }
            }
        },
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        titleSelectable = titleSelectable,
        useMarkup = useMarkup,
        useUnderline = useUnderline,
        subtitleLines = subtitleLines,
        subtitleSelectable = subtitleSelectable,
        titleLines = titleLines,
    )
}
