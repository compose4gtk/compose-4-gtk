package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.GtkContainerComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.gobject.GObject
import org.gnome.gtk.Widget
import org.gnome.adw.ExpanderRow as AdwExpanderRow

private class AdwExpanderRowComposeNode(gObject: AdwExpanderRow) :
    GtkContainerComposeNode<AdwExpanderRow>(gObject) {
    override fun addNode(index: Int, child: GtkComposeWidget<Widget>) {
        when (index) {
            children.size -> widget.addRow(child.widget)
            0 -> widget.insertAfter(child.widget, null)
            else -> widget.insertAfter(child.widget, children[index - 1])
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

    var onExpand: SignalConnection<GObject.NotifyCallback>? = null
    var onEnableExpansion: SignalConnection<GObject.NotifyCallback>? = null
}

/*
    TODO:
        - Prefix/suffix
 */

/**
 * Creates a [org.gnome.adw.ExpanderRow] which contains widget that can be hidden if desired.
 *
 * @param expanded Whether the row is expanded.
 * @param title The title for this row.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param enableExpansion Whether the row can be expanded.
 * @param showEnableSwitch Shows a switch to control [enableExpansion].
 * @param subtitle The subtitle for this row.
 * @param subtitleLines The number of lines at the end of which the subtitle label will be ellipsized.
 * @param titleLines The number of lines at the end of which the title label will be ellipsized.
 * @param selectable Whether the row can be selected.
 * @param activatable Whether the component can be activated.
 * @param titleSelectable Whether the title is selectable.
 * @param useMarkup Whether to use Pango markup for the title and subtitle.
 * @param useUnderline Whether an embedded underline in the title or subtitle indicates a mnemonic.
 * @param onEnableExpansion Callback triggered when the enable switch is triggered.
 * @param onExpand Callback triggered when the row is expanded or contracted.
 * @param content The composable content revealed or hidden by the row.
 *
 * TODO: Prefix/suffix
 */
@Composable
fun ExpanderRow(
    expanded: Boolean,
    title: String,
    modifier: Modifier = Modifier,
    enableExpansion: Boolean = true,
    showEnableSwitch: Boolean = false,
    subtitle: String? = null,
    subtitleLines: Int = 0,
    titleLines: Int = 0,
    selectable: Boolean = true,
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    onEnableExpansion: () -> Unit = {},
    onExpand: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    var pendingExpandedChange by remember { mutableIntStateOf(0) }
    var pendingEnableExpansionChange by remember { mutableIntStateOf(0) }

    PreferencesRow(
        creator = {
            AdwExpanderRowComposeNode(AdwExpanderRow())
        },
        updater = {
            set(expanded to pendingExpandedChange) {
                this.onExpand?.block()
                this.widget.expanded = expanded
                this.onExpand?.unblock()
            }
            set(enableExpansion to pendingEnableExpansionChange) {
                this.onEnableExpansion?.block()
                this.widget.enableExpansion = enableExpansion
                this.onEnableExpansion?.unblock()
            }
            set(showEnableSwitch) { this.widget.showEnableSwitch = it }
            set(subtitle) { this.widget.subtitle = it }
            set(subtitleLines) { this.widget.subtitleLines = it }
            set(titleLines) { this.widget.titleLines = it }
            set(onEnableExpansion) {
                this.onEnableExpansion?.disconnect()
                this.onEnableExpansion = this.widget.onNotify("enable-expansion") {
                    pendingEnableExpansionChange++
                    onEnableExpansion()
                }
            }
            set(onExpand) {
                this.onExpand?.disconnect()
                this.onExpand = this.widget.onNotify("expanded") {
                    pendingExpandedChange++
                    onExpand()
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
        content = content,
    )
}
