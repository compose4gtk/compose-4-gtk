import io.github.compose4gtk.adw.application
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.ToolbarView
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.components.*
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.alignment
import io.github.compose4gtk.modifier.expandVertically
import org.gnome.gio.File
import org.gnome.gtk.Align
import org.gnome.gtk.ContentFit
import org.gnome.gtk.PackType

fun main(args: Array<String>) {
    application("my.example.hello-app", args) {
        ApplicationWindow("Test", onClose = ::exitApplication, defaultWidth = 800) {
            ToolbarView(
                bottomBar = {
                    HeaderBar(
                        startWidgets = { Button("Start", onClick = {}) },
                        endWidgets = { Button("End", onClick = {}) },
                        title = {
                            Label("Custom title")
                        },
                    )
                },
            ) {
                Overlay(
                    mainChild = {
                        Picture(
                            ImageSource.forFile(File.newForPath("src/test/gresources/images/lulu.jpg")),
                            contentFit = ContentFit.COVER,
                            modifier = Modifier.expandVertically(),
                        )
                    },
                    overlays = {
                        WindowControls(
                            side = PackType.START,
                            modifier = Modifier.alignment(Align.START, Align.START),
                        )
                        WindowControls(
                            side = PackType.END,
                            modifier = Modifier.alignment(Align.END, Align.START),
                        )
                    },
                )
            }
        }
    }
}
