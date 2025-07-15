package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.NavigationPage as AdwNavigationPage

@Composable
fun NavigationPage(
    title: String,
    modifier: Modifier = Modifier,
    tag: String? = null,
    canPop: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    ComposeNode<GtkComposeWidget<AdwNavigationPage>, GtkApplier>(
        factory = {
            SingleChildComposeNode(
                widget = AdwNavigationPage(),
                set = { child = it },
            )
        },
        update = {
            set(title) { this.widget.title = it }
            set(modifier) { applyModifier(it) }
            set(tag) { this.widget.tag = it }
            set(canPop) { this.widget.canPop = it }
        },
        content = content,
    )
}
