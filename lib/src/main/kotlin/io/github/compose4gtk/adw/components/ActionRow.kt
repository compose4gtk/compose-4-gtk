package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Updater
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeNode
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.GtkContainerComposeNode
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.VirtualComposeNode
import io.github.compose4gtk.VirtualComposeNodeContainer
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.combine
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.gtk.Widget
import org.gnome.adw.ActionRow as AdwActionRow
import org.gnome.adw.SwitchRow as AdwSwitchRow
import org.gnome.gtk.Switch as GtkSwitch

enum class ActionRowSlot {
    PREFIX,
    SUFFIX,
}

@DslMarker
annotation class ActionRowSlotScopeMarker

@ActionRowSlotScopeMarker
interface ActionRowSlotScope {
    /**
     * A custom modifier to set the activatable child in an action row.
     */
    fun Modifier.activateWithActionRow(): Modifier = combine(
        apply = {
            val parent = it.parent.parent.parent as AdwActionRow
            if (parent.activatableWidget == null) {
                parent.activatableWidget = it
            } else {
                error("Action row can only have one activatable widget.")
            }
        },
        undo = {
            val parent = it.parent.parent.parent as AdwActionRow
            if (parent.activatableWidget == it) {
                parent.activatableWidget = null
            }
        },
    )
}

@Composable
private fun <W : GtkComposeWidget<AdwActionRow>> BaseActionRow(
    creator: () -> W,
    updater: Updater<W>.() -> Unit,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    subtitleLines: Int = 0,
    subtitleSelectable: Boolean = false,
    titleLines: Int = 0,
    content: @Composable () -> Unit = {},
) {
    ComposeNode<W, GtkApplier>(
        factory = creator,
        update = {
            set(title) { this.widget.title = it }
            set(subtitle) { this.widget.subtitle = it }
            set(modifier) { applyModifier(it) }
            set(activatable) { this.widget.activatable = it }
            set(titleSelectable) { this.widget.titleSelectable = it }
            set(useMarkup) { this.widget.useMarkup = it }
            set(useUnderline) { this.widget.useUnderline = it }
            set(subtitleLines) { this.widget.subtitleLines = it }
            set(subtitleSelectable) { this.widget.subtitleSelectable = it }
            set(titleLines) { this.widget.titleLines = it }
            updater()
        },
        content = content,
    )
}

private class AdwActionRowSlotContainer(actionRow: AdwActionRow, private val slot: ActionRowSlot) :
    GtkContainerComposeNode<AdwActionRow>(actionRow) {
    private val currentlyAdded = mutableListOf<Widget>()

    private fun syncChildren() {
        currentlyAdded.forEach { widget.remove(it) }
        currentlyAdded.clear()

        children.forEach { child ->
            when (slot) {
                ActionRowSlot.PREFIX -> widget.addPrefix(child)
                ActionRowSlot.SUFFIX -> widget.addSuffix(child)
            }
            currentlyAdded.add(child)
        }
    }

    override fun addNode(index: Int, child: GtkComposeWidget<Widget>) {
        super.addNode(index, child)
        syncChildren()
    }

    override fun removeNode(index: Int) {
        super.removeNode(index)
        syncChildren()
    }

    override fun clearNodes() {
        currentlyAdded.forEach { widget.remove(it) }
        currentlyAdded.clear()
        super.clearNodes()
        syncChildren()
    }
}

@Composable
private fun Prefix(
    content: @Composable ActionRowSlotScope.() -> Unit,
) {
    val scope = object : ActionRowSlotScope {}
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<AdwActionRow> { actionRow ->
                AdwActionRowSlotContainer(actionRow, ActionRowSlot.PREFIX)
            }
        },
        update = {},
        content = {
            with(scope) { content() }
        },
    )
}

@Composable
private fun Suffix(
    content: @Composable ActionRowSlotScope.() -> Unit,
) {
    val scope = object : ActionRowSlotScope {}
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<AdwActionRow> { actionRow ->
                AdwActionRowSlotContainer(actionRow, ActionRowSlot.SUFFIX)
            }
        },
        update = {},
        content = { with(scope) { content() } },
    )
}

/**
 * Creates a [org.gnome.adw.ActionRow], a list box row that presents actions.
 *
 * [org.gnome.adw.ActionRow] is a child of [org.gnome.adw.PreferencesRow] which is usually used for
 * preferences/settings inside and application.
 *
 * @param title The title for this row.
 * @param subtitle The subtitle for this row.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param prefix Composable components displayed at the start of the row.
 * @param suffix Composable components displayed at the end of the row.
 * @param useMarkup Whether to use Pango markup for the title and subtitle.
 * @param useUnderline Whether an embedded underline in the title or subtitle indicates a mnemonic.
 * @param subtitleLines The number of lines at the end of which the subtitle label will be ellipsized.
 * @param subtitleSelectable Whether the subtitle is selectable.
 * @param titleLines The number of lines at the end of which the title label will be ellipsized.
 */
@Composable
fun ActionRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    prefix: @Composable ActionRowSlotScope.() -> Unit = {},
    suffix: @Composable ActionRowSlotScope.() -> Unit = {},
    onActivate: () -> Unit = {},
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    subtitleLines: Int = 0,
    subtitleSelectable: Boolean = false,
    titleLines: Int = 0,
) {
    val actionRow = remember { AdwActionRow() }

    BaseActionRow(
        creator = { VirtualComposeNodeContainer(actionRow) },
        updater = {
            set(onActivate) { this.widget.onActivated { onActivate() } }
        },
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        activatable = activatable,
        titleSelectable = titleSelectable,
        useMarkup = useMarkup,
        useUnderline = useUnderline,
        subtitleLines = subtitleLines,
        subtitleSelectable = subtitleSelectable,
        titleLines = titleLines,
        content = {
            Prefix {
                prefix()
            }
            Suffix {
                suffix()
            }
        },
    )
}

private class AdwSwitchRowComposeNode(gObject: AdwSwitchRow) : LeafComposeNode<AdwSwitchRow>(gObject) {
    var activated: SignalConnection<GtkSwitch.StateSetCallback>? = null
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
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
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
                    it()
                    true
                }
            }
        },
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        activatable = activatable,
        titleSelectable = titleSelectable,
        useMarkup = useMarkup,
        useUnderline = useUnderline,
        subtitleLines = subtitleLines,
        subtitleSelectable = subtitleSelectable,
        titleLines = titleLines,
    )
}
