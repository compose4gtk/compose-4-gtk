package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Orientation
import org.gnome.gtk.Separator

@Composable
fun VerticalSeparator(
    modifier: Modifier = Modifier,
) {
    Separator(modifier, Orientation.VERTICAL)
}

@Composable
fun HorizontalSeparator(
    modifier: Modifier = Modifier,
) {
    Separator(modifier, Orientation.HORIZONTAL)
}

@Composable
fun Separator(
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.HORIZONTAL,
) {
    ComposeNode<GtkComposeWidget<Separator>, GtkApplier>({
        LeafComposeNode(Separator.builder().build())
    }) {
        set(modifier) { applyModifier(it) }
        set(orientation) { widget.orientation = it }
    }
}
