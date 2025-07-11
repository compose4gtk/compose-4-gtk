package io.github.compose4gtk.modifier

fun Modifier.cssClasses(vararg classes: String) = combine(
    apply = {
        for (cssClass in classes) {
            it.addCssClass(cssClass)
        }
    },
    undo = {
        for (cssClass in classes) {
            it.removeCssClass(cssClass)
        }
    },
)

fun Modifier.cssClasses(classes: List<String>) = combine(
    apply = {
        for (cssClass in classes) {
            it.addCssClass(cssClass)
        }
    },
    undo = {
        for (cssClass in classes) {
            it.removeCssClass(cssClass)
        }
    },
)
