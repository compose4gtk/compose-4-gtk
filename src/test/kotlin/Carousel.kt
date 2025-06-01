import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.*
import io.github.compose4gtk.gtk.components.*
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.expand
import io.github.compose4gtk.modifier.margin
import org.gnome.gtk.Orientation

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("Carousel", onClose = ::exitApplication) {
            var pageCount by remember { mutableIntStateOf(8) }
            val orientation = remember { mutableStateOf(Orientation.HORIZONTAL) }
            val carouselState = rememberCarouselState(pageCount, orientation.value)
            val allowLongSwipes = remember { mutableStateOf(false) }
            val allowMouseDrag = remember { mutableStateOf(true) }
            val allowScrollWheel = remember { mutableStateOf(true) }

            VerticalBox {
                HeaderBar(title = { Label("Current page: ${carouselState.currentPage}/${carouselState.pageCount}") })

                Box(
                    orientation = if (orientation.value == Orientation.HORIZONTAL) {
                        Orientation.VERTICAL
                    } else {
                        Orientation.HORIZONTAL
                    },
                    modifier = Modifier.margin(8, 0),
                ) {
                    Carousel(
                        state = carouselState,
                        modifier = Modifier.expand(true),
                        allowLongSwipes = allowLongSwipes.value,
                        allowMouseDrag = allowMouseDrag.value,
                        allowScrollWheel = allowScrollWheel.value,
                        spacing = 80,
                        onPageChange = { index ->
                            println("Page changed to $index")
                            println("State current page changed to ${carouselState.currentPage}")
                        },
                    ) { page ->
                        when (page) {
                            0 -> Presentation()
                            1 -> Settings(allowLongSwipes, allowMouseDrag, allowScrollWheel)
                            2 -> MoreSettings(orientation)
                            else -> {
                                StatusPage(title = "Page $page", description = "Status page $page") {
                                    VerticalBox {
                                        if (page == pageCount - 1) {
                                            Label("You reached the end!")
                                            Button("Add page", onClick = {
                                                pageCount += 1
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }

                    CarouselIndicatorDots(carouselState)
                    CarouselIndicatorLines(carouselState)
                }

                Box(orientation = Orientation.VERTICAL, modifier = Modifier.margin(16), spacing = 16) {
                    Button("Snap scroll to page 4", onClick = {
                        carouselState.scrollTo(4, animate = false)
                    })
                    Button("Scroll to last", onClick = {
                        carouselState.scrollTo(pageCount - 1)
                    })
                    Button("Scroll to first", onClick = {
                        carouselState.scrollTo(0)
                    })
                }
            }
        }
    }
}

@Composable
private fun Presentation() {
    StatusPage(title = "Carousel") {
        Label("This is a carousel component")
    }
}

@Composable
private fun Settings(
    allowLongSwipes: MutableState<Boolean>,
    allowMouseDrag: MutableState<Boolean>,
    allowScrollWheel: MutableState<Boolean>,
) {
    StatusPage(title = "Settings") {
        Box(orientation = Orientation.VERTICAL, modifier = Modifier.margin(16), spacing = 16) {
            Label("It can be modified")
            ToggleButton(
                label = "Allow long swipes",
                active = allowLongSwipes.value,
                onToggle = {
                    allowLongSwipes.value = !allowLongSwipes.value
                },
            )
            ToggleButton(
                label = "Allow mouse drag",
                active = allowMouseDrag.value,
                onToggle = {
                    allowMouseDrag.value = !allowMouseDrag.value
                },
            )
            ToggleButton(
                label = "Allow scroll wheel",
                active = allowScrollWheel.value,
                onToggle = {
                    allowScrollWheel.value = !allowScrollWheel.value
                },
            )
        }
    }
}

@Composable
private fun MoreSettings(orientation: MutableState<Orientation>) {
    StatusPage(title = "More Settings!") {
        Box(orientation = Orientation.VERTICAL, modifier = Modifier.margin(16), spacing = 16) {
            Label("I am ${if (orientation.value == Orientation.HORIZONTAL) "Horizontal" else "Vertical"}")
            Button(label = "Change orientation", onClick = {
                if (orientation.value == Orientation.HORIZONTAL) {
                    orientation.value = Orientation.VERTICAL
                } else {
                    orientation.value = Orientation.HORIZONTAL
                }
            })
        }
    }
}
