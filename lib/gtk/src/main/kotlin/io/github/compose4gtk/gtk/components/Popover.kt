package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.remember
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.gdk.Rectangle
import org.gnome.gtk.Popover
import org.javagi.gobject.SignalConnection

private val logger = KotlinLogging.logger {}

private class GtkPopoverComposeNode(gObject: Popover) : SingleChildComposeNode<Popover>(
    widget = gObject,
    set = { child = it },
) {
    var onClose: SignalConnection<Popover.ClosedCallback>? = null
}

sealed interface PopoverScope {
    var popover: Popover?
    fun setPointingTo(rectangle: Rectangle? = null)
    fun popup()
    fun popdown()
}

private class PopoverScopeImpl : PopoverScope {
    override var popover: Popover? = null
    override fun setPointingTo(rectangle: Rectangle?) {
        val p = popover
        if (p == null) {
            logger.warn { "Cannot set pointing to: Popover not initialized yet" }
            return
        }
        p.setPointingTo(rectangle)
    }

    override fun popup() {
        val p = popover
        if (p == null) {
            logger.warn { "Cannot popup: Popover not initialized yet" }
            return
        }
        p.popup()
    }

    override fun popdown() {
        val p = popover
        if (p == null) {
            logger.warn { "Cannot popdown: Popover not initialized yet" }
            return
        }
        p.popdown()
    }
}

@Composable
fun Popover(
    trigger: @Composable PopoverScope.() -> Unit,
    modifier: Modifier = Modifier,
    autoHide: Boolean = true,
    hasArrow: Boolean = true,
    onClose: PopoverScope.() -> Unit = {},
    content: @Composable PopoverScope.() -> Unit,
) {
    val scope = remember { PopoverScopeImpl() }

    ComposeNode<GtkPopoverComposeNode, GtkApplier>(
        factory = {
            val popover = Popover()
            scope.popover = popover
            GtkPopoverComposeNode(popover)
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(autoHide) { this.widget.autohide = it }
            set(hasArrow) { this.widget.hasArrow = it }
            set(onClose) {
                this.onClose?.disconnect()
                this.onClose = this.widget.onClosed {
                    scope.onClose()
                }
            }
        },
        content = { scope.content() },
    )
    scope.trigger()
}
