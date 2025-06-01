package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.SingleChildComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import org.gnome.gtk.CheckButton

private class GtkCheckButtonComposeNode(gObject: CheckButton) :
    SingleChildComposeNode<CheckButton>(gObject, { child = it }) {
    var toggled: SignalConnection<CheckButton.ToggledCallback>? = null
}

sealed interface RadioGroupState

private class RadioGroupStateImpl : RadioGroupState {
    private val checkButtons = mutableListOf<GtkCheckButtonComposeNode>()
    fun registerCheckButton(checkButton: GtkCheckButtonComposeNode) {
        checkButtons.add(checkButton)
        updateGroup()
    }

    fun unregisterCheckButton(checkButton: GtkCheckButtonComposeNode) {
        checkButtons.remove(checkButton)
        updateGroup()
    }

    private fun updateGroup() {
        if (checkButtons.isEmpty()) return

        var prev: GtkCheckButtonComposeNode? = null
        for (checkButton in checkButtons) {
            checkButton.widget.setGroup(prev?.widget)
            prev = checkButton
        }
    }

    fun blockToggledListeners() {
        for (node in checkButtons) {
            node.toggled?.block()
        }
    }

    fun unblockToggledListeners() {
        for (node in checkButtons) {
            node.toggled?.unblock()
        }
    }
}

@Composable
fun rememberRadioGroupState(): RadioGroupState {
    return remember { RadioGroupStateImpl() }
}

/**
 * Internal base function for creating a GTK [CheckButton]. Used by public overloads.
 *
 * This version accepts both a `label` and `child`, but in practice only one should be used.
 *
 * @param active Whether the check button is currently active.
 * @param onActiveRequest Callback invoked when the check button is toggled.
 * @param modifier The modifier to apply to the widget.
 * @param label Optional text label.
 * @param inconsistent Whether the button should display an inconsistent (partially active) state.
 * @param useUnderline Whether to use an underscore in the label for mnemonic activation.
 * @param child Optional custom composable content.
 */
@Composable
private fun BaseCheckButton(
    active: Boolean,
    onActiveRequest: (active: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: RadioGroupState? = null,
    label: String? = null,
    inconsistent: Boolean = false,
    useUnderline: Boolean = false,
    child: @Composable () -> Unit = {},
) {
    var pendingChange by remember { mutableIntStateOf(0) }
    val state: RadioGroupStateImpl? = when (state) {
        is RadioGroupStateImpl -> state
        null -> null
    }
    val checkButtonNode = remember { GtkCheckButtonComposeNode(CheckButton.builder().build()) }

    ComposeNode<GtkCheckButtonComposeNode, GtkApplier>(
        factory = { checkButtonNode },
        update = {
            set(modifier) { this.applyModifier(it) }
            set(active to pendingChange) { (active, _) ->
                this.toggled?.block()
                state?.blockToggledListeners()
                this.widget.active = active
                this.toggled?.unblock()
                state?.unblockToggledListeners()
            }
            set(inconsistent) { this.widget.inconsistent = it }
            set(label) { this.widget.label = it }
            set(useUnderline) { this.widget.useUnderline = it }
            set(onActiveRequest) {
                this.toggled?.disconnect()
                this.toggled = this.widget.onToggled {
                    pendingChange += 1
                    it(this.widget.active)
                    this.toggled?.block()
                    state?.blockToggledListeners()
                    this.widget.active = active
                    this.toggled?.unblock()
                    state?.unblockToggledListeners()
                }
            }
        },
        content = child,
    )

    DisposableEffect(Unit) {
        state?.registerCheckButton(checkButtonNode)
        onDispose {
            state?.unregisterCheckButton(checkButtonNode)
        }
    }
}

/**
 * Creates a [CheckButton] without a label or custom child content.
 *
 * @param active Whether the check button is currently active.
 * @param onActiveRequest Callback invoked when the check button is toggled.
 * @param modifier The modifier to apply to the widget.
 * @param inconsistent Whether the button should display an inconsistent (partially active) state.
 * @param useUnderline Whether to use an underscore in the label for mnemonic activation.
 */
@Composable
fun CheckButton(
    active: Boolean,
    onActiveRequest: (active: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    inconsistent: Boolean = false,
    useUnderline: Boolean = false,
) {
    BaseCheckButton(
        active = active,
        onActiveRequest = onActiveRequest,
        modifier = modifier,
        inconsistent = inconsistent,
        useUnderline = useUnderline,
    )
}

/**
 * Creates a [RadioButton] without a label or custom child content.
 *
 * @param state Shared radio group state for grouping buttons.
 * @param active Whether the check button is currently active.
 * @param onSelect Callback invoked when the radio button is selected.
 * @param modifier The modifier to apply to the widget.
 * @param inconsistent Whether the button should display an inconsistent (partially active) state.
 * @param useUnderline Whether to use an underscore in the label for mnemonic activation.
 */
@Composable
fun RadioButton(
    state: RadioGroupState,
    active: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    inconsistent: Boolean = false,
    useUnderline: Boolean = false,
) {
    BaseCheckButton(
        active = active,
        onActiveRequest = { active ->
            if (active) {
                onSelect()
            }
        },
        modifier = modifier,
        state = state,
        inconsistent = inconsistent,
        useUnderline = useUnderline,
    )
}

/**
 * Creates a [CheckButton] with a simple text [label].
 *
 * @param active Whether the check button is currently active.
 * @param label Text label.
 * @param onActiveRequest Callback invoked when the check button is toggled.
 * @param modifier The modifier to apply to the widget.
 * @param inconsistent Whether the button should display an inconsistent (partially active) state.
 * @param useUnderline Whether to use an underscore in the label for mnemonic activation.
 */
@Composable
fun CheckButton(
    active: Boolean,
    label: String,
    onActiveRequest: (active: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    inconsistent: Boolean = false,
    useUnderline: Boolean = false,
) {
    BaseCheckButton(
        active = active,
        onActiveRequest = onActiveRequest,
        modifier = modifier,
        label = label,
        inconsistent = inconsistent,
        useUnderline = useUnderline,
    )
}

/**
 * Creates a [RadioButton] with a simple text [label].
 *
 * @param state Shared radio group state for grouping buttons.
 * @param active Whether the check button is currently active.
 * @param label Text label.
 * @param onSelect Callback invoked when the radio button is selected.
 * @param modifier The modifier to apply to the widget.
 * @param inconsistent Whether the button should display an inconsistent (partially active) state.
 * @param useUnderline Whether to use an underscore in the label for mnemonic activation.
 */
@Composable
fun RadioButton(
    state: RadioGroupState,
    active: Boolean,
    label: String,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    inconsistent: Boolean = false,
    useUnderline: Boolean = false,
) {
    BaseCheckButton(
        active = active,
        onActiveRequest = { active ->
            if (active) {
                onSelect()
            }
        },
        modifier = modifier,
        state = state,
        label = label,
        inconsistent = inconsistent,
        useUnderline = useUnderline,
    )
}

/**
 * Creates a [CheckButton] with a custom child composable as its content.
 *
 * @param active Whether the check button is currently active.
 * @param onActiveRequest Callback invoked when the check button is toggled.
 * @param modifier The modifier to apply to the widget.
 * @param inconsistent Whether the button should display an inconsistent (partially active) state.
 * @param useUnderline Whether to use an underscore in the label for mnemonic activation.
 * @param child Custom composable content.
 */
@Composable
fun CheckButton(
    active: Boolean,
    onActiveRequest: (active: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    inconsistent: Boolean = false,
    useUnderline: Boolean = false,
    child: @Composable () -> Unit,
) {
    BaseCheckButton(
        active = active,
        onActiveRequest = onActiveRequest,
        modifier = modifier,
        inconsistent = inconsistent,
        useUnderline = useUnderline,
        child = child,
    )
}

/**
 * Creates a [RadioButton] with a custom child composable as its content.
 *
 * @param modifier The modifier to apply to the widget.
 * @param state Shared radio group state for grouping buttons.
 * @param active Whether the check button is currently active.
 * @param child Custom composable content.
 * @param inconsistent Whether the button should display an inconsistent (partially active) state.
 * @param useUnderline Whether to use an underscore in the label for mnemonic activation.
 * @param onSelect Callback invoked when the radio button is selected.
 */
@Composable
fun RadioButton(
    state: RadioGroupState,
    active: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    inconsistent: Boolean = false,
    useUnderline: Boolean = false,
    child: @Composable () -> Unit,
) {
    BaseCheckButton(
        active = active,
        onActiveRequest = { active ->
            if (active) {
                onSelect()
            }
        },
        modifier = modifier,
        state = state,
        inconsistent = inconsistent,
        useUnderline = useUnderline,
        child = child,
    )
}
