package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.setPaintable
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.ContentFit
import org.gnome.gtk.Picture

/**
 * Creates a [org.gnome.gtk.Picture] that displays a picture.
 *
 * @param image The image to display.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param alternativeText An alternative textual description for the picture content.
 * @param canShrink Whether the picture can be made smaller than its content.
 * @param contentFit How the content should be resized to fit.
 */
@Composable
fun Picture(
    image: ImageSource.PaintableFactory?,
    modifier: Modifier = Modifier,
    alternativeText: String? = null,
    canShrink: Boolean = true,
    contentFit: ContentFit = ContentFit.CONTAIN,
) {
    ComposeNode<GtkComposeWidget<Picture>, GtkApplier>({
        LeafComposeNode(Picture.builder().build())
    }) {
        set(modifier) { applyModifier(it) }
        set(image) { img ->
            this.widget.setPaintable(
                img?.create(),
                getCurrentPaintable = { paintable },
                setPaintable = { paintable = it },
            )
        }
        set(alternativeText) { this.widget.alternativeText = it }
        set(canShrink) { this.widget.canShrink = it }
        set(contentFit) { this.widget.contentFit = it }
    }
}
