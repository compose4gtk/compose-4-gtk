package io.github.compose4gtk.modifier

fun Modifier.margin(margin: Int) = margin(margin, margin)
fun Modifier.margin(horizontal: Int = 0, vertical: Int = 0) = margin(horizontal, vertical, horizontal, vertical)
fun Modifier.margin(
    start: Int = 0,
    top: Int = 0,
    end: Int = 0,
    bottom: Int = 0,
) = combine(
    apply = {
        it.marginStart = start
        it.marginTop = top
        it.marginEnd = end
        it.marginBottom = bottom
    },
    undo = {
        it.marginStart = 0
        it.marginTop = 0
        it.marginEnd = 0
        it.marginBottom = 0
    },
)
