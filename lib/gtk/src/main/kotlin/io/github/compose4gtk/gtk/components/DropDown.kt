package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.rememberCompositionContext
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gobject.GObject
import org.gnome.gobject.ParamSpec
import org.gnome.gtk.DropDown
import org.gnome.gtk.SingleSelection
import org.javagi.gobject.SignalConnection

private class GtkDropDownComposeNode(gObject: DropDown) : LeafComposeNode<DropDown>(gObject) {
    var onSelected: SignalConnection<*>? = null
}

/**
 * @param onSelectionChanges a callback invoked when the selected item changes.
 *        At the beginning, the firest item is selected
 * @param item the composable to render items in the dropdown list
 * @param selectedItem the composable to render the selected item in the button. Defaults to [item]
 */
@Composable
fun <T : GObject> DropDown(
    model: SingleSelection<T>,
    onSelectionChanges: (selectedItem: T) -> Unit,
    item: @Composable (item: T) -> Unit,
    modifier: Modifier = Modifier,
    showArrow: Boolean = true,
    selectedItem: @Composable (item: T) -> Unit = { item(it) },
) {
    val compositionContext = rememberCompositionContext()
    ComposeNode<GtkDropDownComposeNode, GtkApplier>({
        GtkDropDownComposeNode(
            DropDown.builder()
                .setListFactory(createListItemFactory(compositionContext, item))
                .setFactory(createListItemFactory(compositionContext, selectedItem))
                .build(),
        )
    }) {
        set(modifier) { applyModifier(it) }
        set(model) { this.widget.model = it }
        set(showArrow) { this.widget.showArrow = it }
        set(onSelectionChanges) { onSelectionChanges ->
            this.onSelected?.disconnect()
            this.onSelected = this.widget.connect("notify::selected-item") { _: ParamSpec ->
                @Suppress("UNCHECKED_CAST")
                val selection = widget.selectedItem as T
                onSelectionChanges(selection)
            }
        }
    }
}
