package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.rememberCompositionContext
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gobject.GObject
import org.gnome.gtk.ListTabBehavior
import org.gnome.gtk.SelectionModel
import org.gnome.gtk.Widget
import org.javagi.base.FunctionPointer
import org.javagi.gio.ListIndexModel
import org.javagi.gobject.SignalConnection
import org.gnome.gtk.ListView as GTKListView

internal class BaseListComposeNode<W : Widget, C : FunctionPointer>(
    gObject: W,
) : LeafComposeNode<W>(gObject) {
    var onActivate: SignalConnection<C>? = null
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
 *     items = 10000,
 *     selectionMode = SelectionMode.Multiple,
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
    val compositionContext = rememberCompositionContext()

    ComposeNode<BaseListComposeNode<GTKListView, GTKListView.ActivateCallback>, GtkApplier>(
        factory = {
            BaseListComposeNode(
                gObject = GTKListView.builder().setFactory(createListItemFactory(compositionContext, child)).build(),
            )
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(model) { this.widget.model = it }
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
        },
    )
}
