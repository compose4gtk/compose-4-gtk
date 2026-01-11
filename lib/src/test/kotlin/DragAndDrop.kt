import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ActionRow
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.adw.components.ToolbarView
import io.github.compose4gtk.gtk.ImageSource
import io.github.compose4gtk.gtk.components.Frame
import io.github.compose4gtk.gtk.components.HorizontalBox
import io.github.compose4gtk.gtk.components.Image
import io.github.compose4gtk.gtk.components.Label
import io.github.compose4gtk.gtk.components.ListBox
import io.github.compose4gtk.gtk.components.VerticalBox
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.cssClasses
import io.github.compose4gtk.modifier.eventControllers
import io.github.compose4gtk.modifier.expand
import io.github.compose4gtk.modifier.margin
import io.github.jwharm.javagi.gobject.types.Types
import org.gnome.adw.ActionRow as AdwActionRow
import org.gnome.gdk.ContentFormats
import org.gnome.gdk.ContentProvider
import org.gnome.gdk.DragAction
import org.gnome.gobject.GObject
import org.gnome.gobject.Value
import org.gnome.gtk.DragIcon
import org.gnome.gtk.DragSource
import org.gnome.gtk.DropTarget
import org.gnome.gtk.Frame as GtkFrame
import org.gnome.gtk.Widget
import org.gnome.gtk.Image as GtkImage
import org.gnome.gtk.ListBox as GtkListBox

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow(title = "Drag and Drop", onClose = ::exitApplication) {
            VerticalBox {
                ToolbarView(
                    topBar = { HeaderBar() }
                ) {
                    VerticalBox {
                        val leftLabels = remember { mutableStateListOf("Apple", "Banana", "Orange", "Pear", "Melon") }
                        val rightLabels = remember { mutableStateListOf<String>() }

                        HorizontalBox(
                            modifier = Modifier.margin(8),
                            spacing = 8,
                            homogeneous = true,
                        ) {
                            Frame(
                                modifier = Modifier
                                    .expand()
                                    .eventControllers(
                                        generateFruitDropEvent { value ->
                                            if (!leftLabels.contains(value.string)) {
                                                rightLabels.remove(value.string)
                                                leftLabels.add(value.string)
                                            }
                                            true
                                        }
                                    )
                            ) {
                                VerticalBox(
                                    modifier = Modifier.margin(8),
                                    spacing = 8,
                                ) {
                                    for (label in leftLabels) {
                                        Frame(modifier = Modifier.expand().eventControllers { gObject ->
                                            listOf(generateFruitDragEvent(label, gObject))
                                        }) {
                                            Label(text = label)
                                        }
                                    }
                                }
                            }
                            Frame(
                                modifier = Modifier
                                    .expand()
                                    .eventControllers(
                                        generateFruitDropEvent { value ->
                                            if (!rightLabels.contains(value.string)) {
                                                leftLabels.remove(value.string)
                                                rightLabels.add(value.string)
                                            }
                                            true
                                        }
                                    )
                            ) {
                                VerticalBox(
                                    modifier = Modifier.margin(8),
                                    spacing = 8,
                                ) {
                                    for (label in rightLabels) {
                                        Frame(modifier = Modifier.expand().eventControllers { gObject ->
                                            listOf(generateFruitDragEvent(label, gObject))
                                        }) {
                                            Label(text = label)
                                        }
                                    }
                                }
                            }
                        }

                        val orderedList = remember {
                            mutableStateListOf(
                                OrderRow("Row 1"),
                                OrderRow("Row 2"),
                                OrderRow("Row 3"),
                                OrderRow("Row 4"),
                                OrderRow("Row 5"),
                            )
                        }

                        fun reorderList(draggedRow: OrderRow, dropTargetRow: OrderRow) {
                            val oldIndex = orderedList.indexOf(draggedRow)
                            val newIndex = orderedList.indexOf(dropTargetRow)

                            orderedList.removeAt(oldIndex)

                            orderedList.add(newIndex, draggedRow)
                        }

                        VerticalBox(
                            modifier = Modifier.margin(8),
                        ) {
                            ListBox(
                                modifier = Modifier.cssClasses("boxed-list"),
                            ) {
                                var index = 0
                                for (row in orderedList) {
                                    ActionRow(
                                        title = row.name,
                                        subtitle = "Index: ${index++}",
                                        prefix = {
                                            Image(
                                                ImageSource.Icon("list-drag-handle-symbolic"),
                                                modifier = Modifier.cssClasses("dimmed"),
                                            )
                                        },
                                        modifier = Modifier.eventControllers { gObject ->
                                            listOf(
                                                generateOrderDragEvent(row, gObject),
                                                generateOrderDropEvent { value ->
                                                    val dragged = value.`object` as OrderRow
                                                    reorderList(dragged, row)
                                                    true
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun generateFruitDragEvent(label: String, gObject: Widget): DragSource {
    var dragX = 0.0
    var dragY = 0.0

    return DragSource.builder()
        .setActions(DragAction.MOVE)
        .onPrepare { x, y ->
            dragX = x
            dragY = y

            val value = Value()
            value.init(Types.STRING)
            value.string = label

            ContentProvider.forValue(value)
        }
        .onDragBegin { drag ->
            // Sets where the component is grabbed
            drag.setHotspot(dragX.toInt(), dragY.toInt())

            // Create a custom icon with gObjects
            // that matches the original component
            val icon = DragIcon.getForDrag(drag)
            icon.child = GtkFrame.builder()
                .setWidthRequest(gObject.width)
                .setHeightRequest(gObject.height)
                .setChild(
                    org.gnome.gtk.Label(label)
                )
                .build()
        }
        .build()
}

private fun generateFruitDropEvent(onDrop: (Value) -> Boolean): DropTarget {
    return DropTarget.builder()
        .setActions(DragAction.MOVE)
        .setFormats(ContentFormats.forGtype(Types.STRING))
        .onMotion { _, _ ->
            setOf(DragAction.MOVE)
        }
        .onDrop { value, _, _ ->
            onDrop(value)
        }
        .build()
}

private fun generateOrderDragEvent(orderRow: OrderRow, gObject: Widget): DragSource {
    var dragX = 0.0
    var dragY = 0.0

    return DragSource.builder()
        .setActions(DragAction.MOVE)
        .onPrepare { x, y ->
            dragX = x
            dragY = y

            val value = Value()
            value.init(Types.OBJECT)
            value.`object` = orderRow

            ContentProvider.forValue(value)
        }
        .onDragBegin { drag ->
            drag.setHotspot(dragX.toInt(), dragY.toInt())

            val icon = DragIcon.getForDrag(drag)
            gObject as AdwActionRow
            val listBox = GtkListBox.builder()
                .setWidthRequest(gObject.width)
                .setHeightRequest(gObject.height)
                .setCssClasses(arrayOf("boxed-list"))
                .build()
            val actionRow = AdwActionRow.builder()
                .setTitle(gObject.title)
                .setSubtitle(gObject.subtitle)
                .build()
            actionRow.addPrefix(
                GtkImage.builder()
                    .setIconName("list-drag-handle-symbolic")
                    .setCssClasses(arrayOf("dimmed"))
                    .build()
            )

            listBox.append(actionRow)
            listBox.dragHighlightRow(actionRow)
            icon.child = listBox
        }
        .build()
}

private fun generateOrderDropEvent(onDrop: (Value) -> Boolean): DropTarget {
    return DropTarget.builder()
        .setActions(DragAction.MOVE)
        .setFormats(ContentFormats.forGtype(Types.OBJECT))
        .onMotion { _, _ ->
            setOf(DragAction.MOVE)
        }
        .onDrop { value, _, _ ->
            onDrop(value)
        }
        .build()
}

private data class OrderRow(val name: String) : GObject()
