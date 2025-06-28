package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.WindowTitle

/**
 * Creates a [org.gnome.adw.WindowTitle] that helps to set a window’s title and subtitle.
 *
 * @param title The title to display.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param subtitle The subtitle to display.
 */
@Composable
fun WindowTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    ComposeNode<GtkComposeWidget<WindowTitle>, GtkApplier>({
        LeafComposeNode(WindowTitle.builder().build())
    }) {
        set(title) { this.widget.title = it }
        set(modifier) { applyModifier(it) }
        set(subtitle) { this.widget.subtitle = it }
    }
}
