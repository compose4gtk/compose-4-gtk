package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.setImage
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.IconSize
import org.gnome.gtk.Image

/**
 * The size of the GTK [Image].
 * The possible values correspond to GTK's [IconSize], plus the [Specific] class to represent
 * an exact value that will be set with [Image.setPixelSize].
 */
sealed interface ImageSize {
    data object Normal : ImageSize
    data object Large : ImageSize
    data object Inherit : ImageSize
    data class Specific(val sizePx: Int) : ImageSize
}

/**
 * Composable for GTK's [org.gnome.gtk.Image].
 * Generally used to display an icon.
 */
@Composable
fun Image(
    image: ImageSource?,
    modifier: Modifier = Modifier,
    iconSize: ImageSize = ImageSize.Inherit,
) {
    ComposeNode<GtkComposeWidget<Image>, GtkApplier>({
        LeafComposeNode(Image.builder().build())
    }) {
        set(modifier) { applyModifier(it) }
        set(image) { img ->
            val size = widget.iconSize
            widget.setImage(
                img,
                setIcon = { setFromIconName(it) },
                setPaintable = { setFromPaintable(it) },
                getCurrentPaintable = { paintable },
            )
            // It seems like setting a new image will ignore the previous icon size. This is a trick to restore it
            widget.iconSize = IconSize.INHERIT
            widget.iconSize = size
        }
        set(iconSize) {
            when (it) {
                ImageSize.Inherit -> {
                    widget.pixelSize = -1
                    widget.iconSize = IconSize.INHERIT
                }

                ImageSize.Normal -> {
                    widget.pixelSize = -1
                    widget.iconSize = IconSize.NORMAL
                }

                ImageSize.Large -> {
                    widget.pixelSize = -1
                    widget.iconSize = IconSize.LARGE
                }

                is ImageSize.Specific -> {
                    widget.pixelSize = it.sizePx
                }
            }
        }
    }
}
