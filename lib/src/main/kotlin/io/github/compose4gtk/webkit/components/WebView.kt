package io.github.compose4gtk.webkit.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.GtkComposeWidget
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.webkit.LoadEvent
import org.gnome.webkit.WebView as WebKitWebView

private val logger = KotlinLogging.logger {}

sealed interface WebViewState {
    val webView: WebKitWebView?
    val title: String?
    val uri: String?
    val isLoading: Boolean
    val estimatedLoadProgress: Double
    val canGoBack: Boolean
    val canGoForward: Boolean
    fun loadPlainText(plainText: String)
    fun loadUri(uri: String)
    fun loadHtml(html: String, baseUri: String? = null)
    fun goBack()
    fun goForward()
    fun reload()
    fun stopLoading()
}

private class WebViewStateImpl : WebViewState {
    override var webView: WebKitWebView? = null
        set(value) {
            check(field == null) { "WebViewState can be associated to a single WebView" }
            requireNotNull(value)
            field = value

            value.onNotify("title") {
                _title = value.title
            }
            value.onNotify("uri") {
                _uri = value.uri
            }
            value.onNotify("is-loading") {
                _isLoading = value.isLoading
            }
            value.onNotify("estimated-load-progress") {
                _estimatedLoadProgress = value.estimatedLoadProgress
            }
            value.onLoadChanged { loadEvent ->
                when (loadEvent) {
                    LoadEvent.STARTED -> {}
                    LoadEvent.COMMITTED -> {}
                    LoadEvent.REDIRECTED -> {}
                    LoadEvent.FINISHED -> {
                        _canGoBack = value.canGoBack()
                        _canGoForward = value.canGoForward()
                    }
                }
            }
        }

    private var _title by mutableStateOf<String?>(null)
    override val title: String? by derivedStateOf { _title }

    private var _uri by mutableStateOf<String?>(null)
    override val uri: String? by derivedStateOf { _uri }

    private var _isLoading by mutableStateOf(true)
    override val isLoading: Boolean by derivedStateOf { _isLoading }

    private var _estimatedLoadProgress by mutableDoubleStateOf(0.0)
    override val estimatedLoadProgress: Double by derivedStateOf { _estimatedLoadProgress }

    private var _canGoBack by mutableStateOf(false)
    override val canGoBack: Boolean by derivedStateOf { _canGoBack }

    private var _canGoForward by mutableStateOf(false)
    override val canGoForward: Boolean by derivedStateOf { _canGoForward }

    override fun loadPlainText(plainText: String) {
        val w = webView
        if (w == null) {
            logger.warn { "Cannot load plain text: WebView not initialized yet" }
            return
        }
        w.loadPlainText(plainText)
    }

    override fun loadUri(uri: String) {
        val w = webView
        if (w == null) {
            logger.warn { "Cannot load uri: WebView not initialized yet" }
            return
        }
        w.loadUri(uri)
    }

    override fun loadHtml(html: String, baseUri: String?) {
        val w = webView
        if (w == null) {
            logger.warn { "Cannot load HTML: WebView not initialized yet" }
            return
        }
        w.loadHtml(html, baseUri)
    }

    override fun goBack() {
        val w = webView
        if (w == null) {
            logger.warn { "Cannot go back: WebView not initialized yet" }
            return
        }
        w.goBack()
    }

    override fun goForward() {
        val w = webView
        if (w == null) {
            logger.warn { "Cannot go forward: WebView not initialized yet" }
            return
        }
        w.goForward()
    }

    override fun reload() {
        val w = webView
        if (w == null) {
            logger.warn { "Cannot reload: WebView not initialized yet" }
            return
        }
        w.reload()
    }

    override fun stopLoading() {
        val w = webView
        if (w == null) {
            logger.warn { "Cannot stop loading: WebView not initialized yet" }
            return
        }
        w.stopLoading()
    }
}

@Composable
fun rememberWebViewState(): WebViewState {
    val state = remember { WebViewStateImpl() }
    return state
}

/**
 * Creates a [org.gnome.webkit.WebView] that can load and display HTML pages.
 *
 * @param state [io.github.compose4gtk.adw.components.CarouselState] that manages the web view.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param zoomLevel Zoom level of the view.
 */
@Composable
fun WebView(
    state: WebViewState,
    modifier: Modifier = Modifier,
    zoomLevel: Double = 1.0,
) {
    val stateImpl: WebViewStateImpl = when (state) {
        is WebViewStateImpl -> state
    }
    ComposeNode<GtkComposeWidget<WebKitWebView>, GtkApplier>(
        factory = {
            val gObject = WebKitWebView()
            stateImpl.webView = gObject
            LeafComposeNode(gObject)
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(zoomLevel) { this.widget.zoomLevel = it }
        },
    )
}
