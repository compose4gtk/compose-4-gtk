package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gio.File
import org.gnome.glib.GError
import org.gnome.gobject.ParamSpec
import org.gnome.gtk.GraphicsOffloadEnabled
import org.gnome.gtk.Video as GtkVideo

sealed interface VideoState {
    val video: GtkVideo?
    val duration: Long
    val ended: Boolean
    val error: GError?
    val hasAudio: Boolean
    val hasVideo: Boolean
    var loop: Boolean
    var muted: Boolean
    var volume: Double
    val playing: Boolean
    val prepared: Boolean
    val seekable: Boolean
    val seeking: Boolean
    val timestamp: Long
    fun play()
    fun pause()
    fun seek(timestamp: Long)
}

private class VideoStateImpl : VideoState {

    private var _video by mutableStateOf<GtkVideo?>(null)
    override var video: GtkVideo?
        get() = _video
        set(value) {
            check(_video == null) { "VideoState can be associated to a single Video" }
            requireNotNull(value)
            _video = value

            // Listen for mediaStream changes
            value.onNotify("media-stream") { _: ParamSpec? ->
                val stream = value.mediaStream ?: return@onNotify

                stream.onNotify("duration") { _: ParamSpec? ->
                    duration = stream.duration
                }

                stream.onNotify("ended") { _: ParamSpec? ->
                    ended = stream.ended
                }

                stream.onNotify("error") { _: ParamSpec? ->
                    error = stream.error
                }

                stream.onNotify("has-audio") { _: ParamSpec? ->
                    hasAudio = stream.hasAudio()
                }

                stream.onNotify("has-video") { _: ParamSpec? ->
                    hasVideo = stream.hasVideo()
                }

                stream.onNotify("loop") { _: ParamSpec? ->
                    loop = stream.loop
                }

                stream.onNotify("muted") { _: ParamSpec? ->
                    muted = stream.muted
                }

                stream.onNotify("volume") { _: ParamSpec? ->
                    if (!stream.muted) {
                        volume = stream.volume
                    }
                }

                stream.onNotify("playing") { _: ParamSpec? ->
                    playing = stream.playing
                }

                stream.onNotify("prepared") { _: ParamSpec? ->
                    prepared = stream.isPrepared
                }

                stream.onNotify("seekable") { _: ParamSpec? ->
                    seekable = stream.isSeekable
                }

                stream.onNotify("seeking") { _: ParamSpec? ->
                    seeking = stream.isSeeking
                }

                stream.onNotify("timestamp") { _: ParamSpec? ->
                    timestamp = stream.timestamp
                }
            }
        }

    override var duration by mutableLongStateOf(0L)
        private set

    override var ended by mutableStateOf(false)
        private set

    override var error by mutableStateOf<GError?>(null)
        private set

    override var hasAudio by mutableStateOf(false)
        private set

    override var hasVideo by mutableStateOf(false)
        private set

    private var _loop by mutableStateOf(false)
    override var loop
        get() = _loop
        set(value) {
            _loop = value
            video?.mediaStream?.loop = value
        }

    private var _muted by mutableStateOf(false)
    override var muted
        get() = _muted
        set(value) {
            _muted = value
            video?.mediaStream?.muted = value
            video?.mediaStream?.volume = volume
        }

    private var _volume by mutableDoubleStateOf(1.0)
    override var volume
        get() = _volume
        set(value) {
            _volume = value
            video?.mediaStream?.volume = value
        }

    override var playing by mutableStateOf(false)
        private set

    override var prepared by mutableStateOf(false)
        private set

    override var seekable by mutableStateOf(true)
        private set

    override var seeking by mutableStateOf(false)
        private set

    override var timestamp by mutableLongStateOf(0L)
        private set

    override fun play() {
        video?.mediaStream?.play()
    }

    override fun pause() {
        video?.mediaStream?.pause()
    }

    override fun seek(timestamp: Long) {
        video?.mediaStream?.seek(timestamp)
    }
}

@Composable
fun rememberVideoState(): VideoState {
    val state = remember { VideoStateImpl() }
    return state
}

@Composable
private fun <V : GtkComposeWidget<GtkVideo>> BaseVideo(
    creator: () -> V,
    modifier: Modifier = Modifier,
    file: File? = null,
    autoplay: Boolean = false,
    graphicsOffload: GraphicsOffloadEnabled = GraphicsOffloadEnabled.DISABLED,
    loop: Boolean = false,
) {
    ComposeNode<V, GtkApplier>(
        factory = creator,
        update = {
            set(modifier) { applyModifier(modifier) }
            set(file) { this.widget.file = it }
            set(autoplay) { this.widget.autoplay = it }
            set(graphicsOffload) { this.widget.graphicsOffload = it }
            set(loop) { this.widget.mediaStream?.loop = it }
        },
    )
}

/**
 * Creates a [org.gnome.gtk.Video] that displays a video whose stream
 * is controlled by a [VideoState].
 *
 * @param state The [VideoState] that controls the video's media stream.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param file The video file.
 * @param autoplay Whether the video automatically plays once it's loaded.
 * @param graphicsOffload Offloads the graphics which bypasses gsk rendering
 * by passing the content of its child directly to the compositor.
 */
@Composable
fun Video(
    state: VideoState,
    modifier: Modifier = Modifier,
    file: File? = null,
    autoplay: Boolean = false,
    graphicsOffload: GraphicsOffloadEnabled = GraphicsOffloadEnabled.DISABLED,
) {
    val stateImpl: VideoStateImpl = when (state) {
        is VideoStateImpl -> state
    }

    // Pause the stream so it does not continue in the background
    DisposableEffect(Unit) {
        onDispose {
            stateImpl.video?.mediaStream?.pause()
        }
    }

    BaseVideo(
        creator = {
            val gObject = GtkVideo()
            stateImpl.video = gObject
            LeafComposeNode(gObject)
        },
        modifier = modifier,
        file = file,
        autoplay = autoplay,
        graphicsOffload = graphicsOffload,
        loop = stateImpl.loop,
    )
}

/**
 * Creates a [org.gnome.gtk.Video] that displays a video.
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param file The video file.
 * @param autoplay Whether the video automatically plays once it's loaded.
 * @param graphicsOffload Offloads the graphics which bypasses gsk rendering
 * by passing the content of its child directly to the compositor.
 * @param loop Whether the video restarts when it reaches the end.
 */
@Composable
fun Video(
    modifier: Modifier = Modifier,
    file: File? = null,
    autoplay: Boolean = false,
    graphicsOffload: GraphicsOffloadEnabled = GraphicsOffloadEnabled.DISABLED,
    loop: Boolean = false,
) {
    BaseVideo(
        creator = {
            LeafComposeNode(GtkVideo())
        },
        modifier = modifier,
        file = file,
        autoplay = autoplay,
        graphicsOffload = graphicsOffload,
        loop = loop,
    )
}
