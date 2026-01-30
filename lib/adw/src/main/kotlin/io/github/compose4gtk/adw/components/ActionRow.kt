package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Updater
import androidx.compose.runtime.remember
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeNode
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.GtkContainerComposeNode
import io.github.compose4gtk.VirtualComposeNode
import io.github.compose4gtk.VirtualComposeNodeContainer
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.combine
import org.gnome.gtk.Widget
import org.gnome.adw.ActionRow as AdwActionRow

private enum class ActionRowSlot {
    PREFIX,
    SUFFIX,
}

sealed interface ActionRowSlotScope {
    /**
     * A custom modifier to set the activatable child in an action row.
     */
    fun Modifier.activateWithActionRow(): Modifier
}

private class ActionRowSlotScopeImpl : ActionRowSlotScope {
    var actionRow: AdwActionRow? = null

    override fun Modifier.activateWithActionRow(): Modifier = combine(
        apply = { widget ->
            actionRow?.let { actionRow ->
                if (actionRow.activatableWidget == null) {
                    actionRow.activatableWidget = widget
                } else {
                    error("Action row can only have one activatable widget.")
                }
            }
        },
        undo = { widget ->
            actionRow?.let { actionRow ->
                if (actionRow.activatableWidget == widget) {
                    actionRow.activatableWidget = null
                }
            }
        },
    )
}

@Composable
internal fun <W : GtkComposeWidget<AdwActionRow>> BaseActionRow(
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
        super.clearNodes()
        syncChildren()
    }
}

@Composable
private fun Prefix(
    actionRow: AdwActionRow,
    content: @Composable ActionRowSlotScope.() -> Unit,
) {
    val scope = ActionRowSlotScopeImpl()
    scope.actionRow = actionRow
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
    actionRow: AdwActionRow,
    content: @Composable ActionRowSlotScope.() -> Unit,
) {
    val scope = ActionRowSlotScopeImpl()
    scope.actionRow = actionRow
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
 * @param onActivate Callback triggered when this row is activated.
 * @param activatable Whether the component can be activated.
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
            Prefix(actionRow) {
                prefix()
            }
            Suffix(actionRow) {
                suffix()
            }
        },
    )
}
