package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.base.FunctionPointer
import io.github.jwharm.javagi.gio.ListIndexModel
import io.github.jwharm.javagi.gobject.SignalConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.gobject.GObject
import org.gnome.gtk.ListTabBehavior
import org.gnome.gtk.SelectionModel
import org.gnome.gtk.Widget
import org.javagi.base.FunctionPointer
import org.javagi.gio.ListIndexModel
import org.javagi.gobject.SignalConnection
import org.gnome.gtk.ListView as GTKListView

private val logger = KotlinLogging.logger {}

internal class BaseListComposeNode<W : Widget, C : FunctionPointer>(
    gObject: W,
) : LeafComposeNode<W>(gObject) {
    var onActivate: SignalConnection<C>? = null
    var onSelectionChanges: SignalConnection<SelectionModel.SelectionChangedCallback>? = null
}

@Composable
private fun <T : GObject> BaseControlledListView(
    model: SelectionModel<T>,
    modifier: Modifier = Modifier,
    selectedItems: Set<Int>? = null,
    enableRubberband: Boolean = false,
    singleClickActivate: Boolean = false,
    showSeparators: Boolean = false,
    tabBehaviour: ListTabBehavior = ListTabBehavior.ALL,
    onActivate: ((position: Int) -> Unit)? = null,
    onSelectionChange: ((positions: Set<Int>) -> Unit)? = null,
    child: @Composable (item: T) -> Unit,
) {
    val compositionContext = rememberCompositionContext()
    var pendingChange by remember { mutableIntStateOf(0) }

    ComposeNode<BaseListComposeNode<GTKListView, GTKListView.ActivateCallback>, GtkApplier>(
        factory = {
            BaseListComposeNode(
                gObject = GTKListView.builder().setFactory(createListItemFactory(compositionContext, child)).build(),
            )
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(model) { this.widget.model = it }
            set(model.size) { pendingChange++ }
            set(enableRubberband) { this.widget.enableRubberband = it }
            set(showSeparators) { this.widget.showSeparators = it }
            set(singleClickActivate) { this.widget.singleClickActivate = it }
            set(tabBehaviour) { this.widget.tabBehavior = it }
            set(onActivate) {
                this.onActivate?.disconnect()
                if (onActivate != null) {
                    this.onActivate = this.widget.onActivate(it)
                } else {
                    this.onActivate = null
                }
            }
            set(onSelectionChange) {
                this.onSelectionChanges?.disconnect()
                this.onSelectionChanges = model.onSelectionChanged { _, _ ->
                    pendingChange++
                    val positions = mutableListOf<Int>()
                    for (position in 0 until model.size) {
                        if (model.isSelected(position)) {
                            positions.add(position)
                        }
                    }
                    onSelectionChange?.invoke(positions.toSet())
                }
            }
            set(selectedItems to selectedItems?.size to pendingChange) {
                if (selectedItems != null) {
                    this.onSelectionChanges?.block()
                    model.unselectAll()
                    if (selectedItems.isNotEmpty()) {
                        for (position in selectedItems) {
                            if (position < model.size) {
                                model.selectItem(position, false)
                            } else {
                                logger.warn { "Position $position is out of range" }
                            }
                        }
                    }
                    this.onSelectionChanges?.unblock()
                }
            }
        },
    )
}

/**
 * Creates a [org.gnome.gtk.ListView] bound to the given [model].
 * Each element is a composable created using [child].
 *
 * [SelectionModel] can be created using the [rememberNoSelectionModel], [rememberSingleSelectionModel] and
 * [rememberMultiSelectionModel] functions, but you can also create them explicitly if you need more customization.
 */
@Composable
fun <T : GObject> ListView(
    model: SelectionModel<T>,
    modifier: Modifier = Modifier,
    enableRubberband: Boolean = false,
    singleClickActivate: Boolean = false,
    showSeparators: Boolean = false,
    tabBehaviour: ListTabBehavior = ListTabBehavior.ALL,
    onActivate: ((position: Int) -> Unit)? = null,
    child: @Composable (item: T) -> Unit,
) {
    BaseControlledListView(
        model = model,
        modifier = modifier,
        enableRubberband = enableRubberband,
        singleClickActivate = singleClickActivate,
        showSeparators = showSeparators,
        tabBehaviour = tabBehaviour,
        onActivate = onActivate,
        child = child,
    )
}

/**
 * Creates a [org.gnome.gtk.ListView] with [items] items.
 * Each element is a composable created using [child].
 *
 * The created [org.gnome.gio.ListModel] will have the specified [selectionMode] (e.g. [SelectionMode.Multiple]).
 *
 * Example:
 * ```kotlin
 * ListView(
 *     value = value,
 *     items = 10000,
 *     selectionMode = SelectionMode.Multiple,
 *     onSelectionChanges = { value = it },
 * ) { index ->
 *     Label("Item #$index")
 * }
 * ```
 *
 * You usually want to wrap this component into a scrollable container, like [ScrolledWindow].
 *
 * You can use `ListView(model){ ... }` if you want more customization options.
 *
 * @return the selection model you can use to manage the selection
 */
@Suppress("ComposableNaming", "ContentEmitterReturningValues")
@Composable
fun <M : SelectionModel<ListIndexModel.ListIndex>> ListView(
    items: Int,
    selectionMode: SelectionMode<M>,
    modifier: Modifier = Modifier,
    enableRubberband: Boolean = false,
    singleClickActivate: Boolean = false,
    showSeparators: Boolean = false,
    tabBehaviour: ListTabBehavior = ListTabBehavior.ALL,
    onActivate: ((position: Int) -> Unit)? = null,
    child: @Composable (index: Int) -> Unit,
): M {
    val selectionModel = rememberSelectionModel(itemsCount = items, selectionMode = selectionMode)
    ListView(
        model = selectionModel,
        modifier = modifier,
        enableRubberband = enableRubberband,
        singleClickActivate = singleClickActivate,
        showSeparators = showSeparators,
        tabBehaviour = tabBehaviour,
        onActivate = onActivate,
    ) {
        child(it.index)
    }
    return selectionModel
}

/**
 * Creates a controlled multiple selection [org.gnome.gtk.ListView].
 *
 * @param selectedItems Selected indexes in the list.
 * @param items The items to be listed.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param enableRubberband Whether selection can be changed by dragging with the mouse.
 * @param singleClickActivate Whether rows should be activated on single click and selected on hover.
 * @param showSeparators Show separators between rows.
 * @param tabBehaviour How the `Tab` key behaves.
 * @param onActivate Callback triggered when a row is activated.
 * @param onSelectionChange Callback triggered when selections are made.
 * @param child Composable child representing a row.
 */
@Composable
fun <T : GObject> ControlledListView(
    selectedItems: Set<Int>,
    items: List<T>,
    modifier: Modifier = Modifier,
    enableRubberband: Boolean = false,
    singleClickActivate: Boolean = false,
    showSeparators: Boolean = false,
    tabBehaviour: ListTabBehavior = ListTabBehavior.ALL,
    onActivate: ((position: Int) -> Unit)? = null,
    onSelectionChange: ((positions: Set<Int>) -> Unit)? = null,
    child: @Composable (item: T) -> Unit,
) {
    val model = rememberMultiSelectionModel(items)

    BaseControlledListView(
        model = model,
        modifier = modifier,
        selectedItems = selectedItems,
        enableRubberband = enableRubberband,
        singleClickActivate = singleClickActivate,
        showSeparators = showSeparators,
        tabBehaviour = tabBehaviour,
        onActivate = onActivate,
        onSelectionChange = onSelectionChange,
        child = child,
    )
}

/**
 * Creates a controlled single selection [org.gnome.gtk.ListView].
 *
 * @param selectedItem Selected index in the list.
 * @param items The items to be listed.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param enableRubberband Whether selection can be changed by dragging with the mouse.
 * @param singleClickActivate Whether rows should be activated on single click and selected on hover.
 * @param showSeparators Show separators between rows.
 * @param tabBehaviour How the `Tab` key behaves.
 * @param onActivate Callback triggered when a row is activated.
 * @param onSelectionChange Callback triggered when a selection is made.
 * @param child Composable child representing a row.
 */
@Composable
fun <T : GObject> ControlledListView(
    selectedItem: Int?,
    items: List<T>,
    modifier: Modifier = Modifier,
    enableRubberband: Boolean = false,
    singleClickActivate: Boolean = false,
    showSeparators: Boolean = false,
    tabBehaviour: ListTabBehavior = ListTabBehavior.ALL,
    onActivate: ((position: Int) -> Unit)? = null,
    onSelectionChange: ((positions: Int?) -> Unit)? = null,
    child: @Composable (item: T) -> Unit,
) {
    val model = rememberSingleSelectionModel(items)

    LaunchedEffect(model) {
        model.autoselect = false
        model.canUnselect = true
    }

    BaseControlledListView(
        model = model,
        modifier = modifier,
        selectedItems = if (selectedItem != null) setOf(selectedItem) else emptySet(),
        enableRubberband = enableRubberband,
        singleClickActivate = singleClickActivate,
        showSeparators = showSeparators,
        tabBehaviour = tabBehaviour,
        onActivate = onActivate,
        onSelectionChange = { positions ->
            if (positions.isNotEmpty()) {
                onSelectionChange?.invoke(positions.first())
            } else {
                onSelectionChange?.invoke(null)
            }
        },
        child = child,
    )
}

/**
 * Creates a controlled no selection [org.gnome.gtk.ListView].
 *
 * @param items The items to be listed.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param enableRubberband Whether selection can be changed by dragging with the mouse.
 * @param singleClickActivate Whether rows should be activated on single click and selected on hover.
 * @param showSeparators Show separators between rows.
 * @param tabBehaviour How the `Tab` key behaves.
 * @param onActivate Callback triggered when a row is activated.
 * @param child Composable child representing a row.
 */
@Composable
fun <T : GObject> ControlledListView(
    items: List<T>,
    modifier: Modifier = Modifier,
    enableRubberband: Boolean = false,
    singleClickActivate: Boolean = false,
    showSeparators: Boolean = false,
    tabBehaviour: ListTabBehavior = ListTabBehavior.ALL,
    onActivate: ((position: Int) -> Unit)? = null,
    child: @Composable (item: T) -> Unit,
) {
    val model = rememberNoSelectionModel(items)

    BaseControlledListView(
        model = model,
        modifier = modifier,
        enableRubberband = enableRubberband,
        singleClickActivate = singleClickActivate,
        showSeparators = showSeparators,
        tabBehaviour = tabBehaviour,
        onActivate = onActivate,
        child = child,
    )
}
