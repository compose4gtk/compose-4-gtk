package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.GtkContainerComposeNode
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.adw.Carousel
import org.gnome.adw.CarouselIndicatorDots
import org.gnome.adw.CarouselIndicatorLines
import org.gnome.adw.SpringParams
import org.gnome.gtk.Orientation
import org.gnome.gtk.Widget

@Suppress("MagicNumber")
private val DEFAULT_SPRING_PARAMS = SpringParams(1.0, 0.5, 500.0)
private val logger = KotlinLogging.logger {}

private class AdwCarouselComposeNode(gObject: Carousel) : GtkContainerComposeNode<Carousel>(gObject) {
    var onPageChanged: SignalConnection<Carousel.PageChangedCallback>? = null

    override fun addNode(index: Int, child: GtkComposeWidget<Widget>) {
        when (index) {
            children.size -> widget.append(child.widget)
            0 -> widget.insertAfter(child.widget, null)
            else -> widget.insertAfter(child.widget, children[index - 1])
        }
        super.addNode(index, child)
    }

    override fun removeNode(index: Int) {
        val child = children[index]
        widget.remove(child)
        super.removeNode(index)
    }

    override fun clearNodes() {
        children.forEach { widget.remove(it) }
        super.clearNodes()
    }
}

sealed interface CarouselState {
    val carousel: Carousel?
    val currentPage: Int
    val pageCount: Int
    val orientation: Orientation
    fun scrollTo(pageNumber: Int, animate: Boolean = true)
}

private class CarouselStateImpl : CarouselState {
    override var carousel: Carousel? = null
        set(value) {
            check(field == null) { "CarouselState can be associated to a single Carousel" }
            requireNotNull(value)
            field = value
        }
    override var pageCount by mutableIntStateOf(0)
    override var currentPage by mutableIntStateOf(0)
    override var orientation by mutableStateOf(Orientation.HORIZONTAL)

    override fun scrollTo(pageNumber: Int, animate: Boolean) {
        val c = carousel
        if (c == null) {
            logger.warn { "Cannot scroll to $pageNumber: Carousel not initialized yet" }
            return
        }
        val widget = c.getNthPage(pageNumber)
        if (widget == null) {
            logger.warn { "Cannot scroll to $pageNumber: page not initialized yet" }
            return
        }
        c.scrollTo(widget, animate)
    }
}

/**
 * Creates and remembers a [CarouselState] for controlling a [Carousel] composable.
 *
 * @param pageCount Total number of pages in the carousel.
 * @param orientation The scrolling direction.
 */
@Composable
fun rememberCarouselState(pageCount: Int, orientation: Orientation = Orientation.HORIZONTAL): CarouselState {
    val state = remember { CarouselStateImpl() }
    state.pageCount = pageCount
    state.orientation = orientation
    return state
}

/**
 * Creates a [org.gnome.adw.Carousel], a paginated scrolling widget.
 *
 * @param state The shared [CarouselState] instance.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param allowLongSwipes Whether swipes can navigate across multiple pages at once.
 * @param allowMouseDrag Whether the carousel can be dragged with the mouse pointer.
 * @param allowScrollWheel Enables scrolling between pages with the mouse wheel.
 * @param interactive Whether the carousel can be navigated.
 * @param revealDuration Duration in milliseconds for page transition animations.
 * @param scrollParams The parameters for animating the carousel's scroll.
 * @param spacing The space between each page.
 * @param onPageChange Callback triggered when the current page changes.
 * @param content Composable widget that represents the pages.
 */
@Composable
fun Carousel(
    state: CarouselState,
    modifier: Modifier = Modifier,
    allowLongSwipes: Boolean = false,
    allowMouseDrag: Boolean = true,
    allowScrollWheel: Boolean = true,
    interactive: Boolean = true,
    revealDuration: Int = 0,
    scrollParams: SpringParams = DEFAULT_SPRING_PARAMS,
    spacing: Int = 0,
    onPageChange: ((Int) -> Unit)? = null,
    content: @Composable (page: Int) -> Unit,
) {
    val stateImpl: CarouselStateImpl = when (state) {
        is CarouselStateImpl -> state
    }
    ComposeNode<AdwCarouselComposeNode, GtkApplier>(
        factory = {
            val gObject = Carousel()
            stateImpl.carousel = gObject
            AdwCarouselComposeNode(gObject)
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(state.orientation) { this.widget.orientation = it }
            set(allowLongSwipes) { this.widget.allowLongSwipes = it }
            set(allowMouseDrag) { this.widget.allowMouseDrag = it }
            set(allowScrollWheel) { this.widget.allowScrollWheel = it }
            set(interactive) { this.widget.interactive = it }
            set(revealDuration) { this.widget.revealDuration = it }
            set(scrollParams) { this.widget.scrollParams = it }
            set(spacing) { this.widget.spacing = it }
            set(onPageChange) {
                this.onPageChanged?.disconnect()
                this.onPageChanged = this.widget.onPageChanged { index ->
                    stateImpl.currentPage = index
                    if (it != null) {
                        it(index)
                    }
                }
            }
        },
        content = {
            repeat(state.pageCount) { index ->
                // This is necessary to have a predictable number of pages, regardless to how many composable
                // per page the caller adds.
                // Moreover, it prevents undesired scrolling when swapping a composable in a page.
                VerticalBox(homogeneous = true) {
                    content(index)
                }
            }
        },
    )
}

/**
 * Creates a [org.gnome.adw.CarouselIndicatorDots], a dots indicator for [Carousel].
 *
 * @param carouselState The [CarouselState] used by the carousel.
 * @param modifier Compose [Modifier] for layout and styling.
 */
@Composable
fun CarouselIndicatorDots(
    carouselState: CarouselState,
    modifier: Modifier = Modifier,
) {
    ComposeNode<GtkComposeWidget<CarouselIndicatorDots>, GtkApplier>({
        LeafComposeNode(CarouselIndicatorDots.builder().build())
    }) {
        set(carouselState.carousel) { this.widget.carousel = it }
        set(modifier) { applyModifier(it) }
        set(carouselState.orientation) { this.widget.orientation = it }
    }
}

/**
 * Creates a [org.gnome.adw.CarouselIndicatorLines], a lines indicator for [Carousel].
 *
 * @param carouselState The [CarouselState] used by the carousel.
 * @param modifier Compose [Modifier] for layout and styling.
 */
@Composable
fun CarouselIndicatorLines(
    carouselState: CarouselState,
    modifier: Modifier = Modifier,
) {
    ComposeNode<GtkComposeWidget<CarouselIndicatorLines>, GtkApplier>({
        LeafComposeNode(CarouselIndicatorLines.builder().build())
    }) {
        set(carouselState.carousel) { this.widget.carousel = it }
        set(modifier) { applyModifier(it) }
        set(carouselState.orientation) { this.widget.orientation = it }
    }
}
