import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import io.github.oshai.kotlinlogging.KotlinLogging
import org.joml.Math.toRadians
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_FALSE
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glDrawArrays
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glBufferData
import org.lwjgl.opengl.GL15.glDeleteBuffers
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL20.GL_COMPILE_STATUS
import org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL20.GL_VERTEX_SHADER
import org.lwjgl.opengl.GL20.glAttachShader
import org.lwjgl.opengl.GL20.glCompileShader
import org.lwjgl.opengl.GL20.glCreateProgram
import org.lwjgl.opengl.GL20.glCreateShader
import org.lwjgl.opengl.GL20.glDeleteProgram
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glGetShaderInfoLog
import org.lwjgl.opengl.GL20.glGetShaderi
import org.lwjgl.opengl.GL20.glGetUniformLocation
import org.lwjgl.opengl.GL20.glLinkProgram
import org.lwjgl.opengl.GL20.glShaderSource
import org.lwjgl.opengl.GL20.glUniformMatrix4fv
import org.lwjgl.opengl.GL20.glUseProgram
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glDeleteVertexArrays
import org.lwjgl.opengl.GL30.glGenVertexArrays

private val logger = KotlinLogging.logger {}

private val triangleVertices = floatArrayOf(
    -0.5f, -0.5f, 0.0f,
    0.5f, -0.5f, 0.0f,
    0.0f, 0.5f, 0.0f,
)
private val vertexShaderSource = """
    #version 330 core
    layout (location = 0) in vec3 aPos;
    uniform mat4 model;
    uniform mat4 projection;
    void main() {
        gl_Position = projection * model * vec4(aPos.x, aPos.y, aPos.z, 1.0);
    }
""".trimIndent()
private val fragmentShaderSource = """
    #version 330 core
    out vec4 FragColor;
    void main() {
        FragColor = vec4(1.0, 0.0, 0.0, 1.0); // Rouge
    }
""".trimIndent()

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Test", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar()

                var vaoId by remember { mutableIntStateOf(-1) }
                var vboId by remember { mutableIntStateOf(-1) }
                var shaderProgramId by remember { mutableIntStateOf(-1) }
                var modelMatrixUniformLocation by remember { mutableIntStateOf(-1) }
                val modelMatrix = remember { Matrix4f().identity() }
                var projectionMatrixUniformLocation by remember { mutableIntStateOf(-1) }
                val projectionMatrix = remember { Matrix4f() }

                GLArea(
                    modifier = Modifier
                        .sizeRequest(500, 500)
                        .expand(true),
                    onInit = {
                        // create vertex array object
                        vaoId = glGenVertexArrays()
                        glBindVertexArray(vaoId)

                        // create vertex buffer object
                        vboId = glGenBuffers()
                        glBindBuffer(GL_ARRAY_BUFFER, vboId)
                        glBufferData(GL_ARRAY_BUFFER, triangleVertices, GL_STATIC_DRAW)

                        // create and compile shaders
                        val vertexShader = createShader(GL_VERTEX_SHADER, vertexShaderSource)
                        val fragmentShader = createShader(GL_FRAGMENT_SHADER, fragmentShaderSource)

                        // create shader program
                        shaderProgramId = glCreateProgram()
                        glAttachShader(shaderProgramId, vertexShader!!)
                        glAttachShader(shaderProgramId, fragmentShader!!)
                        glLinkProgram(shaderProgramId)

                        // configure vertices
                        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
                        glEnableVertexAttribArray(0)

                        // get uniform locations
                        modelMatrixUniformLocation = glGetUniformLocation(shaderProgramId, "model")
                        projectionMatrixUniformLocation = glGetUniformLocation(shaderProgramId, "projection")

                        // set background color
                        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
                    },
                    onDestroy = {
                        // free resources
                        glDeleteVertexArrays(vaoId)
                        glDeleteBuffers(vboId)
                        glDeleteProgram(shaderProgramId)
                    },
                    onRender = { context ->
                        glClear(GL_COLOR_BUFFER_BIT)

                        // draw triangle
                        glUseProgram(shaderProgramId)

                        modelMatrix.rotateZ(toRadians(1.0).toFloat())
                        glUniformMatrix4fv(modelMatrixUniformLocation, false, modelMatrix.get(FloatArray(16)))

                        glBindVertexArray(vaoId)
                        glDrawArrays(GL_TRIANGLES, 0, 3)

                        true
                    },
                    onResize = { w, h ->
                        // preserve aspect ratio on resize
                        val aspectRatio = w.toFloat() / h.toFloat()
                        projectionMatrix.identity()
                        if (w > h) {
                            projectionMatrix.ortho(-aspectRatio, aspectRatio, -1.0f, 1.0f, -1.0f, 1.0f)
                        } else {
                            projectionMatrix.ortho(-1.0f, 1.0f, -1.0f / aspectRatio, 1.0f / aspectRatio, -1.0f, 1.0f)
                        }

                        glUseProgram(shaderProgramId)
                        glUniformMatrix4fv(projectionMatrixUniformLocation, false, projectionMatrix.get(FloatArray(16)))
                    },
                )
            }
        }
    }
}

private fun createShader(type: Int, source: String): Int? {
    val shader = glCreateShader(type)
    glShaderSource(shader, source)
    glCompileShader(shader)

    if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
        val infoLog = glGetShaderInfoLog(shader)
        logger.info { "Shader compilation error: $infoLog" }
        return null
    }

    return shader
}
