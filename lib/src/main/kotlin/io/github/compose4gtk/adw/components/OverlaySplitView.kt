package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeNode
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.VirtualComposeNode
import io.github.compose4gtk.VirtualComposeNodeContainer
import io.github.compose4gtk.modifier.Modifier
import org.gnome.adw.OverlaySplitView
import org.gnome.gtk.PackType

interface OverlaySplitViewScope {
    val showSidebar: Boolean
    fun showSidebar()
    fun hideSidebar()
}

private class OverlaySplitViewImpl : OverlaySplitViewScope {
    override var showSidebar by mutableStateOf(true)
    var overlaySplitView: OverlaySplitView? = null
        set(widget) {
            require(field == null)
            if (widget != null) {
                widget.showSidebar = showSidebar
                widget.onNotify("show-sidebar") {
                    showSidebar = widget.showSidebar
                }
                field = widget
            }
        }

    override fun showSidebar() {
        showSidebar(true)
    }

    override fun hideSidebar() {
        showSidebar(false)
    }

    private fun showSidebar(show: Boolean) {
        when (val w = overlaySplitView) {
            null -> showSidebar = show
            else -> w.showSidebar = show
        }
    }
}

/**
 * TODO:
 *  - min/max sidebar width
 */
@Composable
fun OverlaySplitView(
    sidebar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    collapsed: Boolean = false,
    pinSidebar: Boolean = false,
    sidebarPosition: PackType = PackType.START,
    sidebarWidthFraction: Double = 0.25,
    enableHideGesture: Boolean = true,
    enableShowGesture: Boolean = true,
    content: @Composable OverlaySplitViewScope.() -> Unit,
) {
    val scope = remember { OverlaySplitViewImpl() }
    ComposeNode<GtkComposeWidget<OverlaySplitView>, GtkApplier>(
        factory = {
            val splitView = OverlaySplitView
                .builder()
                .build()
            VirtualComposeNodeContainer(splitView)
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(collapsed) { this.widget.collapsed = it }
            set(pinSidebar) { this.widget.pinSidebar = it }
            set(sidebarPosition) { this.widget.sidebarPosition = it }
            set(sidebarWidthFraction) { this.widget.sidebarWidthFraction = it }
            set(enableHideGesture) { this.widget.enableHideGesture = it }
            set(enableShowGesture) { this.widget.enableShowGesture = it }
            set(scope) { scope.overlaySplitView = this.widget }
        },
        content = {
            Sidebar {
                sidebar()
            }
            Content {
                scope.apply {
                    content()
                }
            }
        },
    )
}

@Composable
private fun Content(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<OverlaySplitView> { overlay ->
                SingleChildComposeNode(
                    overlay,
                    set = { setContent(it) },
                )
            }
        },
        update = { },
        content = content,
    )
}

@Composable
private fun Sidebar(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<OverlaySplitView> { overlay ->
                SingleChildComposeNode(
                    overlay,
                    set = { sidebar = it },
                )
            }
        },
        update = { },
        content = content,
    )
}
