import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.expandVertically

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(
            "StatusPage",
            onClose = ::exitApplication,
            defaultWidth = 800,
            defaultHeight = 900,
        ) {
            VerticalBox {
                HeaderBar()
                StatusPage(
                    title = "Loading",
                    description = "Please wait...",
                    icon = ImageSource.spinner,
                    modifier = Modifier.expandVertically(),
                )
                StatusPage(
                    title = "No Results Found",
                    description = "Try a different search",
                    icon = ImageSource.Icon("system-search-symbolic"),
                    modifier = Modifier
                        .expandVertically()
                        .cssClasses("compact"),
                )
                StatusPage(
                    title = "No Results Found",
                    description = "Try a different search",
                    icon = ImageSource.Icon("system-search-symbolic"),
                    modifier = Modifier
                        .expandVertically(),
                )
            }
        }
    }
}
