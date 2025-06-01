package io.github.compose4gtk.modifier

fun Modifier.margin(margin: Int) = margin(margin, margin)
fun Modifier.margin(horizontal: Int, vertical: Int) = margin(horizontal, vertical, horizontal, vertical)
fun Modifier.margin(
    start: Int,
    top: Int,
    end: Int,
    bottom: Int,
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
