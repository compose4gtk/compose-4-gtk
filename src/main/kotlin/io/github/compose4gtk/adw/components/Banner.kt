package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.adw.Banner

private class AdwBannerComposeNode(
    gObject: Banner,
) : LeafComposeNode<Banner>(gObject) {
    var onButtonClicked: SignalConnection<Banner.ButtonClickedCallback>? = null
}

@Composable
fun Banner(
    modifier: Modifier = Modifier,
    title: String? = null,
    buttonLabel: String? = null,
    onButtonClick: (() -> Unit)? = null,
    revealed: Boolean = true,
    useMarkup: Boolean = true,
) {
    ComposeNode<AdwBannerComposeNode, GtkApplier>({
        AdwBannerComposeNode(Banner.builder().build())
    }) {
        set(modifier) { applyModifier(it) }
        set(title) { this.widget.title = it }
        set(buttonLabel) { this.widget.buttonLabel = it }
        set(onButtonClick) {
            this.onButtonClicked?.disconnect()
            if (it != null) {
                this.onButtonClicked = this.widget.onButtonClicked {
                    it()
                }
            } else {
                this.onButtonClicked = null
            }
        }
        set(revealed) { this.widget.revealed = it }
        set(useMarkup) { this.widget.useMarkup = it }
    }
}
