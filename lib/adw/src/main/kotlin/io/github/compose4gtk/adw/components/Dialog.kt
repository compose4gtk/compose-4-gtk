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
import io.github.compose4gtk.components.LocalApplicationWindow
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.adw.AboutDialog
import org.gnome.adw.AlertDialog
import org.gnome.adw.Dialog
import org.gnome.adw.DialogPresentationMode
import org.gnome.adw.ResponseAppearance
import org.gnome.gobject.GObjects
import org.gnome.gtk.License
import org.javagi.gobject.SignalConnection

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

/**
 * Creates a [org.gnome.adw.Dialog], an adaptive dialog container.
 *
 * @param title The title shown in the dialog header.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param contentHeight The height of the content.
 * @param contentWidth The width of the content.
 * @param followsContentSize Whether to size content automatically.
 * @param presentationMode Which mode used to display the dialog.
 * @param onClose Callback triggered when the dialog is closed.
 * @param content The composable content to display inside the dialog.
 */
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

/**
 * Creates a [org.gnome.adw.AboutDialog], a standardized dialog for displaying application metadata and credits.
 *
 * @param title The title shown in the dialog header.
 * @param applicationName The name of the application.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param applicationIcon The name of the application icon.
 * @param artists The list of artists of the application.
 * @param comments The comments about the application.
 * @param contentHeight The height of the content.
 * @param contentWidth The width of the content.
 * @param copyright The copyright information.
 * @param debugInfo The debug information.
 * @param debugInfoFilename The debug information filename.
 * @param designers The list of designers of the application.
 * @param developerName The developer name.
 * @param developers The list of developers of the application.
 * @param documenters The list of documenters of the application.
 * @param issueUrl The URL for the application’s issue tracker.
 * @param followsContentSize Whether to size content automatically.
 * @param license The license text.
 * @param licenseType The license type.
 * @param presentationMode Which mode used to display the dialog.
 * @param releaseNotes The release notes of the application.
 * @param releaseNotesVersion The version described by the application’s release notes.
 * @param supportUrl The URL of the application’s support page.
 * @param translatorCredits The translator credits string.
 * @param version The version of the application.
 * @param website The URL of the application's website.
 * @param onClose Callback triggered when the dialog is closed.
 * @param content The composable content to display inside the dialog.
 */
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

/**
 * Represents a response option (button) in an [AlertDialog].
 *
 * @property id Identifier for the response.
 * @property label The text shown in the button.
 * @property appearance Visual style.
 * @property isEnabled Whether the response is currently interactable.
 */
data class AlertDialogResponse(
    val id: String,
    val label: String,
    val appearance: ResponseAppearance = ResponseAppearance.DEFAULT,
    val isEnabled: Boolean = true,
)

/**
 * Creates a [org.gnome.adw.AlertDialog], a dialog presenting a message or a question.
 *
 * @param heading The heading of the dialog.
 * @param body The body text of the dialog.
 * @param responses List of possible responses shown as buttons.
 * @param onResponse Callback triggered when a response is selected.
 * @param onClose Callback triggered when the dialog is closed.
 * @param modifier Compose [Modifier] for layout and styling.
 * @param contentHeight The height of the content.
 * @param contentWidth The width of the content.
 * @param followsContentSize Whether to size content automatically.
 * @param presentationMode Which mode used to display the dialog.
 * @param defaultResponse The response selected by default.
 */
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
