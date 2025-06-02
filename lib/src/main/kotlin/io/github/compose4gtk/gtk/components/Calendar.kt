package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import io.github.jwharm.javagi.gobject.SignalConnection
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.gnome.glib.DateTime
import org.gnome.gtk.Calendar

private class GtkCalendarComposeNode(gObject: Calendar) : LeafComposeNode<Calendar>(gObject) {
    var onDaySelect: SignalConnection<Calendar.DaySelectedCallback>? = null
    var onNextMonth: SignalConnection<Calendar.NextMonthCallback>? = null
    var onNextYear: SignalConnection<Calendar.NextYearCallback>? = null
    var onPreviousMonth: SignalConnection<Calendar.PrevMonthCallback>? = null
    var onPreviousYear: SignalConnection<Calendar.PrevYearCallback>? = null
}

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    day: Int = 1,
    month: Int = 0,
    showDayNames: Boolean = true,
    showHeading: Boolean = true,
    showWeekNumber: Boolean = false,
    year: Int = DateTime.nowLocal().year,
    onDaySelect: ((day: Int, month: Int, year: Int) -> Unit)? = null,
    onNextMonth: (() -> Unit)? = null,
    onNextYear: (() -> Unit)? = null,
    onPreviousMonth: (() -> Unit)? = null,
    onPreviousYear: (() -> Unit)? = null,
) {
    ComposeNode<GtkCalendarComposeNode, GtkApplier>({
        GtkCalendarComposeNode(Calendar.builder().build())
    }) {
        set(modifier) { applyModifier(it) }
        set(day) { this.widget.day = it }
        set(month) { this.widget.month = it }
        set(showDayNames) { this.widget.showDayNames = it }
        set(showHeading) { this.widget.showHeading = it }
        set(showWeekNumber) { this.widget.showWeekNumbers = it }
        set(year) { this.widget.year = it }
        set(onDaySelect) {
            this.onDaySelect?.disconnect()
            onDaySelect?.let { this.widget.onDaySelected { onDaySelect(widget.day, widget.month, widget.year) } }
        }
        set(onNextMonth) {
            this.onNextMonth?.disconnect()
            onNextMonth?.let { this.widget.onNextMonth(onNextMonth) }
        }
        set(onNextYear) {
            this.onNextYear?.disconnect()
            onNextYear?.let { this.widget.onNextYear(onNextYear) }
        }
        set(onPreviousMonth) {
            this.onPreviousMonth?.disconnect()
            onPreviousMonth?.let { this.widget.onPrevMonth(onPreviousMonth) }
        }
        set(onPreviousYear) {
            this.onPreviousYear?.disconnect()
            onPreviousYear?.let { this.widget.onPrevYear(onPreviousYear) }
        }
    }
}

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    date: LocalDate = LocalDate(year = DateTime.nowLocal().year, month = Month.JANUARY, dayOfMonth = 1),
    showDayNames: Boolean = true,
    showHeading: Boolean = true,
    showWeekNumber: Boolean = false,
    onDaySelect: ((date: LocalDate) -> Unit)? = null,
    onNextMonth: (() -> Unit)? = null,
    onNextYear: (() -> Unit)? = null,
    onPreviousMonth: (() -> Unit)? = null,
    onPreviousYear: (() -> Unit)? = null,
) {
    Calendar(
        modifier = modifier,
        day = date.dayOfMonth,
        month = date.monthNumber - 1,
        showDayNames = showDayNames,
        showHeading = showHeading,
        showWeekNumber = showWeekNumber,
        year = date.year,
        onDaySelect = if (onDaySelect != null) {
            { day, month, year -> onDaySelect(LocalDate(year = year, monthNumber = month + 1, dayOfMonth = day)) }
        } else {
            null
        },
        onNextMonth = onNextMonth,
        onNextYear = onNextYear,
        onPreviousMonth = onPreviousMonth,
        onPreviousYear = onPreviousYear,
    )
}
