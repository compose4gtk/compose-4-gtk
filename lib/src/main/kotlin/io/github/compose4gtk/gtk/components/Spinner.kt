package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Spinner

@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    spinning: Boolean = false,
) {
    ComposeNode<GtkComposeWidget<Spinner>, GtkApplier>({
        LeafComposeNode(Spinner.builder().build())
    }) {
        set(modifier) { applyModifier(it) }
        set(spinning) { this.widget.spinning = it }
    }
}
