import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ActionRow
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.ButtonRow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.PreferencesGroup
import io.github.compose4gtk.adw.components.EntryRow
import io.github.compose4gtk.adw.components.SpinRow
import io.github.compose4gtk.adw.components.SwitchRow
import io.github.compose4gtk.adw.components.ComboRow

import io.github.compose4gtk.adw.components.ToastOverlay
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.components.IconButton
import io.github.compose4gtk.gtk.components.ListBox
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.alignment
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.margin
import io.github.compose4gtk.useGioResource
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gnome.adw.ColorScheme
import org.gnome.adw.StyleManager
import org.gnome.adw.Toast
import org.gnome.gtk.Align
import org.gnome.gtk.SelectionMode
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.rememberSelectionModel
import io.github.compose4gtk.gtk.components.SelectionMode as ListSelectionMode

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    useGioResource("resources.gresource") {
        adwApplication("my.example.hello-app", args) {
            ApplicationWindow("Preferences", onClose = ::exitApplication) {
                ToastOverlay {
                    VerticalBox {
                        HeaderBar(modifier = Modifier.cssClasses("flat"))

                        ListBox(
                            selectionMode = SelectionMode.NONE,
                            modifier = Modifier.margin(16).cssClasses("boxed-list"),
                        ) {
                            ButtonRows()
                        }

                        val styleManager = remember { StyleManager.getDefault() }
                        var isSwitchActive by remember { mutableStateOf(styleManager.dark) }

                        PreferencesGroup(
                            title = "Preferences Group",
                            description = "Useful for grouping settings",
                            modifier = Modifier.margin(16),
                        ) {
                            SwitchRow(
                                active = isSwitchActive,
                                title = "Dark Mode",
                                subtitle = "Whether the app uses dark mode",
                                onActivate = {
                                    logger.info { "Switch toggled" }
                                    isSwitchActive = !isSwitchActive

                                    val colorScheme =
                                        if (isSwitchActive) ColorScheme.FORCE_DARK else ColorScheme.FORCE_LIGHT
                                    styleManager.colorScheme = colorScheme
                                    logger.info { styleManager.colorScheme }
                                },
                            )

                            var username by remember { mutableStateOf("guest") }
                            EntryRow(
                                text = username,
                                title = "Username",
                                showApplyButton = false,
                                // "check" button pressed
                                onApply = {
                                    logger.info { "Apply btn: Username entered: $username" }
                                },
                                // won't work while showApplyButton = true
                                onEntryActivate = {
                                    logger.info { "Enter key: Username entered: $username" }
                                },
                                onTextChange = {
                                    logger.info { "changed signal: EntryRow text changed: $it" }
                                    username = it
                                },

                            )
                            ComboRows()
                            ActionRow(
                                title = "Toast",
                                subtitle = "Launches a toast",
                                prefix = {
                                    IconButton(
                                        icon = ImageSource.Icon("system-search-symbolic"),
                                        onClick = {
                                            logger.info { "Searching" }
                                        },
                                        modifier = Modifier.alignment(Align.CENTER),
                                    )
                                },
                                suffix = {
                                    IconButton(
                                        ImageSource.Icon("bread-symbolic"),
                                        modifier = Modifier.alignment(Align.CENTER).activateWithActionRow(),
                                        onClick = {
                                            logger.info { "Launch a toast" }

                                            val toast = Toast().apply {
                                                title = "A toast"
                                            }

                                            dismissAllToasts()
                                            addToast(toast)
                                        },
                                    )
                                },
                            )
                            ActionRow(
                                title = "Additional Preferences",
                                subtitle = "Navigates to another page",
                                onActivate = {
                                    logger.info { "Will navigate to another page in the future." }
                                },
                            )

                            var spinRowValue by remember { mutableDoubleStateOf(35.0) }
                            SpinRow(
                                value = spinRowValue,
                                title = "A Spin Row",
                                subtitle = "Helps select numerical values",
                                onActivate = { logger.info { "Spin row activated" } },
                                onValueChange = {
                                    spinRowValue = it
                                },
                                upper = 100.0,
                                stepIncrement = 1.0,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonRows() {
    ButtonRow(
        title = "Search",
        startIcon = ImageSource.Icon("system-search-symbolic"),
        onActivate = {
            logger.info { "I added stuff" }
        },
    )
    ButtonRow(
        title = "Delete",
        modifier = Modifier.cssClasses("destructive-action"),
        endIcon = ImageSource.Icon("edit-delete-symbolic"),
        onActivate = {
            logger.info { "I deleted stuff" }
        },
    )
}

@Composable
fun ComboRows() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val items = remember { listOf("One", "Two", "Three") }
    ComboRow(
        items = items,
        selectedIndex = selectedIndex,
        title = "Favorite number",
        subtitle = "Pick one",
        onSelectedChange = { idx ->
            selectedIndex = idx
            logger.info { "ComboRow selected index: $idx, value: ${items[idx]}" }
        },
    )

    val comboRowSelectModel = rememberSelectionModel(
        itemsCount = items.size,
        selectionMode = ListSelectionMode.Single,
    )
    ComboRow(
        model = comboRowSelectModel,
        onSelectedChange = { selected ->
            val idx = selected.index
            logger.info { "Model ComboRow selected index: $idx, value: ${items[idx]}" }
        },
        item = { idxObj ->
            Label(text = items[idxObj.index])
        },
        selectedItem = { idxObj ->
            Label(text = "Selected: ${items[idxObj.index]}")
        },
        title = "Favorite number (model)",
        subtitle = "Pick one (custom render)",
    )
}
