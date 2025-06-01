package io.github.compose4gtk

import io.github.compose4gtk.modifier.Modifier
import org.gnome.gobject.GObject
import org.gnome.gtk.Widget

/**
 * A base node in the Compose tree.
 *
 * TODO: can we achieve the same using composition instead of inheritance?
 */
interface GtkComposeNode {
    fun addNode(index: Int, child: GtkComposeNode)
    fun removeNode(index: Int)
    fun clearNodes()
}

/**
 * A node in the Compose tree that corresponds to a GTK [Widget].
 */
abstract class GtkComposeWidget<out W : Widget>(val widget: W) : GtkComposeNode {
    var modifier: Modifier = Modifier

    fun applyModifier(modifier: Modifier) {
        this.modifier.undo(widget)
        this.modifier = modifier
        this.modifier.apply(widget)
    }
}

/**
 * A node in the Compose tree that corresponds to a GTK [Widget] that can contain other widgets.
 */
abstract class GtkComposeContainer<out W : Widget>(widget: W) : GtkComposeWidget<W>(widget) {

    final override fun addNode(index: Int, child: GtkComposeNode) {
        require(child is GtkComposeWidget<*>) {
            "GtkComposeContainer only works with GtkComposeWidget"
        }
        addNode(index, child)
    }

    protected abstract fun addNode(index: Int, child: GtkComposeWidget<Widget>)
}

internal open class LeafComposeNode<G : Widget>(widget: G) : GtkComposeWidget<G>(widget) {
    override fun addNode(index: Int, child: GtkComposeNode) = throw UnsupportedOperationException()
    override fun removeNode(index: Int) = throw UnsupportedOperationException()
    override fun clearNodes() = throw UnsupportedOperationException()
}

internal open class SingleChildComposeNode<W : Widget>(
    widget: W,
    val set: W.(Widget?) -> Unit,
) : GtkComposeContainer<W>(widget) {
    private val stack = mutableListOf<Widget>()

    private fun recompute() {
        val widget = stack.lastOrNull()
        this.widget.set(widget)
    }

    override fun addNode(index: Int, child: GtkComposeWidget<Widget>) {
        check(stack.isEmpty()) {
            "${widget.javaClass.simpleName} can have at most one child node"
        }
        stack.add(child.widget)
        recompute()
    }

    override fun removeNode(index: Int) {
        stack.removeFirst()
        recompute()
    }

    override fun clearNodes() {
        stack.clear()
        recompute()
    }
}

internal class VirtualComposeNode<G : GObject>(
    val nodeCreator: (G) -> GtkComposeNode,
) : GtkComposeNode {
    private var parentCreator: GtkComposeNode? = null
    private val children = mutableListOf<GtkComposeNode>()

    override fun addNode(index: Int, child: GtkComposeNode) {
        children.add(index, child)
        parentCreator?.addNode(index, child)
    }

    override fun removeNode(index: Int) {
        children.removeAt(index)
        parentCreator?.removeNode(index)
    }

    override fun clearNodes() {
        children.clear()
        parentCreator?.clearNodes()
    }

    fun setParent(parent: G?) {
        parentCreator?.clearNodes()
        parentCreator = if (parent != null) {
            nodeCreator(parent).also {
                children.forEachIndexed { index, child -> it.addNode(index, child) }
            }
        } else {
            null
        }
    }
}

internal class VirtualComposeNodeContainer<W : Widget>(widget: W) : GtkComposeWidget<W>(widget) {
    private val children = mutableListOf<VirtualComposeNode<W>>()
    override fun addNode(index: Int, child: GtkComposeNode) {
        @Suppress("UNCHECKED_CAST")
        child as VirtualComposeNode<W>
        children.add(index, child)
        child.setParent(widget)
    }

    override fun removeNode(index: Int) {
        children.removeAt(index).setParent(null)
    }

    override fun clearNodes() {
        children.forEach { it.setParent(null) }
        children.clear()
    }
}

internal abstract class GtkContainerComposeNode<W : Widget>(widget: W) : GtkComposeContainer<W>(widget) {
    private val _children = mutableListOf<Widget>()
    protected val children: List<Widget> = _children
    override fun addNode(index: Int, child: GtkComposeWidget<Widget>) {
        val childWidget = child.widget
        _children.add(index, childWidget)
    }

    override fun removeNode(index: Int) {
        _children.removeAt(index)
    }

    override fun clearNodes() {
        _children.clear()
    }

    companion object {
        fun <W : Widget> appendOnly(
            widget: W,
            add: W.(Widget) -> Unit,
            remove: W.(Widget) -> Unit,
        ) = object : GtkContainerComposeNode<W>(widget) {
            override fun addNode(index: Int, child: GtkComposeWidget<Widget>) {
                val toReinsert = children.drop(index)
                super.addNode(index, child)
                toReinsert.forEach { widget.remove(it) }
                widget.add(child.widget)
                toReinsert.forEach { widget.add(it) }
            }

            override fun removeNode(index: Int) {
                widget.remove(children[index])
                super.removeNode(index)
            }

            override fun clearNodes() {
                children.forEach { widget.remove(it) }
                super.clearNodes()
            }
        }
    }
}
