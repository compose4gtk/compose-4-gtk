package io.github.compose4gtk.modifier

fun Modifier.tooltipText(text: String): Modifier = combine(
    apply = {
        it.tooltipText = text
    },
    undo = {
        it.tooltipText = null
    },
)

fun Modifier.tooltipMarkup(markup: String): Modifier = combine(
    apply = {
        it.tooltipMarkup = markup
    },
    undo = {
        it.tooltipMarkup = null
    },
)
