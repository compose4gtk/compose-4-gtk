package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Orientation
import org.gnome.adw.Clamp as GtkClamp

@Composable
fun VerticalClamp(
    modifier: Modifier = Modifier,
    maximumSize: Int = 600,
    tighteningThreshold: Int = 400,
    content: @Composable () -> Unit,
) {
    Clamp(modifier, Orientation.VERTICAL, maximumSize, tighteningThreshold, content)
}

@Composable
fun HorizontalClamp(
    modifier: Modifier = Modifier,
    maximumSize: Int = 600,
    tighteningThreshold: Int = 400,
    content: @Composable () -> Unit,
) {
    Clamp(modifier, Orientation.HORIZONTAL, maximumSize, tighteningThreshold, content)
}

@Composable
fun Clamp(
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.HORIZONTAL,
    maximumSize: Int = 600,
    tighteningThreshold: Int = 400,
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeWidget<GtkClamp>, GtkApplier>(
        factory = {
            SingleChildComposeNode(
                GtkClamp.builder().build(),
                set = { child = it },
            )
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(orientation) { this.widget.orientation = it }
            set(maximumSize) { this.widget.maximumSize = it }
            set(tighteningThreshold) { this.widget.tighteningThreshold = it }
        },
        content = content,
    )
}
