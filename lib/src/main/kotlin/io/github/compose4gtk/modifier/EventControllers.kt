package io.github.compose4gtk.modifier

import org.gnome.gtk.EventController
import org.gnome.gtk.Widget

fun Modifier.eventControllers(vararg eventControllers: EventController) = combine(
    apply = {
        for (eventController in eventControllers) {
            it.addController(eventController)
        }
    },
    undo = {
        val currentControllers = it.observeControllers()
        for (controller in currentControllers) {
            it.removeController(controller as EventController)
        }
    },
)

fun Modifier.eventControllers(eventControllers: List<EventController>) = combine(
    apply = {
        for (eventController in eventControllers) {
            it.addController(eventController)
        }
    },
    undo = {
        val currentControllers = it.observeControllers()
        for (controller in currentControllers) {
            it.removeController(controller as EventController)
        }
    },
)

fun Modifier.eventControllers(builder: (widget: Widget) -> List<EventController>) = combine(
    apply = { widget ->
        val eventControllers = builder(widget)
        for (eventController in eventControllers) {
            widget.addController(eventController)
        }
    },
    undo = { widget ->
        val currentControllers = widget.observeControllers()
        for (controller in currentControllers) {
            widget.removeController(controller as EventController)
        }
    },
)
