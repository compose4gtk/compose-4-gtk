package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.gdk.GLAPI
import org.gnome.gdk.GLContext
import org.gnome.gtk.GLArea
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL46C
import org.lwjgl.opengl.GL46C.glGetError

private val logger = KotlinLogging.logger {}

private class GtkGLAreaComposeNode(gObject: GLArea) : LeafComposeNode<GLArea>(gObject) {
    var onCreateContext: SignalConnection<GLArea.CreateContextCallback>? = null
    var onResize: SignalConnection<GLArea.ResizeCallback>? = null
    var onRender: SignalConnection<GLArea.RenderCallback>? = null
}

@Composable
fun GLArea(
    onInit: (GLContext) -> Unit,
    onRender: (GLContext) -> Boolean,
    onDestroy: (GLContext) -> Unit,
    modifier: Modifier = Modifier,
    onCreateContext: (() -> GLContext)? = null,
    onResize: ((width: Int, height: Int) -> Unit)? = null,
) {
    ComposeNode<GtkGLAreaComposeNode, GtkApplier>({
        GtkGLAreaComposeNode(
            GLArea().apply {
                allowedApis = mutableSetOf(GLAPI.GL)
                var tickCallback: Int = -1

                onRealize {
                    makeCurrent()
                    GL.createCapabilities()
                    val error = glGetError()
                    if (error != GL46C.GL_NO_ERROR) {
                        logger.info { "OpenGL createCapabilities failed: $error" }
                        // some errors are non-blocking
                    }

                    onInit(context)

                    tickCallback = addTickCallback { _, _ ->
                        queueRender()
                        true
                    }
                }

                onUnrealize {
                    removeTickCallback(tickCallback)

                    onDestroy(context)
                }
            },
        )
    }) {
        set(modifier) { applyModifier(it) }
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
