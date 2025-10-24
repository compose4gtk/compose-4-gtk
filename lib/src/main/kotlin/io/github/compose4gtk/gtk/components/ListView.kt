package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
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
import org.gnome.gtk.SingleSelection
import org.gnome.gtk.Widget
import org.gnome.gtk.ListView as GTKListView

private val logger = KotlinLogging.logger {}

internal class BaseListComposeNode<W : Widget, C : FunctionPointer>(
    gObject: W,
) : LeafComposeNode<W>(gObject) {
    var onActivate: SignalConnection<C>? = null
    var onSelectionChanges: SignalConnection<SelectionModel.SelectionChangedCallback>? = null
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
    value: Set<Int>,
    items: Int,
    selectionMode: SelectionMode<M>,
    modifier: Modifier = Modifier,
    enableRubberband: Boolean = false,
    singleClickActivate: Boolean = false,
    showSeparators: Boolean = false,
    tabBehaviour: ListTabBehavior = ListTabBehavior.ALL,
    onActivate: ((position: Int) -> Unit)? = null,
    onSelectionChanges: ((positions: Set<Int>) -> Unit)? = null,
    child: @Composable (index: Int) -> Unit,
): M {
    val selectionModel = rememberSelectionModel(itemsCount = items, selectionMode = selectionMode)
    ListView(
        value = value,
        model = selectionModel,
        modifier = modifier,
        enableRubberband = enableRubberband,
        singleClickActivate = singleClickActivate,
        showSeparators = showSeparators,
        tabBehaviour = tabBehaviour,
        onSelectionChanges = onSelectionChanges,
        onActivate = onActivate,
    ) {
        child(it.index)
    }
    return selectionModel
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
    value: Set<Int>,
    model: SelectionModel<T>,
    modifier: Modifier = Modifier,
    enableRubberband: Boolean = false,
    singleClickActivate: Boolean = false,
    showSeparators: Boolean = false,
    tabBehaviour: ListTabBehavior = ListTabBehavior.ALL,
    onActivate: ((position: Int) -> Unit)? = null,
    onSelectionChanges: ((positions: Set<Int>) -> Unit)? = null,
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
            set(model) {
                if (model is SingleSelection) {
                    model.autoselect = false
                    model.canUnselect = true
                }
                this.widget.model = it
            }
            // Ensures that when items are removed and re-added,
            // they are reselected to keep the UI in sync with the state.
            set(model.size) {
                pendingChange++
            }
            set(enableRubberband) { this.widget.enableRubberband = it }
            set(showSeparators) { this.widget.showSeparators = it }
            set(singleClickActivate) { this.widget.singleClickActivate = it }
            set(tabBehaviour) { this.widget.tabBehavior = it }
            set(onSelectionChanges) {
                this.onSelectionChanges?.disconnect()
                if (onSelectionChanges != null) {
                    this.onSelectionChanges = model.onSelectionChanged { _, _ ->
                        pendingChange++
                        val positions = mutableListOf<Int>()
                        for (position in 0 until model.size) {
                            if (model.isSelected(position)) {
                                positions.add(position)
                            }
                        }
                        onSelectionChanges(positions.toSet())
                    }
                } else {
                    this.onSelectionChanges = null
                }
            }
            set(onActivate) {
                this.onActivate?.disconnect()
                if (onActivate != null) {
                    this.onActivate = this.widget.onActivate(it)
                } else {
                    this.onActivate = null
                }
            }
            set(value to pendingChange) {
                this.onSelectionChanges?.block()
                model.unselectAll()
                for (position in value) {
                    if (position < model.size) {
                        model.selectItem(position, false)
                    } else {
                        logger.warn { "Position $position is out of range" }
                    }
                }
                this.onSelectionChanges?.unblock()
            }
        },
    )
}
