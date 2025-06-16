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
    val progressBar: ProgressBar?
    var fraction: Double
    fun pulse()
    fun reset()
}

private class ProgressBarStateImpl : ProgressBarState {
    override var progressBar: ProgressBar? = null
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

@Composable
fun rememberProgressBarState(fraction: Double): ProgressBarState {
    val state = remember { ProgressBarStateImpl() }
    state.fraction = fraction
    return state
}

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
