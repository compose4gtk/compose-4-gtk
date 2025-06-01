package io.github.compose4gtk.modifier

fun Modifier.cssClasses(vararg classes: String) = combine(
    apply = { it.cssClasses = classes },
    undo = { it.cssClasses = emptyArray() },
)

fun Modifier.cssClasses(classes: List<String>) = combine(
    apply = { it.cssClasses = classes.toTypedArray() },
    undo = { it.cssClasses = emptyArray() },
)
