package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Label

// TODO: all other properties
@Composable
fun Label(
    text: String,
    modifier: Modifier = Modifier,
) {
    ComposeNode<GtkComposeWidget<Label>, GtkApplier>({
        LeafComposeNode(Label.builder().build())
    }) {
        set(modifier) { applyModifier(it) }
        set(text) { this.widget.text = it }
    }
}
