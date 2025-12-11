import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.ExpanderRow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.PreferencesGroup
import io.github.compose4gtk.adw.components.SwitchRow
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.margin

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Expander Row", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar()

                var expanded by remember { mutableStateOf(false) }
                var expansionEnabled by remember { mutableStateOf(true) }

                var useEncryptionSetting by remember { mutableStateOf(false) }

                PreferencesGroup(
                    title = "Settings",
                    modifier = Modifier.margin(8),
                    description = "A bunch of settings for your app",
                ) {
                    ExpanderRow(
                        expanded = expanded,
                        title = "Enable Backups",
                        enableExpansion = expansionEnabled,
                        showEnableSwitch = true,
                        onEnableExpansion = { expansionEnabled = !expansionEnabled },
                        onExpand = { expanded = !expanded },
                    ) {
                        SwitchRow(
                            active = useEncryptionSetting,
                            title = "Use Encryption",
                            onActivate = { useEncryptionSetting = !useEncryptionSetting },
                        )
                    }
                }
            }
        }
    }
}
