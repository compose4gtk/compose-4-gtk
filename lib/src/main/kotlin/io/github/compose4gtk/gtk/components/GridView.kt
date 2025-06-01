package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.rememberCompositionContext
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gio.ListIndexModel
import org.gnome.gobject.GObject
import org.gnome.gtk.ListTabBehavior
import org.gnome.gtk.SelectionModel
import org.gnome.gtk.GridView as GTKGridView

/**
 * Creates a [org.gnome.gtk.GridView] with [items] items.
 * Each element is a composable created using [child].
 *
 * The created [org.gnome.gio.ListModel] will have the specified [selectionMode].
 * You can use `GridView(model){ ... }` if you want more customization options.
 *
 * @return the selection model you can use to manage the selection
 */
@Suppress("ComposableNaming", "ContentEmitterReturningValues")
@Composable
fun <M : SelectionModel<ListIndexModel.ListIndex>> GridView(
    items: Int,
    selectionMode: SelectionMode<M>,
    modifier: Modifier = Modifier,
    enableRubberband: Boolean = false,
    minColumns: Int = 1,
    maxColumns: Int = 7,
    singleClickActivate: Boolean = false,
    tabBehaviour: ListTabBehavior = ListTabBehavior.ALL,
    onActivate: ((position: Int) -> Unit)? = null,
    child: @Composable (index: Int) -> Unit,
): M {
    val selectionModel = rememberSelectionModel(itemsCount = items, selectionMode = selectionMode)
    GridView(
        model = selectionModel,
        modifier = modifier,
        enableRubberband = enableRubberband,
        minColumns = minColumns,
        maxColumns = maxColumns,
        singleClickActivate = singleClickActivate,
        tabBehaviour = tabBehaviour,
        onActivate = onActivate,
    ) {
        child(it.index)
    }
    return selectionModel
}

/**
 * Creates a [org.gnome.gtk.GridView] bound to the given [model].
 * Each element is a composable created using [child].
 */
@Composable
fun <T : GObject> GridView(
    model: SelectionModel<T>,
    modifier: Modifier = Modifier,
    enableRubberband: Boolean = false,
    minColumns: Int = 1,
    maxColumns: Int = 7,
    singleClickActivate: Boolean = false,
    tabBehaviour: ListTabBehavior = ListTabBehavior.ALL,
    onActivate: ((position: Int) -> Unit)? = null,
    child: @Composable (item: T) -> Unit,
) {
    val compositionContext = rememberCompositionContext()

    ComposeNode<BaseListComposeNode<GTKGridView, GTKGridView.ActivateCallback>, GtkApplier>(
        factory = {
            BaseListComposeNode(
                gObject = GTKGridView.builder()
                    .setFactory(createListItemFactory(compositionContext, child))
                    .build(),
            )
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(model) { this.widget.model = it }
            set(enableRubberband) { this.widget.enableRubberband = it }
            set(minColumns) { this.widget.minColumns = it }
            set(maxColumns) { this.widget.maxColumns = it }
            set(singleClickActivate) { this.widget.singleClickActivate = it }
            set(tabBehaviour) { this.widget.tabBehavior = it }
            set(onActivate) {
                this.onActivate?.disconnect()
                this.onActivate = this.widget.onActivate(it)
            }
        },
    )
}
