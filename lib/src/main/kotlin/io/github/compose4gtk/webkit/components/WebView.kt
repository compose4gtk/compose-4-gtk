package io.github.compose4gtk.webkit.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.webkit.WebView as WebKitWebView

@Composable
fun WebView(
    modifier: Modifier = Modifier,
    plainText: String? = null,
    uri: String? = null,
) {
    ComposeNode<GtkComposeWidget<WebKitWebView>, GtkApplier>(
        factory = {
            LeafComposeNode(WebKitWebView())
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(plainText) {
                plainText?.let { this.widget.loadPlainText(it) }
            }
            set(uri) {
                uri?.let { this.widget.loadUri(it) }
            }
        },
    )
}
