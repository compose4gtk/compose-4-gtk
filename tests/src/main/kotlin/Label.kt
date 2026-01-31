import androidx.compose.runtime.Composable
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.Box
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.alignment
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.margin
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.gtk.Align
import org.gnome.gtk.Justification
import org.gnome.gtk.Orientation
import org.gnome.pango.EllipsizeMode

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("Test", onClose = ::exitApplication, defaultWidth = 100, defaultHeight = 100) {
            Box(orientation = Orientation.VERTICAL) {
                HeaderBar()

                Card("Markup") {
                    Label(
                        "Some text with <b>basic</b> <i>format</i>",
                        useMarkup = true,
                        modifier = Modifier
                            .alignment(Align.START),
                    )
                    Label(
                        "Internally handled <a href=\"https://example.org\">link</a>.",
                        onActivateLink = { uri ->
                            logger.info { "link clicked: $uri" }
                            true
                        },
                        useMarkup = true,
                        modifier = Modifier
                            .alignment(Align.START),
                    )
                    Label(
                        "Externally handled <a href=\"https://example.org\">link</a>.",
                        onActivateLink = { uri ->
                            logger.info { "link clicked: $uri" }
                            false
                        },
                        useMarkup = true,
                        modifier = Modifier
                            .alignment(Align.START),
                    )
                }

                Card("Selectable text") {
                    Label(
                        "Some text that can be selected and copied",
                        selectable = true,
                        onCopyClipboard = { println("copied") },
                    )
                }

                Card("Ellipsize") {
                    Label(
                        "Some long text we want to ellipsize",
                        modifier = Modifier
                            .alignment(Align.START),
                    )
                    Label(
                        "Some long text we want to ellipsize",
                        maxWidthChars = 20,
                        ellipsize = EllipsizeMode.START,
                        modifier = Modifier
                            .alignment(Align.START),
                    )
                    Label(
                        "Some long text we want to ellipsize",
                        maxWidthChars = 20,
                        ellipsize = EllipsizeMode.MIDDLE,
                        modifier = Modifier
                            .alignment(Align.START),
                    )
                    Label(
                        "Some long text we want to ellipsize",
                        maxWidthChars = 20,
                        ellipsize = EllipsizeMode.END,
                        modifier = Modifier
                            .alignment(Align.START),
                    )
                }

                Card("Justification") {
                    Label(
                        "Left justification, sample text: lorem ipsum dolor sit amet.",
                        wrap = true,
                        justify = Justification.LEFT,
                    )
                    Label(
                        "Center justification, sample text: lorem ipsum dolor sit amet.",
                        wrap = true,
                        justify = Justification.CENTER,
                    )
                    Label(
                        "Right justification, sample text: lorem ipsum dolor sit amet.",
                        wrap = true,
                        justify = Justification.RIGHT,
                    )
                    Label(
                        "Fill justification, sample text: lorem ipsum dolor sit amet.",
                        wrap = true,
                        justify = Justification.FILL,
                    )
                }
            }
        }
    }
}

@Composable
private fun Card(
    title: String,
    content: @Composable () -> Unit,
) {
    VerticalBox(
        modifier = Modifier
            .margin(16),
        spacing = 16,
    ) {
        Label(title)

        Box(
            modifier = Modifier
                .cssClasses(listOf("card")),
        ) {
            VerticalBox(
                spacing = 16,
                modifier = Modifier
                    .margin(16),
            ) {
                content()
            }
        }
    }
}
