import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.AboutDialog
import io.github.compose4gtk.adw.components.AlertDialog
import io.github.compose4gtk.adw.components.AlertDialogResponse
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.Dialog
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.Button
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.ToggleButton
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.horizontalAlignment
import io.github.compose4gtk.modifier.margin
import org.gnome.adw.DialogPresentationMode
import org.gnome.adw.ResponseAppearance
import org.gnome.gtk.Align
import org.gnome.gtk.License

private val cancelResponse = AlertDialogResponse(
    id = "cancel",
    label = "_Cancel",
)
private val replaceResponse = AlertDialogResponse(
    id = "replace",
    label = "_Replace",
    appearance = ResponseAppearance.DESTRUCTIVE,
)

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(
            "Dialogs",
            onClose = ::exitApplication,
            defaultWidth = 960,
            defaultHeight = 800,
        ) {
            var floatingDialog by remember { mutableStateOf(false) }
            if (floatingDialog) {
                ExampleFloatingDialog { floatingDialog = false }
            }

            var bottomSheetDialog by remember { mutableStateOf(false) }
            if (bottomSheetDialog) {
                ExampleBottomSheetDialog { bottomSheetDialog = false }
            }

            var aboutDialog by remember { mutableStateOf(false) }
            if (aboutDialog) {
                ExampleAboutDialog { aboutDialog = false }
            }

            var alertDialog by remember { mutableStateOf(false) }
            if (alertDialog) {
                ExampleAlertDialog { alertDialog = false }
            }

            VerticalBox {
                HeaderBar()

                VerticalBox(
                    modifier = Modifier.margin(16),
                    spacing = 16,
                ) {
                    ToggleButton(
                        label = "Toggle dialog",
                        active = floatingDialog,
                        onToggle = { floatingDialog = !floatingDialog },
                    )
                    Button(
                        modifier = Modifier.horizontalAlignment(Align.CENTER),
                        label = "Floating dialog",
                        onClick = { floatingDialog = true },
                    )
                    Button(
                        modifier = Modifier.horizontalAlignment(Align.CENTER),
                        label = "Bottom sheet dialog",
                        onClick = { bottomSheetDialog = true },
                    )
                    Button(
                        modifier = Modifier.horizontalAlignment(Align.CENTER),
                        label = "About dialog",
                        onClick = { aboutDialog = true },
                    )
                    Button(
                        modifier = Modifier.horizontalAlignment(Align.CENTER),
                        label = "Alert dialog",
                        onClick = { alertDialog = true },
                    )
                }
            }
        }
    }
}

@Composable
private fun ExampleFloatingDialog(onClose: () -> Unit) {
    Dialog(
        presentationMode = DialogPresentationMode.FLOATING,
        title = "Floating dialog",
        onClose = {
            // Doing nothing, let's keep it open
        },
    ) {
        VerticalBox {
            HeaderBar()
            Label("Say something", modifier = Modifier.margin(64))
            Button("close", onClick = onClose)
        }
    }
}

@Composable
private fun ExampleBottomSheetDialog(onClose: () -> Unit) {
    Dialog(
        presentationMode = DialogPresentationMode.BOTTOM_SHEET,
        title = "Bottom sheet dialog",
        onClose = onClose,
    ) {
        Label("Say something", modifier = Modifier.margin(64))
    }
}

@Composable
private fun ExampleAboutDialog(onClose: () -> Unit) {
    AboutDialog(
        applicationName = "Application",
        applicationIcon = "help-about",
        developerName = "Developer Name",
        artists = listOf("Joe Dalton", "Jack Dalton", "William Dalton"),
        designers = listOf("Jane Doe", "John Doe"),
        licenseType = License.GPL_3_0,
        issueUrl = "https://www.example.org",
        releaseNotes = "<ul><li>first change</li><li>second change</li></ul>",
        version = "1.2.3",
        website = "https://www.example.org",
        title = "About",
        contentWidth = 400,
        contentHeight = 600,
        onClose = onClose,
    )
}

@Composable
private fun ExampleAlertDialog(onClose: () -> Unit) {
    AlertDialog(
        heading = "Replace file?",
        body = "A file named \"example.png\" already exists. Do you want to replace it?",
        followsContentSize = true,
        responses = listOf(
            cancelResponse,
            replaceResponse,
        ),
        defaultResponse = cancelResponse,
        onResponse = { response ->
            if (response == replaceResponse) {
                println("Replace the file")
            }
        },
        onClose = onClose,
    )
}
