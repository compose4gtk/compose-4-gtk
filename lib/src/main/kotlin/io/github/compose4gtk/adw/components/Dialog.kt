package io.github.compose4gtk.adw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.gtkSubComposition
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.shared.components.LocalApplicationWindow
import io.github.jwharm.javagi.gobject.SignalConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.adw.AboutDialog
import org.gnome.adw.AlertDialog
import org.gnome.adw.Dialog
import org.gnome.adw.DialogPresentationMode
import org.gnome.adw.ResponseAppearance
import org.gnome.gobject.GObjects
import org.gnome.gtk.License

private val logger = KotlinLogging.logger {}

private class GtkDialogComposeNode<D : Dialog>(
    gObject: D,
) : SingleChildComposeNode<D>(gObject, { this.child = it }) {
    var onCloseAttempt: SignalConnection<*>? = null
}

/**
 * TODO:
 *   default widget
 *   focus widget
 *   current breakpoint
 */
@Composable
private fun <D : Dialog> baseDialog(
    creator: () -> D,
    title: String?,
    modifier: Modifier = Modifier,
    contentHeight: Int = 0,
    contentWidth: Int = 0,
    followsContentSize: Boolean = false,
    presentationMode: DialogPresentationMode = DialogPresentationMode.AUTO,
    onClose: () -> Unit = {},
    content: @Composable () -> Unit = {},
): D {
    val applicationWindow = LocalApplicationWindow.current

    val composeNode = gtkSubComposition(
        createNode = {
            val dialog = creator()
            dialog.canClose = false
            GtkDialogComposeNode(dialog)
        },
        content = { content() },
    )
    val dialog = composeNode.widget

    DisposableEffect(Unit) {
        dialog.present(applicationWindow)
        onDispose {
            dialog.forceClose()
            dialog.emitDestroy()
        }
    }

    remember(title) { dialog.title = title }
    remember(modifier) { composeNode.applyModifier(modifier) }
    remember(contentHeight) { dialog.contentHeight = contentHeight }
    remember(contentWidth) { dialog.contentWidth = contentWidth }
    remember(followsContentSize) { dialog.followsContentSize = followsContentSize }
    remember(presentationMode) { dialog.presentationMode = presentationMode }
    remember(onClose) {
        composeNode.onCloseAttempt?.disconnect()
        composeNode.onCloseAttempt = dialog.onCloseAttempt {
            onClose()
            GObjects.signalStopEmissionByName(dialog, "close-attempt")
        }
    }
    return dialog
}

@Composable
fun Dialog(
    title: String?,
    modifier: Modifier = Modifier,
    contentHeight: Int = 0,
    contentWidth: Int = 0,
    followsContentSize: Boolean = false,
    presentationMode: DialogPresentationMode = DialogPresentationMode.AUTO,
    onClose: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    baseDialog(
        creator = {
            Dialog.builder().build()
        },
        title = title,
        modifier = modifier,
        contentHeight = contentHeight,
        contentWidth = contentWidth,
        followsContentSize = followsContentSize,
        presentationMode = presentationMode,
        onClose = onClose,
        content = content,
    )
}

@Composable
fun AboutDialog(
    title: String?,
    applicationName: String,
    modifier: Modifier = Modifier,
    applicationIcon: String = "",
    artists: List<String> = emptyList(),
    comments: String = "",
    contentHeight: Int = 0,
    contentWidth: Int = 0,
    copyright: String = "",
    debugInfo: String = "",
    debugInfoFilename: String = "",
    designers: List<String> = emptyList(),
    developerName: String = "",
    developers: List<String> = emptyList(),
    documenters: List<String> = emptyList(),
    issueUrl: String = "",
    followsContentSize: Boolean = false,
    license: String = "",
    licenseType: License = License.UNKNOWN,
    presentationMode: DialogPresentationMode = DialogPresentationMode.AUTO,
    releaseNotes: String = "",
    releaseNotesVersion: String = "",
    supportUrl: String = "",
    translatorCredits: String = "",
    version: String = "",
    website: String = "",
    onClose: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val dialog = baseDialog(
        creator = {
            AboutDialog.builder().build()
        },
        title = title,
        modifier = modifier,
        contentHeight = contentHeight,
        contentWidth = contentWidth,
        followsContentSize = followsContentSize,
        presentationMode = presentationMode,
        onClose = onClose,
        content = content,
    )
    remember(applicationName) { dialog.applicationName = applicationName }
    remember(applicationIcon) { dialog.applicationIcon = applicationIcon }
    remember(artists) { dialog.artists = artists.toTypedArray() }
    remember(comments) { dialog.comments = comments }
    remember(copyright) { dialog.copyright = copyright }
    remember(debugInfo) { dialog.debugInfo = debugInfo }
    remember(debugInfoFilename) { dialog.debugInfoFilename = debugInfoFilename }
    remember(designers) { dialog.designers = designers.toTypedArray() }
    remember(developerName) { dialog.developerName = developerName }
    remember(developers) { dialog.developers = developers.toTypedArray() }
    remember(documenters) { dialog.documenters = documenters.toTypedArray() }
    remember(issueUrl) { dialog.issueUrl = issueUrl }
    remember(license) { dialog.license = license }
    remember(licenseType) { dialog.licenseType = licenseType }
    remember(releaseNotes) { dialog.releaseNotes = releaseNotes }
    remember(releaseNotesVersion) { dialog.releaseNotesVersion = releaseNotesVersion }
    remember(supportUrl) { dialog.supportUrl = supportUrl }
    remember(translatorCredits) { dialog.translatorCredits = translatorCredits }
    remember(version) { dialog.version = version }
    remember(website) { dialog.website = website }
}

data class AlertDialogResponse(
    val id: String,
    val label: String,
    val appearance: ResponseAppearance = ResponseAppearance.DEFAULT,
    val isEnabled: Boolean = true,
)

@Composable
fun AlertDialog(
    heading: String,
    body: String,
    responses: List<AlertDialogResponse>,
    onResponse: (AlertDialogResponse) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    contentHeight: Int = -1,
    contentWidth: Int = -1,
    followsContentSize: Boolean = false,
    presentationMode: DialogPresentationMode = DialogPresentationMode.AUTO,
    defaultResponse: AlertDialogResponse? = null,
) {
    var connection: SignalConnection<*>? by remember { mutableStateOf(null) }
    var previousResponses: List<AlertDialogResponse>? by remember { mutableStateOf(null) }

    val dialog = baseDialog(
        creator = {
            AlertDialog.builder().build()
        },
        title = null,
        modifier = modifier,
        contentHeight = contentHeight,
        contentWidth = contentWidth,
        followsContentSize = followsContentSize,
        presentationMode = presentationMode,
        onClose = onClose,
    )
    remember(heading) { dialog.heading = heading }
    remember(body) { dialog.body = body }
    remember(responses) {
        // clear responses
        previousResponses
            ?.asSequence()
            ?.forEach { previousResponse ->
                dialog.removeResponse(previousResponse.id)
            }
        previousResponses = responses

        // add or update responses
        responses.forEach { response ->
            dialog.addResponse(response.id, response.label)
            dialog.setResponseEnabled(response.id, response.isEnabled)
            dialog.setResponseAppearance(response.id, response.appearance)
        }
    }
    remember(defaultResponse, responses) {
        if (defaultResponse != null) {
            require(
                value = responses.any { response -> response.id == defaultResponse.id },
                lazyMessage = { "\"Cannot find default response '${defaultResponse.id}' among responses\"" },
            )
        }
        dialog.defaultResponse = defaultResponse?.id
    }
    remember(onResponse, responses) {
        connection?.disconnect()
        connection = dialog.onResponse(null) { responseId ->
            if (responseId == "close") return@onResponse

            val response = responses.firstOrNull { it.id == responseId }
            if (response != null) {
                onResponse(response)
            } else {
                logger.warn { "Cannot find selected response '$responseId' among responses" }
            }
        }
    }
}
