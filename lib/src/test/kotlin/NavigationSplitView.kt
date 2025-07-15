import androidx.compose.runtime.getValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.NavigationPage
import io.github.compose4gtk.adw.components.NavigationSplitView
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.adw.components.ToolbarView
import io.github.compose4gtk.adw.components.rememberNavigationSplitViewState
import io.github.compose4gtk.gtk.components.Button
import org.gnome.adw.BreakpointCondition

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(
            title = "Navigation Split View",
            onClose = ::exitApplication,
            defaultHeight = 300,
            defaultWidth = 500,
        ) {
            val navigationSplitViewState = rememberNavigationSplitViewState()
            val collapsedBreakpoint by rememberBreakpoint(
                condition = BreakpointCondition.parse("max-width: 400sp"),
            )
            navigationSplitViewState.collapsed = collapsedBreakpoint

            NavigationSplitView(state = navigationSplitViewState, sidebar = {
                NavigationPage(title = "Sidebar") {
                    ToolbarView(
                        topBar = {
                            HeaderBar(showTitle = false)
                        },
                    ) {
                        StatusPage(
                            title = "Sidebar",
                        ) {
                            if (navigationSplitViewState.collapsed) {
                                Button(
                                    label = "Show Content",
                                    onClick = { navigationSplitViewState.showContent() },
                                )
                            }
                        }
                    }
                }
            }) {
                NavigationPage(title = "Content") {
                    ToolbarView(
                        topBar = {
                            HeaderBar(showTitle = false)
                        },
                    ) {
                        StatusPage(
                            title = "Content",
                        ) {
                            if (navigationSplitViewState.collapsed) {
                                Button(
                                    label = "Hide Content",
                                    onClick = { navigationSplitViewState.hideContent() },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
