package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.ShortcutLabel as AdwShortcutLabel

/**
 * Creates a [org.gnome.adw.ShortcutLabel] to display keyboard shortcuts.
 *
 * @param accelerator The keys that activates the shortcut.
 * @param disabledText Text displayed when the shortcut is disabled.
 * @param modifier Compose [Modifier] for layout and styling.
 */
@Composable
fun ShortcutLabel(
    accelerator: String,
    disabledText: String,
    modifier: Modifier = Modifier,
) {
    ComposeNode<GtkComposeWidget<AdwShortcutLabel>, GtkApplier>(
        factory = { LeafComposeNode(AdwShortcutLabel()) },
        update = {
            set(accelerator) { this.widget.accelerator = it }
            set(disabledText) { this.widget.disabledText = it }
            set(modifier) { applyModifier(it) }
        },
    )
}
