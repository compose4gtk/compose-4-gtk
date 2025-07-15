import androidx.compose.runtime.Composable
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.NavigationPage
import io.github.compose4gtk.adw.components.NavigationView
import io.github.compose4gtk.adw.components.NavigationViewState
import io.github.compose4gtk.adw.components.rememberNavigationViewState
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.margin

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Navigation View", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar(modifier = Modifier.cssClasses("flat"))

                val navigationViewState = rememberNavigationViewState()

                VerticalBox(
                    modifier = Modifier.cssClasses("card"),
                ) {
                    NavigationView(
                        state = navigationViewState,
                        verticallyHomogenous = true,
                        modifier = Modifier.margin(16),
                    ) {
                        NavigationPage(title = "Page 1", tag = "page1") {
                            VerticalBox {
                                PageOne(navigationViewState)
                            }
                        }
                        NavigationPage(title = "Page 2", tag = "page2") {
                            PageTwo(navigationViewState)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PageOne(navigationViewState: NavigationViewState) {
    VerticalBox(
        spacing = 8,
    ) {
        Label(text = "Welcome to page 1!")
        Button(label = "Go to page 2", onClick = {
            navigationViewState.pushByTag("page2")
        })
    }
}

@Composable
fun PageTwo(navigationViewState: NavigationViewState) {
    Button(label = "Go back", onClick = {
        navigationViewState.pop()
    })
}
