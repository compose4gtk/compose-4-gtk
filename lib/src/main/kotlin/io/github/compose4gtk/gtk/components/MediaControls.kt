package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.MediaStream
import org.gnome.gtk.MediaControls as GtkMediaControls

/**
 * Creates a [org.gnome.gtk.MediaControls] that controls a [MediaStream].
 *
 * @param state The [VideoState] that controls the video's media stream.
 * @param modifier Compose [Modifier] for layout and styling.
 */
@Composable
fun MediaControls(
    state: VideoState,
    modifier: Modifier = Modifier,
) {
    ComposeNode<GtkComposeWidget<GtkMediaControls>, GtkApplier>(
        factory = {
            LeafComposeNode(GtkMediaControls())
        },
        update = {
            set(state.video?.mediaStream) { mediaStream: MediaStream? ->
                this.widget.mediaStream = mediaStream
            }
            set(modifier) { applyModifier(it) }
        },
    )
}
