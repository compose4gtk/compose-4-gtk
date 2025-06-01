package io.github.compose4gtk.gtk

import org.gnome.adw.SpinnerPaintable
import org.gnome.gdk.Texture
import org.gnome.gtk.Widget
import org.gnome.gdk.Paintable as GtkPaintable

sealed interface ImageSource {
    data class Icon(val iconName: String) : ImageSource

    fun interface PaintableFactory : ImageSource {
        fun create(): GtkPaintable
    }

    companion object {
        val spinner = PaintableFactory {
            SpinnerPaintable.builder().build()
        }

        fun forFile(file: org.gnome.gio.File) = PaintableFactory {
            Texture.fromFile(file)
        }

        fun forResource(resourcePath: String) = PaintableFactory {
            Texture.fromResource(resourcePath)
        }

        fun forTexture(texture: Texture) = PaintableFactory {
            texture
        }
    }
}

fun <W : Widget> W.setImage(
    image: ImageSource?,
    getCurrentPaintable: W.() -> GtkPaintable?,
    setPaintable: W.(GtkPaintable?) -> Unit,
    setIcon: W.(String?) -> Unit,
) {
    when (image) {
        is ImageSource.Icon -> {
            setPaintable(null, getCurrentPaintable, setPaintable)
            setIcon(image.iconName)
        }

        is ImageSource.PaintableFactory? -> {
            setPaintable(image?.create(), getCurrentPaintable, setPaintable)
        }
    }
}

fun <W : Widget> W.setPaintable(
    paintable: GtkPaintable?,
    getCurrentPaintable: W.() -> GtkPaintable?,
    setPaintable: W.(GtkPaintable?) -> Unit,
) {
    getCurrentPaintable()?.setWidget(null)
    setPaintable(paintable)
    paintable?.setWidget(this)
}

private fun GtkPaintable.setWidget(widget: Widget?) {
    if (this is SpinnerPaintable) {
        this.widget = widget
    }
}
