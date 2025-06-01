package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.setImage
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.StatusPage

@Composable
fun StatusPage(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageSource? = null,
    content: @Composable () -> Unit = {},
) {
    ComposeNode<GtkComposeWidget<StatusPage>, GtkApplier>(
        factory = {
            SingleChildComposeNode(
                StatusPage.builder().build(),
                set = { child = it },
            )
        },
        update = {
            set(title) { this.widget.title = it }
            set(modifier) { applyModifier(it) }
            set(description) { this.widget.description = it }
            set(icon) { img ->
                this.widget.setImage(
                    img,
                    getCurrentPaintable = { this.paintable },
                    setIcon = { this.iconName = it },
                    setPaintable = { this.paintable = it },
                )
            }
        },
        content = content,
    )
}
