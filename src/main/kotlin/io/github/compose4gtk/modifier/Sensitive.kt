package io.github.compose4gtk.modifier

fun Modifier.sensitive(sensitive: Boolean = true) = combine(
    apply = { it.sensitive = sensitive },
    undo = { it.sensitive = true },
)
