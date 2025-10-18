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
import androidx.compose.runtime.rememberCompositionContext
import io.github.compose4gtk.gtk.components.createListItemFactory
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.gobject.GObject
import org.gnome.gobject.ParamSpec
import org.gnome.gtk.Adjustment
import org.gnome.gtk.SingleSelection
import org.gnome.gtk.StringList
import org.gnome.gtk.Widget
import kotlin.collections.List
import kotlin.collections.forEach
import kotlin.collections.mutableListOf
import kotlin.collections.toTypedArray
import kotlin.ranges.coerceAtLeast
import org.gnome.adw.ActionRow as AdwActionRow
import org.gnome.adw.ComboRow as AdwComboRow
import org.gnome.adw.SpinRow as AdwSpinRow
import org.gnome.adw.SwitchRow as AdwSwitchRow
import org.gnome.gtk.SpinButton as GtkSpinButton
import org.gnome.gtk.Switch as GtkSwitch

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
 * @param subtitle The subtitle for this row.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param onActivate Callback triggered when this row is activated.
 * @param activatable Whether the component can be activated.
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
                    onActivate()
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

private class AdwComboRowComposeNode(gObject: AdwComboRow) : LeafComposeNode<AdwComboRow>(gObject) {
    var onSelected: SignalConnection<*>? = null
}

@Composable
fun ComboRow(
    items: List<String>,
    selectedIndex: Int,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onSelectedChange: (Int) -> Unit = {},
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    subtitleLines: Int = 0,
    subtitleSelectable: Boolean = false,
    titleLines: Int = 0,
) {
    val comboRow = remember { AdwComboRow() }
    var pendingChange by remember { mutableIntStateOf(0) }
    val model = remember(items) { StringList(items.toTypedArray()) }

    BaseActionRow(
        creator = { AdwComboRowComposeNode(comboRow) },
        updater = {
            set(items) {
                this.widget.setModel(model)
                if (this.widget.selected >= items.size) {
                    this.widget.selected = (items.size - 1).coerceAtLeast(0)
                }
            }
            set(selectedIndex to pendingChange) { (selected, _) ->
                this.onSelected?.block()
                if (selected < (this.widget.model?.nItems ?: 0)) {
                    comboRow.selected = selected
                }
                this.onSelected?.unblock()
            }
            set(onSelectedChange) { onSelectionChanges ->
                this.onSelected?.disconnect()
                println(this.onSelected)
                // Observe property changes for "selected" (since it's not a proper signal)
                // https://gnome.pages.gitlab.gnome.org/libadwaita/doc/1.8/property.ComboRow.selected.html
                this.onSelected = this.widget.connect("notify::selected") { _: ParamSpec ->
                    pendingChange += 1
                    onSelectionChanges(this.widget.selected)
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

@Composable
fun <T : GObject> ComboRow(
    model: SingleSelection<T>,
    onSelectedChange: (selectedItem: T) -> Unit,
    item: @Composable (T) -> Unit,
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
    selectedItem: @Composable (T) -> Unit = { item(it) },
) {
    val compositionContext = rememberCompositionContext()
    val comboRow = remember {
        AdwComboRow.builder()
            .setListFactory(createListItemFactory(compositionContext, item))
            .setFactory(createListItemFactory(compositionContext, selectedItem))
            .build()
    }
    var pendingChange by remember { mutableIntStateOf(0) }

    BaseActionRow(
        creator = { AdwComboRowComposeNode(comboRow) },
        updater = {
            set(model to pendingChange) {
                this.onSelected?.block()
                this.widget.model = it.first
                this.onSelected?.unblock()
            }
            set(onSelectedChange) { callback ->
                this.onSelected?.disconnect()
                // Observe property changes for "selected" (since it's not a proper signal)
                this.onSelected = this.widget.connect("notify::selected-item") { _: ParamSpec ->
                    pendingChange += 1
                    val selection = widget.selectedItem
                    if (selection != null) {
                        @Suppress("UNCHECKED_CAST")
                        callback(selection as T)
                    }
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

private class AdwSpinRowComposeNode(gObject: AdwSpinRow) : LeafComposeNode<AdwSpinRow>(gObject) {
    var valueChanged: SignalConnection<GtkSpinButton.ValueChangedCallback>? = null
    var onActivate: SignalConnection<GtkSpinButton.ActivateCallback>? = null
}

/**
 * Creates a [org.gnome.adw.SpinRow] that displays numeric values
 *
 * [org.gnome.adw.SpinRow] is a child of [org.gnome.adw.ActionRow] and [org.gnome.adw.PreferencesRow] which are usually
 * used for preferences/settings inside and application.
 *
 * @param value Number displayed in the component.
 * @param title The title for this row.
 * @param subtitle The subtitle for this row.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param onActivate Callback triggered when the component is activated (pressing "enter").
 * @param onValueChange Callback triggered when the value changes.
 * @param lower Minimum allowed value.
 * @param upper Maximum allowed value.
 * @param stepIncrement How much the value increments or decrements by when using the buttons or the arrow keys.
 * @param pageIncrement How much the value increments or decrements by when using "PageUp" or "PageDown".
 * @param climbRate The acceleration rate when holding down an arrow button or key.
 * @param digits The number of decimal places to display.
 * @param numeric Whether non-numeric characters are ignored.
 * @param activatable Whether the component can be activated.
 * @param titleSelectable Whether the title is selectable.
 * @param useMarkup Whether to use Pango markup for the title and subtitle.
 * @param useUnderline Whether an embedded underline in the title or subtitle indicates a mnemonic.
 * @param subtitleLines The number of lines at the end of which the subtitle label will be ellipsized.
 * @param subtitleSelectable Whether the subtitle is selectable.
 * @param titleLines The number of lines at the end of which the title label will be ellipsized.
 */
@Composable
fun SpinRow(
    value: Double,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onActivate: () -> Unit = {},
    onValueChange: (Double) -> Unit = {},
    lower: Double = 0.0,
    upper: Double = 0.0,
    stepIncrement: Double = 0.0,
    pageIncrement: Double = 0.0,
    climbRate: Double = 0.0,
    digits: Int = 0,
    numeric: Boolean = false,
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    subtitleLines: Int = 0,
    subtitleSelectable: Boolean = false,
    titleLines: Int = 0,
) {
    val adjustment = remember {
        Adjustment.builder()
            .setValue(value)
            .setLower(lower)
            .setUpper(upper)
            .setStepIncrement(stepIncrement)
            .setPageIncrement(pageIncrement)
            .build()
    }
    val spinRow = remember { AdwSpinRow.builder().setAdjustment(adjustment).build() }
    val spinButton = remember { spinRow.firstChild.lastChild.firstChild as GtkSpinButton }
    var pendingChange by remember { mutableIntStateOf(0) }

    BaseActionRow(
        creator = { AdwSpinRowComposeNode(spinRow) },
        updater = {
            set(value to pendingChange) { (value, _) ->
                this.valueChanged?.block()
                this.widget.value = value
                this.valueChanged?.unblock()
            }
            set(onActivate) {
                this.onActivate?.disconnect()
                this.onActivate = spinButton.onActivate {
                    pendingChange += 1
                    onActivate()
                }
            }
            set(onValueChange) {
                this.valueChanged?.disconnect()
                this.valueChanged = spinButton.onValueChanged {
                    pendingChange += 1
                    onValueChange(this.widget.value)
                }
            }
            set(lower) { this.widget.adjustment.lower = it }
            set(upper) { this.widget.adjustment.upper = it }
            set(stepIncrement) { this.widget.adjustment.stepIncrement = it }
            set(pageIncrement) { this.widget.adjustment.pageIncrement = it }
            set(climbRate) { this.widget.climbRate = it }
            set(digits) { this.widget.digits = it }
            set(numeric) { this.widget.numeric = it }
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
