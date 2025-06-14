package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.ButtonContent

@Composable
fun ButtonContent(
    label: String?,
    iconName: ImageSource.Icon,
    modifier: Modifier = Modifier,
    useUnderline: Boolean = false,
    canShrink: Boolean = false,
) {
    ComposeNode<GtkComposeWidget<ButtonContent>, GtkApplier>({
        LeafComposeNode(ButtonContent.builder().build())
    }) {
        set(modifier) { applyModifier(it) }
        set(label) { this.widget.label = it }
        set(iconName) { this.widget.iconName = it?.iconName }
        set(useUnderline) { this.widget.useUnderline = it }
        set(canShrink) { this.widget.canShrink = it }
    }
}
