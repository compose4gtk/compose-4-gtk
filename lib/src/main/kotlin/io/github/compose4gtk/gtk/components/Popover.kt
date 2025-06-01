package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.modifier.Modifier
import org.gnome.gtk.Popover

interface PopoverScope {
    var gObject: Popover?
}

@Composable
fun Popover(
    trigger: @Composable PopoverScope.() -> Unit,
    modifier: Modifier = Modifier,
    arrow: Boolean = true,
    content: @Composable () -> Unit,
) {
    val scope = object : PopoverScope {
        override var gObject: Popover? = null
    }
    ComposeNode<GtkComposeWidget<Popover>, GtkApplier>(
        factory = {
            val popover = Popover.builder()
                .setHasArrow(arrow)
                .build()
            scope.gObject = popover
            SingleChildComposeNode<Popover>(popover) {
                child = it
            }
        },
        update = {
            set(modifier) { applyModifier(it) }
        },
        content = content,
    )
    scope.trigger()
}
