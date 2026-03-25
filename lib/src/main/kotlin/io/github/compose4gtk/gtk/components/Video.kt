package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gio.File
import org.gnome.gtk.GraphicsOffloadEnabled
import org.gnome.gtk.Video as GtkVideo

@Composable
fun Video(
    modifier: Modifier = Modifier,
    file: File? = null,
    autoplay: Boolean = false,
    graphicsOffload: GraphicsOffloadEnabled = GraphicsOffloadEnabled.DISABLED,
    loop: Boolean = false,
) {
    ComposeNode<GtkComposeWidget<GtkVideo>, GtkApplier>(
        factory = {
            LeafComposeNode(GtkVideo())
        },
        update = {
            set(modifier) { applyModifier(modifier) }
            set(file) { this.widget.file = it }
            set(autoplay) { this.widget.autoplay = it }
            set(graphicsOffload) { this.widget.graphicsOffload = it }
            set(loop) { this.widget.loop = it }
        },
    )
}
