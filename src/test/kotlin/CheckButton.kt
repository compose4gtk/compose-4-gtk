import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.adw.application
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.StatusPage
import io.github.compose4gtk.gtk.components.*
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.alignment
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.sensitive
import org.gnome.gtk.Align

fun main(args: Array<String>) {
    application("my.example.hello-app", args) {
        ApplicationWindow(title = "Check Button", onClose = ::exitApplication) {
            VerticalBox {
                HeaderBar(modifier = Modifier.cssClasses("flat"))

                StatusPage(
                    title = "Check Button",
                    description = "Allow users to control binary options or properties",
                ) {
                    HorizontalBox {
                        VerticalBox(spacing = 8) {
                            var isChecked by remember { mutableStateOf(false) }

                            CheckButton(
                                onActiveRequest = { active -> isChecked = active },
                                modifier = Modifier.alignment(Align.START),
                                active = isChecked,
                                label = "Change me!",
                            )

                            CheckButton(
                                onActiveRequest = { println("Can't change me!") },
                                modifier = Modifier.alignment(Align.START),
                                active = isChecked,
                                label = "Mirror only",
                            )

                            CheckButton(
                                active = isChecked,
                                onActiveRequest = { println("Nothing happens") },
                                modifier = Modifier.alignment(Align.START),
                                inconsistent = true,
                            )

                            CheckButton(
                                onActiveRequest = { active ->
                                    isChecked = active
                                },
                                modifier = Modifier.alignment(Align.START),
                                active = isChecked,
                            ) {
                                HorizontalBox {
                                    Switch(active = isChecked, onToggle = {})
                                    Label("Custom child")
                                }
                            }

                            CheckButton(
                                active = isChecked,
                                label = "Disabled",
                                onActiveRequest = { println("Can't change me!") },
                                modifier = Modifier.alignment(Align.START).sensitive(false),
                            )
                        }

                        VerticalBox(spacing = 8) {
                            val checkedStates = remember { mutableStateListOf(false, false, false, false) }

                            fun allChecked() = checkedStates.all { it }
                            fun someChecked() = checkedStates.any { it }

                            CheckButton(
                                active = allChecked(),
                                label = "Select all",
                                onActiveRequest = { active ->
                                    val newState = !someChecked()
                                    for (i in checkedStates.indices) {
                                        checkedStates[i] = newState
                                    }
                                },
                                modifier = Modifier.alignment(Align.START),
                                inconsistent = someChecked() && !allChecked(),
                            )

                            checkedStates.forEachIndexed { index, isChecked ->
                                CheckButton(
                                    active = isChecked,
                                    label = "Option ${index + 1}",
                                    onActiveRequest = { active ->
                                        checkedStates[index] = active
                                    },
                                    modifier = Modifier.alignment(Align.START),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
