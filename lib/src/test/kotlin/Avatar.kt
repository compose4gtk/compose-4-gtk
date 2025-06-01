import io.github.compose4gtk.adw.application
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.Avatar
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.useGioResource

fun main(args: Array<String>) {
    useGioResource("resources.gresource") {
        application("my.example.hello-app", args) {
            ApplicationWindow(title = "Avatar", onClose = ::exitApplication, defaultWidth = 600, defaultHeight = 540) {
                VerticalBox(
                    spacing = 16,
                ) {
                    HeaderBar()
                    Avatar(
                        text = "John Smith",
                        image = ImageSource.forResource("/my/example/hello-app/images/lulu.jpg"),
                        size = 100,
                    )
                    Avatar(
                        text = "John Doe",
                        image = null,
                        showInitials = true,
                        size = 100,
                    )
                    Avatar(
                        text = "",
                        image = null,
                        size = 100,
                    )
                    Avatar(
                        text = "",
                        image = ImageSource.Icon("folder-symbolic"),
                        size = 100,
                    )
                }
            }
        }
    }
}
