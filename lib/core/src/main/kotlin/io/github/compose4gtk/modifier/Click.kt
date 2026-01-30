package io.github.compose4gtk.modifier

import org.gnome.gtk.GestureClick
import org.gnome.gtk.PropagationPhase

fun Modifier.click(
    passThrough: Boolean = false,
    button: Int = 0,
    action: (nPress: Int, x: Double, y: Double) -> Unit,
): Modifier {
    val gesture = GestureClick.builder()
        .apply {
            if (!passThrough) {
                setPropagationPhase(PropagationPhase.TARGET)
            }
        }
        .setButton(button)
        .build()
    gesture.onPressed(action)
    return combine(
        apply = {
            it.addController(gesture)
        },
        undo = {
            it.removeController(gesture)
        },
    )
}

fun Modifier.click(
    passThrough: Boolean = false,
    button: Int = 0,
    action: () -> Unit,
) = click(passThrough, button) { _, _, _ -> action() }
