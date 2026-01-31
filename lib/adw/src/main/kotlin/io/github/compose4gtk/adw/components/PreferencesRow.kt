package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Updater
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.PreferencesRow as AdwPreferencesRow

@Composable
internal fun <W : GtkComposeWidget<AdwPreferencesRow>> PreferencesRow(
    creator: () -> W,
    updater: Updater<W>.() -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    selectable: Boolean = true,
    activatable: Boolean = true,
    titleSelectable: Boolean = false,
    useMarkup: Boolean = true,
    useUnderline: Boolean = false,
    content: @Composable () -> Unit = {},
) {
    ComposeNode<W, GtkApplier>(
        factory = creator,
        update = {
            set(title) { this.widget.title = it }
            set(modifier) { applyModifier(it) }
            set(selectable) { this.widget.selectable = it }
            set(activatable) { this.widget.activatable = it }
            set(titleSelectable) { this.widget.titleSelectable = it }
            set(useMarkup) { this.widget.useMarkup = it }
            set(useUnderline) { this.widget.useUnderline = it }
            updater()
        },
        content = {
            content()
        },
    )
}
