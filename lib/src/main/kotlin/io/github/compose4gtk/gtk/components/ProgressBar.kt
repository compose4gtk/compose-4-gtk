package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import kotlinx.coroutines.delay
import org.gnome.gtk.ProgressBar
import org.gnome.pango.EllipsizeMode

@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    pulse: Boolean = false,
    pulseInterval: Long = 250L,
    ellipsize: EllipsizeMode = EllipsizeMode.NONE,
    fraction: Double = 0.0,
    inverted: Boolean = false,
    pulseStep: Double = 0.1,
    showText: Boolean = false,
    text: String? = null,
) {
    val progressBar = remember { ProgressBar.builder().build() }

    LaunchedEffect(pulse, pulseInterval) {
        while (pulse) {
            progressBar.pulse()
            delay(pulseInterval)
        }
        progressBar.fraction = fraction
    }

    ComposeNode<GtkComposeWidget<ProgressBar>, GtkApplier>(
        factory = {
            LeafComposeNode(progressBar)
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(ellipsize) { this.widget.ellipsize = it }
            set(fraction) { this.widget.fraction = it }
            set(inverted) { this.widget.inverted = it }
            set(pulseStep) { this.widget.pulseStep = it }
            set(showText) { this.widget.showText = it }
            set(text) { this.widget.text = it }
        },
    )
}
