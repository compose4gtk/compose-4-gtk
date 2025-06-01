package io.github.compose4gtk.modifier

import org.gnome.gtk.Widget

interface Modifier {
    fun apply(w: Widget)
    fun undo(w: Widget)

    companion object : Modifier {
        override fun apply(w: Widget) = Unit
        override fun undo(w: Widget) = Unit

        fun of(
            apply: (Widget) -> Unit,
            undo: (Widget) -> Unit,
        ) = object : Modifier {
            override fun apply(w: Widget) = apply(w)
            override fun undo(w: Widget) = undo(w)
        }
    }
}

operator fun Modifier.plus(another: Modifier): Modifier {
    val me = this
    return Modifier.of(
        apply = {
            me.apply(it)
            another.apply(it)
        },
        undo = {
            another.undo(it)
            me.undo(it)
        },
    )
}

fun Modifier.combine(
    apply: (Widget) -> Unit,
    undo: (Widget) -> Unit,
) = this + Modifier.of(apply, undo)
