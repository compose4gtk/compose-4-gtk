import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.gtk.components.Entry
import io.github.compose4gtk.gtk.components.Frame
import io.github.compose4gtk.gtk.components.HorizontalBox
import io.github.compose4gtk.gtk.components.IconButton
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.expand
import io.github.compose4gtk.modifier.expandHorizontally
import io.github.compose4gtk.modifier.margin
import io.github.compose4gtk.modifier.sensitive
import io.github.compose4gtk.useGioResource
import io.github.compose4gtk.webkit.components.WebView
import io.github.compose4gtk.webkit.components.rememberWebViewState
import kotlin.math.abs
import kotlin.math.roundToInt

const val DEFAULT_URI = "https://compose4gtk.github.io/compose-4-gtk/"

fun main(args: Array<String>) {
    useGioResource("resources.gresource") {
        adwApplication("my.example.hello-app", args) {
            val webViewState = rememberWebViewState()

            ApplicationWindow(
                title = "Web View - ${webViewState.title}",
                onClose = ::exitApplication,
                defaultWidth = 1200,
                defaultHeight = 800,
            ) {
                VerticalBox {
                    HeaderBar()

                    var uri by remember { mutableStateOf(DEFAULT_URI) }
                    var zoomLevel by remember { mutableDoubleStateOf(1.0) }

                    LaunchedEffect(Unit) {
                        webViewState.loadUri(DEFAULT_URI)
                    }

                    LaunchedEffect(webViewState.uri) {
                        webViewState.uri?.let {
                            uri = it
                        }
                    }

                    HorizontalBox(modifier = Modifier.margin(16), spacing = 8) {
                        IconButton(
                            icon = ImageSource.Icon("left-small-symbolic"),
                            onClick = { webViewState.goBack() },
                            modifier = Modifier.sensitive(webViewState.canGoBack),
                        )
                        IconButton(
                            icon = ImageSource.Icon("right-small-symbolic"),
                            onClick = { webViewState.goForward() },
                            modifier = Modifier.sensitive(webViewState.canGoForward),
                        )
                        if (!webViewState.isLoading) {
                            IconButton(
                                icon = ImageSource.Icon("view-refresh-symbolic"),
                                onClick = { webViewState.reload() },
                                modifier = Modifier.sensitive(webViewState.uri != null),
                            )
                        } else {
                            IconButton(
                                icon = ImageSource.Icon("cross-small-symbolic"),
                                onClick = { webViewState.stopLoading() },
                            )
                        }
                        IconButton(
                            icon = ImageSource.Icon("go-home-symbolic"),
                            onClick = { webViewState.loadUri(DEFAULT_URI) },
                        )
                        Entry(
                            placeholderText = "Uri",
                            text = uri,
                            modifier = Modifier
                                .expandHorizontally()
                                .cssClasses("card")
                                .sensitive(!webViewState.isLoading),
                            onTextChange = { uri = it },
                            onActivate = { webViewState.loadUri(uri) },
                            progressFraction = if (webViewState.isLoading) webViewState.estimatedLoadProgress else 0.0,
                        )
                        IconButton(
                            icon = ImageSource.Icon("minus-symbolic"),
                            onClick = { zoomLevel = (zoomLevel - 0.1).coerceAtLeast(0.5) },
                            modifier = Modifier.sensitive(zoomLevel > 0.5),
                        )
                        Button(
                            label = "${(zoomLevel * 100).roundToInt()}%",
                            onClick = { zoomLevel = 1.0 },
                            modifier = Modifier.sensitive(abs(zoomLevel - 1.0) > 0.01),
                        )
                        IconButton(
                            icon = ImageSource.Icon("plus-symbolic"),
                            onClick = { zoomLevel = (zoomLevel + 0.1).coerceAtMost(2.0) },
                            modifier = Modifier.sensitive(zoomLevel < 2.0),
                        )
                    }

                    Frame(
                        modifier = Modifier.margin(16),
                    ) {
                        WebView(state = webViewState, modifier = Modifier.expand(), zoomLevel = zoomLevel)
                    }
                }
            }
        }
    }
}
