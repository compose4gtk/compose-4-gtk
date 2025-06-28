package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Updater
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.gtk.Button
import org.gnome.gtk.LinkButton
import org.gnome.gtk.ToggleButton

private class GtkButtonComposeNode(gObject: Button) : SingleChildComposeNode<Button>(gObject, { child = it }) {
    var onClick: SignalConnection<Button.ClickedCallback>? = null
}

private class GtkToggleButtonComposeNode(
    gObject: ToggleButton,
    var onToggle: SignalConnection<ToggleButton.ToggledCallback>,
) : SingleChildComposeNode<ToggleButton>(gObject, { child = it })

private class GtkLinkButtonComposeNode(
    gObject: LinkButton,
    var onActivateLink: SignalConnection<LinkButton.ActivateLinkCallback>?,
) : SingleChildComposeNode<LinkButton>(gObject, { child = it })

@Composable
private fun <B : GtkComposeWidget<Button>> BaseGenericButton(
    creator: () -> B,
    updater: Updater<B>.() -> Unit,
    label: String?,
    modifier: Modifier = Modifier,
    hasFrame: Boolean = true,
    child: @Composable () -> Unit,
) {
    ComposeNode<B, GtkApplier>(
        creator,
        update = {
            set(modifier) { applyModifier(it) }
            set(label) { this.widget.label = it }
            set(hasFrame) { this.widget.hasFrame = it }
            updater()
        },
        content = child,
    )
}

@Composable
private fun BaseButton(
    label: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasFrame: Boolean = true,
    child: @Composable () -> Unit,
) {
    BaseGenericButton(
        creator = { GtkButtonComposeNode(Button.builder().build()) },
        label = label,
        modifier = modifier,
        hasFrame = hasFrame,
        child = child,
        updater = {
            set(onClick) {
                this.onClick?.disconnect()
                this.onClick = this.widget.onClicked(it)
            }
        },
    )
}

/**
 * Creates a [org.gnome.gtk.Button] that triggers a callback when clicked.
 *
 * @param label The text displayed in the button.
 * @param onClick Callback triggered when the button is clicked.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param hasFrame Whether the button has a frame.
 */
@Composable
fun Button(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasFrame: Boolean = true,
) = BaseButton(label, onClick, modifier, hasFrame, child = {})

/**
 * Creates a [org.gnome.gtk.Button] that triggers a callback when clicked.
 *
 * @param onClick Callback triggered when the button is clicked.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param hasFrame Whether the button has a frame.
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasFrame: Boolean = true,
    child: @Composable () -> Unit = {},
) = BaseButton(label = null, onClick, modifier, hasFrame, child)

@Composable
private fun BaseToggleButton(
    label: String?,
    active: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    hasFrame: Boolean = true,
    child: @Composable () -> Unit = {},
) {
    BaseGenericButton(
        creator = {
            val tb = ToggleButton.builder().build()
            GtkToggleButtonComposeNode(tb, tb.onToggled { })
        },
        label = label,
        modifier = modifier,
        hasFrame = hasFrame,
        child = child,
        updater = {
            set(active) {
                this.onToggle.block()
                this.widget.active = it
                this.onToggle.unblock()
            }
            set(onToggle) {
                this.onToggle.disconnect()
                this.onToggle = this.widget.onToggled {
                    onToggle()
                    this.onToggle.block()
                    this.widget.active = active
                    this.onToggle.unblock()
                }
            }
        },
    )
}

/**
 * Creates a [org.gnome.gtk.ToggleButton] used for toggle states.
 *
 * @param label The text displayed in the button.
 * @param active Whether the button is toggled.
 * @param onToggle Callback triggered when the button is toggled.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param hasFrame Whether the button has a frame.
 */
@Composable
fun ToggleButton(
    label: String,
    active: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    hasFrame: Boolean = true,
) = BaseToggleButton(label, active, onToggle, modifier, hasFrame, child = {})

/**
 * Creates a [org.gnome.gtk.ToggleButton] used for toggle states.
 *
 * @param active Whether the button is toggled.
 * @param onToggle Callback triggered when the button is toggled.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param hasFrame Whether the button has a frame.
 */
@Composable
fun ToggleButton(
    active: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    hasFrame: Boolean = true,
    child: @Composable () -> Unit = {},
) = BaseToggleButton(label = null, active, onToggle, modifier, hasFrame, child)

/**
 * Creates a [org.gnome.gtk.Button] with a single icon.
 *
 * @param icon The icon displayed in the button.
 * @param onClick Callback triggered when the button is clicked.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param hasFrame Whether the button has a frame.
 */
@Composable
fun IconButton(
    icon: ImageSource.Icon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasFrame: Boolean = true,
) {
    BaseGenericButton(
        creator = { GtkButtonComposeNode(Button.builder().build()) },
        label = null,
        modifier = modifier,
        hasFrame = hasFrame,
        child = { },
        updater = {
            set(icon) { this.widget.iconName = it.iconName }
            set(onClick) {
                this.onClick?.disconnect()
                this.onClick = this.widget.onClicked(it)
            }
        },
    )
}

/**
 * Creates a [org.gnome.gtk.LinkButton] that opens a URI.
 *
 * @param label The text displayed in the button.
 * @param uri The URI that the button should open when activated.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param visited Indicates whether the URI has already been visited.
 * @param onActivateLink Callback triggered when the link is activated. Return `true` to handle the event manually.
 */
@Composable
fun LinkButton(
    label: String,
    uri: String,
    modifier: Modifier = Modifier,
    visited: Boolean = false,
    onActivateLink: () -> Boolean = { false },
) {
    BaseGenericButton(
        creator = {
            val lb = LinkButton.builder().build()
            GtkLinkButtonComposeNode(lb, lb.onActivateLink { false })
        },
        label = label,
        modifier = modifier,
        child = { },
        updater = {
            set(uri) { this.widget.uri = it }
            set(visited) { this.widget.visited = it }
            set(onActivateLink) {
                this.onActivateLink?.disconnect()
                this.onActivateLink = this.widget.onActivateLink {
                    onActivateLink()
                }
            }
        },
    )
}
