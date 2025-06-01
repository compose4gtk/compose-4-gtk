package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.WindowTitle

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
