package io.github.compose4gtk

import androidx.compose.runtime.AbstractApplier

internal class GtkApplier(root: GtkComposeNode) : AbstractApplier<GtkComposeNode>(root) {
    override fun insertBottomUp(index: Int, instance: GtkComposeNode) = Unit
    override fun insertTopDown(index: Int, instance: GtkComposeNode) {
        current.addNode(index, instance)
    }

    override fun remove(index: Int, count: Int) {
        repeat(count) {
            current.removeNode(index)
        }
    }

    override fun move(from: Int, to: Int, count: Int) {
        TODO("Move not yet implemented")
    }

    override fun onClear() {
        current.clearNodes()
    }
}
