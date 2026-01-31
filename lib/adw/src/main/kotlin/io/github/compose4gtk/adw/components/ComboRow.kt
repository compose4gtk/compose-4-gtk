package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.gtk.components.createListItemFactory
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gobject.GObject
import org.gnome.gobject.ParamSpec
import org.gnome.gtk.SingleSelection
import org.gnome.gtk.StringList
import org.javagi.gobject.SignalConnection
import org.gnome.adw.ComboRow as AdwComboRow

private class AdwComboRowComposeNode(gObject: AdwComboRow) : LeafComposeNode<AdwComboRow>(gObject) {
    var onSelected: SignalConnection<*>? = null
}

/**
 * Creates a [org.gnome.adw.ComboRow] used to choose from a list of items.
 *
 * @param items The list of string options to display in the combo box.
 * @param selectedIndex The index of the currently selected item.
 * @param title The title displayed on the row.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param onSelectedChange Callback invoked when the selected item changes, providing the new index.
 * @param activatable Whether the row can be activated (affects focus and accessibility).
 * @param titleSelectable Whether the user can select the title text.
 * @param useMarkup Whether the title and subtitle use Pango markup.
 * @param useUnderline Whether an underscore in the title or subtitle indicates a mnemonic.
 * @param subtitle Optional subtitle displayed below the title.
 * @param subtitleLines The number of lines after which the subtitle is ellipsized (0 = no limit).
 * @param subtitleSelectable Whether the user can select the subtitle text.
 * @param titleLines The number of lines after which the title is ellipsized (0 = no limit).
 */
@Composable
fun ComboRow(
    items: List<String>,
    selectedIndex: Int,
    title: String,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    subtitle: String? = null,
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
                // Observe property changes for "selected" (since it's not a proper signal)
                // https://gnome.pages.gitlab.gnome.org/libadwaita/doc/1.8/property.ComboRow.selected.html
                this.onSelected = this.widget.connect("notify::selected") { _: ParamSpec ->
                    pendingChange += 1
                    onSelectionChanges(this.widget.selected)
                }
            }
        },
        title = title,
        modifier = modifier,
        activatable = activatable,
        titleSelectable = titleSelectable,
        useMarkup = useMarkup,
        useUnderline = useUnderline,
        subtitle = subtitle,
        subtitleLines = subtitleLines,
        subtitleSelectable = subtitleSelectable,
        titleLines = titleLines,
    )
}

/**
 * Creates a [org.gnome.adw.ComboRow] used to choose from a list of items.
 *
 * @param T The [org.gnome.gobject.GObject] type used by the model.
 * @param model A [SingleSelection] wrapping the list model containing the available items.
 * @param onSelectedChange Callback invoked when the selected item changes, providing the new [T] instance.
 * @param item Composable used to render each item in the dropdown list.
 * @param title The title displayed on the row.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param activatable Whether the row can be activated (affects focus and accessibility).
 * @param titleSelectable Whether the user can select the title text.
 * @param useMarkup Whether the title and subtitle use Pango markup.
 * @param useUnderline Whether an underscore in the title or subtitle indicates a mnemonic.
 * @param subtitle Optional subtitle displayed below the title.
 * @param subtitleLines The number of lines after which the subtitle is ellipsized (0 = no limit).
 * @param subtitleSelectable Whether the user can select the subtitle text.
 * @param titleLines The number of lines after which the title is ellipsized (0 = no limit).
 * @param selectedItem Optional composable used to render the currently selected item in the collapsed state,
 * by default, it reuses [item].
 */
@Composable
fun <T : GObject> ComboRow(
    model: SingleSelection<T>,
    onSelectedChange: (selectedItem: T) -> Unit,
    item: @Composable (T) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    subtitle: String? = null,
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
        modifier = modifier,
        activatable = activatable,
        titleSelectable = titleSelectable,
        useMarkup = useMarkup,
        useUnderline = useUnderline,
        subtitle = subtitle,
        subtitleLines = subtitleLines,
        subtitleSelectable = subtitleSelectable,
        titleLines = titleLines,
    )
}
