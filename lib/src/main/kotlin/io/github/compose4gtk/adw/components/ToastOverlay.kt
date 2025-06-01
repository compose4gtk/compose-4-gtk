package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.remember
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.Toast
import org.gnome.adw.ToastOverlay

interface ToastOverlayScope {
    /**
     * Shows a Toast
     */
    fun addToast(toast: Toast)
    fun dismissAllToasts()
}

private class ToastOverlayScopeImpl : ToastOverlayScope {
    var toastOverlay: ToastOverlay? = null
    override fun addToast(toast: Toast) {
        toastOverlay!!.addToast(toast)
    }

    override fun dismissAllToasts() {
        toastOverlay?.dismissAll()
    }
}

@Composable
fun ToastOverlay(
    modifier: Modifier = Modifier,
    content: @Composable ToastOverlayScope.() -> Unit,
) {
    val overlayScope = remember { ToastOverlayScopeImpl() }
    ComposeNode<GtkComposeWidget<ToastOverlay>, GtkApplier>(
        factory = {
            val toastOverlay = ToastOverlay.builder().build()
            SingleChildComposeNode(
                toastOverlay,
                set = { toastOverlay.child = it },
            )
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(overlayScope) { it.toastOverlay = this.widget }
        },
        content = {
            overlayScope.content()
        },
    )
}
