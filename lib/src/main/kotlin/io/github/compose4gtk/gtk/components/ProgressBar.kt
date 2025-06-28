package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.remember
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.ProgressBar
import org.gnome.pango.EllipsizeMode

sealed interface ProgressBarState {
    var fraction: Double
    fun pulse()
    fun reset()
}

private class ProgressBarStateImpl : ProgressBarState {
    var progressBar: ProgressBar? = null
        set(value) {
            check(field == null) { "ProgressBarState can be associated to a single ProgressBar" }
            requireNotNull(value)
            field = value
        }

    override var fraction: Double = 0.0

    override fun pulse() {
        progressBar?.pulse()
    }

    override fun reset() {
        progressBar?.fraction = fraction
    }
}

/**
 * Creates and remembers a [ProgressBarState] for controlling a [ProgressBar] composable.
 *
 * @param fraction The level to which the progress bar is filled.
 */
@Composable
fun rememberProgressBarState(fraction: Double): ProgressBarState {
    val state = remember { ProgressBarStateImpl() }
    state.fraction = fraction
    return state
}

/**
 * Creates a [org.gnome.gtk.ProgressBar] that displays the progress of a long-running operation.
 *
 * @param state The shared [ProgressBarState] instance.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param ellipsize The mode used to ellipsize the text.
 * @param inverted Whether the progress bar is inverted.
 * @param pulseStep The fraction of total progress bar length
 * to move the bouncing block when [ProgressBarState.pulse] is called.
 * @param showText Whether the progress bar will show text next to the bar.
 * @param text The text showed next to the progress bar.
 */
@Composable
fun ProgressBar(
    state: ProgressBarState,
    modifier: Modifier = Modifier,
    ellipsize: EllipsizeMode = EllipsizeMode.NONE,
    inverted: Boolean = false,
    pulseStep: Double = 0.1,
    showText: Boolean = false,
    text: String? = null,
) {
    val stateImpl: ProgressBarStateImpl = when (state) {
        is ProgressBarStateImpl -> state
    }
    ComposeNode<GtkComposeWidget<ProgressBar>, GtkApplier>(
        factory = {
            val gObject = ProgressBar()
            stateImpl.progressBar = gObject
            LeafComposeNode(gObject)
        },
        update = {
            set(state.fraction) { this.widget.fraction = it }
            set(modifier) { applyModifier(it) }
            set(ellipsize) { this.widget.ellipsize = it }
            set(inverted) { this.widget.inverted = it }
            set(pulseStep) { this.widget.pulseStep = it }
            set(showText) { this.widget.showText = it }
            set(text) { this.widget.text = it }
        },
    )
}
