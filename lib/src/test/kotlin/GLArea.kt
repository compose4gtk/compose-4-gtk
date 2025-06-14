import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.GLArea
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.expand
import io.github.compose4gtk.modifier.sizeRequest
import org.gnome.gdk.GLAPI
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengles.GLES
import org.lwjgl.opengles.GLES20

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Test", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar()

                var api: GLAPI? by remember { mutableStateOf(null) }
                GLArea(
                    modifier = Modifier
                        .sizeRequest(500, 500)
                        .expand(true),
                    onInit = { context, currentApi ->
                        when (currentApi) {
                            GLAPI.GL -> {
                                GL.createCapabilities()
                                GL11.glClearColor(1f, .5f, .2f, 1f)
                            }
                            GLAPI.GLES -> {
                                GLES.createCapabilities()
                                GLES20.glClearColor(1f, .5f, .2f, 1f)
                            }
                        }

                        api = currentApi
                    },
                    onRender = { context ->
                        when (api) {
                            GLAPI.GL -> {
                                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
                                true
                            }
                            GLAPI.GLES -> {
                                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                                true
                            }
                            null ->
                                false
                        }
                    },
                )
            }
        }
    }
}
