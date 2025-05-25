package io.github.compose4gtk.modifier

fun Modifier.expand(expand: Boolean = true) = expandVertically(expand).expandHorizontally(expand)

fun Modifier.expandVertically(expand: Boolean = true) = combine(
    apply = { it.vexpand = expand },
    undo = { it.vexpand = false },
)

fun Modifier.expandHorizontally(expand: Boolean = true) = combine(
    apply = { it.hexpand = expand },
    undo = { it.hexpand = false },
)
