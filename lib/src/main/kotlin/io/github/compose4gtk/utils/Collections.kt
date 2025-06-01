package io.github.compose4gtk.utils

fun <T> MutableList<T>.inPlaceMap(f: (T) -> T) {
    forEachIndexed { index, t ->
        this[index] = f(t)
    }
}
