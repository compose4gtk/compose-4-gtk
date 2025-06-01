package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.setImage
import io.github.compose4gtk.gtk.setPaintable
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.Avatar

@Composable
fun Avatar(
    image: ImageSource?,
    text: String,
    modifier: Modifier = Modifier,
    showInitials: Boolean = false,
    size: Int = -1,
) {
    ComposeNode<GtkComposeWidget<Avatar>, GtkApplier>({
        LeafComposeNode(Avatar.builder().build())
    }) {
        set(modifier) { applyModifier(it) }
        set(image) { img ->
            this.widget.setImage(
                img,
                getCurrentPaintable = { this.customImage },
                setIcon = { this.iconName = it },
                setPaintable = { this.customImage = it },
            )
        }
        set(text) { this.widget.text = it }
        set(showInitials) { this.widget.showInitials = it }
        set(size) { this.widget.size = it }
    }
}
