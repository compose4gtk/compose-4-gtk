package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.gdk.GLAPI
import org.gnome.gdk.GLContext
import org.gnome.gtk.GLArea

private class GtkGLAreaComposeNode(gObject: GLArea) : LeafComposeNode<GLArea>(gObject) {
    var onCreateContext: SignalConnection<GLArea.CreateContextCallback>? = null
    var onResize: SignalConnection<GLArea.ResizeCallback>? = null
    var onRender: SignalConnection<GLArea.RenderCallback>? = null
}

@Composable
fun GLArea(
    modifier: Modifier = Modifier,
    allowedApis: Set<GLAPI> = setOf(GLAPI.GL, GLAPI.GLES),
    onInit: ((GLContext, api: GLAPI) -> Unit)? = null,
    onRender: ((GLContext) -> Boolean)? = null,
    onDestroy: ((GLContext) -> Unit)? = null,
    onCreateContext: (() -> GLContext)? = null,
    onResize: ((width: Int, height: Int) -> Unit)? = null,
) {
    ComposeNode<GtkGLAreaComposeNode, GtkApplier>({
        GtkGLAreaComposeNode(
            GLArea().apply {
                var tickCallback: Int = -1

                onRealize {
                    makeCurrent()
                    onInit?.invoke(context, api.first())
                    tickCallback = addTickCallback { _, _ ->
                        queueRender()
                        true
                    }
                }

                onUnrealize {
                    removeTickCallback(tickCallback)
                    onDestroy?.invoke(context)
                }
            },
        )
    }) {
        set(modifier) { applyModifier(it) }
        set(allowedApis) {
            this.widget.allowedApis = allowedApis
        }
        set(onCreateContext) {
            this.onCreateContext?.disconnect()
            onCreateContext?.let { this.widget.onCreateContext(onCreateContext) }
        }
        set(onResize) {
            this.onResize?.disconnect()
            onResize?.let { this.widget.onResize(onResize) }
        }
        set(onRender) {
            this.onRender?.disconnect()
            onRender.let { this.widget.onRender(onRender) }
        }
    }
}
