package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeNode
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gio.ListIndexModel
import org.gnome.gio.ListModel
import org.gnome.gio.ListStore
import org.gnome.gobject.GObject
import org.gnome.gtk.ListItem
import org.gnome.gtk.MultiSelection
import org.gnome.gtk.NoSelection
import org.gnome.gtk.SelectionModel
import org.gnome.gtk.SignalListItemFactory
import org.gnome.gtk.SingleSelection
import org.gnome.gtk.ListView as GTKListView

/**
 * The [GtkComposeNode] for each item of a [org.gnome.gtk.ListView].
 */
private class GtkListItemComposeNode(val listItem: ListItem) : GtkComposeNode {
    override fun addNode(index: Int, child: GtkComposeNode) {
        require(index == 0) {
            "ListItem support a single child"
        }
        require(listItem.child == null) {
            "ListItem support a single child"
        }
        require(child is GtkComposeWidget<*>) {
            "ListItem support only GTK widgets"
        }
        listItem.child = child.widget
    }

    override fun removeNode(index: Int) {
        require(index == 0) {
            "ListItem support a single child"
        }
        listItem.child = null
    }

    override fun clearNodes() {
        listItem.child = null
    }
}

sealed interface SelectionMode<M : SelectionModel<ListIndexModel.ListIndex>> {
    fun createSelectionModel(dataModel: ListStore<ListIndexModel.ListIndex>): M

    object None : SelectionMode<NoSelection<ListIndexModel.ListIndex>> {
        override fun createSelectionModel(
            dataModel: ListStore<ListIndexModel.ListIndex>,
        ): NoSelection<ListIndexModel.ListIndex> {
            return NoSelection<ListIndexModel.ListIndex>(dataModel)
        }
    }

    object Single : SelectionMode<SingleSelection<ListIndexModel.ListIndex>> {
        override fun createSelectionModel(
            dataModel: ListStore<ListIndexModel.ListIndex>,
        ): SingleSelection<ListIndexModel.ListIndex> {
            return SingleSelection(dataModel)
        }
    }

    object Multiple : SelectionMode<MultiSelection<ListIndexModel.ListIndex>> {
        override fun createSelectionModel(
            dataModel: ListStore<ListIndexModel.ListIndex>,
        ): MultiSelection<ListIndexModel.ListIndex> {
            return MultiSelection(dataModel)
        }
    }
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
@Suppress("ComposableNaming")
@Composable
fun <M : SelectionModel<ListIndexModel.ListIndex>> ListView(
    items: Int,
    selectionMode: SelectionMode<M>,
    modifier: Modifier = Modifier,
    child: @Composable (index: Int) -> Unit,
): M {
    val dataModel = remember {
        ListStore<ListIndexModel.ListIndex>()
    }
    remember(items) {
        while (dataModel.size > items) {
            dataModel.removeLast()
        }
        while (dataModel.size < items) {
            dataModel.append(ListIndexModel.ListIndex(dataModel.size))
        }
    }
    val selectionModel = remember(dataModel) {
        selectionMode.createSelectionModel(dataModel)
    }
    ListView(selectionModel, modifier) {
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
    child: @Composable (item: T) -> Unit,
) {
    val compositionContext = rememberCompositionContext()

    ComposeNode<GtkComposeWidget<GTKListView>, GtkApplier>(
        factory = {
            LeafComposeNode(
                widget = GTKListView.builder()
                    .setFactory(createListItemFactory(compositionContext, child))
                    .build(),
            )
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(model) { this.widget.model = it }
        },
    )
}

@Composable
fun <Item : GObject> rememberNoSelectionModel(
    items: List<Item>,
): NoSelection<Item> =
    rememberSelectionModel(
        items = items,
        selectionModelFactory = { model -> NoSelection(model) },
    )

@Composable
fun <Item : GObject> rememberSingleSelectionModel(
    items: List<Item>,
): SingleSelection<Item> =
    rememberSelectionModel(
        items = items,
        selectionModelFactory = { model -> SingleSelection(model) },
    )

@Composable
fun <Item : GObject> rememberMultiSelectionModel(
    items: List<Item>,
): MultiSelection<Item> =
    rememberSelectionModel(
        items = items,
        selectionModelFactory = { model -> MultiSelection(model) },
    )

@Composable
private fun <Item : GObject, Model : SelectionModel<Item>> rememberSelectionModel(
    items: List<Item>,
    selectionModelFactory: (model: ListModel<Item>) -> Model,
): Model {
    val model = remember { ListStore<Item>() }

    remember(items) {
        while (model.size > items.size) {
            model.removeLast()
        }

        for (i in 0 until model.size) {
            if (model[i] != items[i]) {
                model[i] = items[i]
            }
        }

        while (model.size < items.size) {
            model.append(items[model.size])
        }
    }

    return remember { selectionModelFactory(model) }
}

/**
 * Creates a [SignalListItemFactory] where each element is provided by [child].
 */
private fun <T : GObject> createListItemFactory(
    compositionContext: CompositionContext,
    child: @Composable (T) -> Unit,
): SignalListItemFactory {
    val compositionMap = HashMap<ListItem, Composition>()
    val factory = SignalListItemFactory.builder().build()
    factory.onSetup { listItem ->
        listItem as ListItem
        compositionMap[listItem] = Composition(
            GtkApplier(GtkListItemComposeNode(listItem)),
            compositionContext,
        )
    }
    factory.onBind { listItem ->
        listItem as ListItem
        @Suppress("UNCHECKED_CAST")
        val item = listItem.item as T
        val composition = compositionMap[listItem]
        checkNotNull(composition)
        composition.setContent {
            child(item)
        }
    }
    factory.onTeardown { listItem ->
        listItem as ListItem
        val composition = compositionMap.remove(listItem)
        checkNotNull(composition)
        composition.dispose()
    }
    return factory
}
