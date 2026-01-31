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
import org.gnome.adw.LengthUnit
import org.gnome.adw.NavigationPage as AdwNavigationPage
import org.gnome.adw.NavigationSplitView as AdwNavigationSplitView

sealed interface NavigationSplitViewState {
    var collapsed: Boolean
    fun showContent()
    fun hideContent()
}

private class NavigationSplitViewStateImpl : NavigationSplitViewState {
    var navigationSplitView: AdwNavigationSplitView? = null
        set(value) {
            check(field == null) { "NavigationSplitViewState can be associated to a single NavigationSplitView" }
            requireNotNull(value)
            field = value
        }

    private var _collapsed by mutableStateOf(false)
    override var collapsed: Boolean
        get() = _collapsed
        set(value) {
            _collapsed = value
        }

    override fun showContent() {
        navigationSplitView?.let {
            it.showContent = true
        }
    }

    override fun hideContent() {
        navigationSplitView?.let {
            it.showContent = false
        }
    }
}

/**
 * Creates and remembers a [NavigationSplitViewState] for controlling a navigation split view.
 */
@Composable
fun rememberNavigationSplitViewState(): NavigationSplitViewState {
    return remember { NavigationSplitViewStateImpl() }
}

/**
 * Creates a [org.gnome.adw.NavigationSplitView] that displays a sidebar and content side by side.
 *
 * @param state A [NavigationSplitViewState] used to control the split view.
 * @param sidebar A composable widget used as the sidebar. Needs to be a [NavigationPage].
 * @param modifier Compose [Modifier] for layout and styling.
 * @param maxSidebarWidth The maximum width of the sidebar.
 * @param minSidebarWidth The minimum width of the sidebar.
 * @param sidebarWidthFraction The preferred sidebar width as a fraction of the total width.
 * @param sidebarWidthUnit The length unit for minimum and maximum sidebar widths.
 * @param content A composable widget used as the content. Needs to be a [NavigationPage].
 */
@Composable
fun NavigationSplitView(
    state: NavigationSplitViewState,
    sidebar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    maxSidebarWidth: Double = 280.0,
    minSidebarWidth: Double = 180.0,
    sidebarWidthFraction: Double = 0.25,
    sidebarWidthUnit: LengthUnit = LengthUnit.SP,
    content: @Composable () -> Unit,
) {
    val stateImpl: NavigationSplitViewStateImpl = when (state) {
        is NavigationSplitViewStateImpl -> state
    }
    ComposeNode<GtkComposeWidget<AdwNavigationSplitView>, GtkApplier>(
        factory = {
            val gObject = AdwNavigationSplitView()
            stateImpl.navigationSplitView = gObject
            VirtualComposeNodeContainer(gObject)
        },
        update = {
            set(state.collapsed) { this.widget.collapsed = it }
            set(modifier) { applyModifier(it) }
            set(maxSidebarWidth) { this.widget.maxSidebarWidth = it }
            set(minSidebarWidth) { this.widget.minSidebarWidth = it }
            set(sidebarWidthFraction) { this.widget.sidebarWidthFraction = it }
            set(sidebarWidthUnit) { this.widget.sidebarWidthUnit = it }
        },
        content = {
            Sidebar {
                sidebar()
            }
            Content {
                content()
            }
        },
    )
}

@Composable
private fun Sidebar(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<AdwNavigationSplitView> { navigationSplitView ->
                SingleChildComposeNode(
                    navigationSplitView,
                    set = { widget ->
                        widget?.let {
                            if (it is AdwNavigationPage) {
                                navigationSplitView.sidebar = it
                            } else {
                                error("Sidebar must be inside a NavigationPage")
                            }
                        }
                    },
                )
            }
        },
        update = {},
        content = content,
    )
}

@Composable
private fun Content(
    content: @Composable () -> Unit,
) {
    ComposeNode<GtkComposeNode, GtkApplier>(
        factory = {
            VirtualComposeNode<AdwNavigationSplitView> { navigationSplitView ->
                SingleChildComposeNode(
                    navigationSplitView,
                    set = { widget ->
                        widget?.let {
                            if (it is AdwNavigationPage) {
                                navigationSplitView.content = it
                            } else {
                                error("Content must be inside a NavigationPage")
                            }
                        }
                    },
                )
            }
        },
        update = {},
        content = content,
    )
}
