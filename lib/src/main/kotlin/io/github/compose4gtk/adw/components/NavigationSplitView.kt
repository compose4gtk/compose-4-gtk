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
    var navigationSplitView: AdwNavigationSplitView?
    var collapsed: Boolean
    fun showContent()
    fun hideContent()
}

private class NavigationSplitViewStateImpl : NavigationSplitViewState {
    override var navigationSplitView: AdwNavigationSplitView? = null
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

@Composable
fun rememberNavigationSplitViewState(): NavigationSplitViewState {
    return remember { NavigationSplitViewStateImpl() }
}

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
    ComposeNode<GtkComposeWidget<AdwNavigationSplitView>, GtkApplier>(
        factory = {
            val gObject = AdwNavigationSplitView()
            state.navigationSplitView = gObject
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
                    set = {
                        it?.let {
                            if (it is AdwNavigationPage) {
                                navigationSplitView.sidebar = it
                            } else {
                                val navigationPage = AdwNavigationPage()
                                navigationPage.child = it
                                navigationSplitView.sidebar = navigationPage
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
                    set = {
                        it?.let {
                            if (it is AdwNavigationPage) {
                                navigationSplitView.content = it
                            } else {
                                val navigationPage = AdwNavigationPage()
                                navigationPage.child = it
                                navigationSplitView.content = navigationPage
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
