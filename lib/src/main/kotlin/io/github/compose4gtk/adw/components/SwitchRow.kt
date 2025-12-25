package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.adw.SwitchRow as AdwSwitchRow
import org.gnome.gtk.Switch as GtkSwitch

private class AdwSwitchRowComposeNode(gObject: AdwSwitchRow) : LeafComposeNode<AdwSwitchRow>(gObject) {
    var activated: SignalConnection<GtkSwitch.StateSetCallback>? = null
}

/**
 * Creates a [org.gnome.adw.ActionRow], a list box row that contains a switch.
 *
 * [org.gnome.adw.SwitchRow] is a child of [org.gnome.adw.ActionRow] and [org.gnome.adw.PreferencesRow] which are
 * usually used for preferences/settings inside and application.
 *
 * @param active Whether the switch is active.
 * @param title The title for this row.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param onActivate Callback triggered when this row is activated.
 * @param activatable Whether the component can be activated.
 * @param titleSelectable Whether the title is selectable.
 * @param useMarkup Whether to use Pango markup for the title and subtitle.
 * @param useUnderline Whether an embedded underline in the title or subtitle indicates a mnemonic.
 * @param subtitle The subtitle for this row.
 * @param subtitleLines The number of lines at the end of which the subtitle label will be ellipsized.
 * @param subtitleSelectable Whether the subtitle is selectable.
 * @param titleLines The number of lines at the end of which the title label will be ellipsized.
 */
@Composable
fun SwitchRow(
    active: Boolean,
    title: String,
    modifier: Modifier = Modifier,
    onActivate: () -> Unit = {},
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    subtitle: String? = null,
    subtitleLines: Int = 0,
    subtitleSelectable: Boolean = false,
    titleLines: Int = 0,
) {
    val switchRow = remember { AdwSwitchRow() }
    val switch = remember { switchRow.firstChild.lastChild.firstChild as GtkSwitch }
    var pendingChange by remember { mutableIntStateOf(0) }

    BaseActionRow(
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
                    onActivate()
                    true
                }
            }
        },
        title = title,
        modifier = modifier,
        activatable = activatable,
        titleSelectable = titleSelectable,
        useMarkup = useMarkup,
        subtitle = subtitle,
        useUnderline = useUnderline,
        subtitleLines = subtitleLines,
        subtitleSelectable = subtitleSelectable,
        titleLines = titleLines,
    )
}
