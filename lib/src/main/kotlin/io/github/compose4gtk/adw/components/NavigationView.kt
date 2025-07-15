package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.remember
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.GtkContainerComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.gtk.Widget
import org.gnome.adw.NavigationPage as AdwNavigationPage
import org.gnome.adw.NavigationView as AdwNavigationView

private val logger = KotlinLogging.logger {}

private class AdwNavigationViewComposeNode(gObject: AdwNavigationView) :
    GtkContainerComposeNode<AdwNavigationView>(gObject) {
    override fun addNode(index: Int, child: GtkComposeWidget<Widget>) {
        if (child.widget is AdwNavigationPage) {
            when (index) {
                children.size -> widget.add(child.widget)
                0 -> widget.insertAfter(child.widget, null)
                else -> widget.insertBefore(child.widget, children[index - 1])
            }
            super.addNode(index, child)
        } else {
            logger.warn { "Only navigation pages can be added to a navigation view." }
        }
    }

    override fun removeNode(index: Int) {
        val child = children[index]
        if (child is AdwNavigationPage) {
            widget.remove(child)
            super.removeNode(index)
        }
    }

    override fun clearNodes() {
        children.forEach {
            if (it is AdwNavigationPage) {
                widget.remove(it)
            }
        }
        super.clearNodes()
    }
}

sealed interface NavigationViewState {
    var navigationView: AdwNavigationView?
    var visiblePage: AdwNavigationPage?
    var visiblePageTag: String?
    fun findPage(tag: String): AdwNavigationPage?
    fun pop()
    fun popToPage(page: AdwNavigationPage)
    fun popToTag(tag: String)
    fun push(page: AdwNavigationPage)
    fun pushByTag(tag: String)
}

private class NavigationViewStateImpl : NavigationViewState {
    override var navigationView: AdwNavigationView? = null
        set(value) {
            check(field == null) { "NavigationViewState can be associated to a single NavigationView" }
            requireNotNull(value)
            field = value
        }
    override var visiblePage: AdwNavigationPage? = navigationView?.visiblePage
    override var visiblePageTag: String? = navigationView?.visiblePageTag
    override fun findPage(tag: String): AdwNavigationPage? {
        return navigationView?.findPage(tag)
    }

    override fun pop() {
        navigationView?.pop()
    }

    override fun popToPage(page: AdwNavigationPage) {
        navigationView?.popToPage(page)
    }

    override fun popToTag(tag: String) {
        navigationView?.popToTag(tag)
    }

    override fun push(page: AdwNavigationPage) {
        navigationView?.push(page)
    }

    override fun pushByTag(tag: String) {
        navigationView?.pushByTag(tag)
    }
}

/**
 * Creates and remembers a [NavigationViewState] for controlling a navigation stack.
 */
@Composable
fun rememberNavigationViewState(): NavigationViewState {
    return remember { NavigationViewStateImpl() }
}

/**
 * Creates a [org.gnome.adw.NavigationView] used to display navigation pages.
 *
 * @param state A [NavigationViewState] used to control navigation pages.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param animateTransitions Whether page transitions are animated.
 * @param horizontallyHomogenous Whether the view is horizontally homogeneous.
 * @param popOnEscape Whether pressing `Escape` pops the current page.
 * @param verticallyHomogenous Whether the view is vertically homogeneous.
 * @param content Composable [NavigationPage] widgets.
 */
@Composable
fun NavigationView(
    state: NavigationViewState,
    modifier: Modifier = Modifier,
    animateTransitions: Boolean = true,
    horizontallyHomogenous: Boolean = false,
    popOnEscape: Boolean = true,
    verticallyHomogenous: Boolean = false,
    content: @Composable () -> Unit = {},
) {
    ComposeNode<GtkComposeWidget<AdwNavigationView>, GtkApplier>(
        factory = {
            val gObject = AdwNavigationView()
            state.navigationView = gObject
            AdwNavigationViewComposeNode(gObject)
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(animateTransitions) { this.widget.animateTransitions = it }
            set(horizontallyHomogenous) { this.widget.hhomogeneous = it }
            set(popOnEscape) { this.widget.popOnEscape = it }
            set(verticallyHomogenous) { this.widget.vhomogeneous = it }
        },
        content = content,
    )
}
