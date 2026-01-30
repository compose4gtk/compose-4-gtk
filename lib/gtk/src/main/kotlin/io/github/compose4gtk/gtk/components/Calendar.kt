package io.github.compose4gtk.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import io.github.compose4gtk.GtkApplier
import io.github.compose4gtk.LeafComposeNode
import io.github.compose4gtk.modifier.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number
import org.gnome.glib.DateTime
import org.gnome.gtk.Calendar
import org.javagi.gobject.SignalConnection

private class GtkCalendarComposeNode(gObject: Calendar) : LeafComposeNode<Calendar>(gObject) {
    var onDaySelect: SignalConnection<Calendar.DaySelectedCallback>? = null
    var onNextMonth: SignalConnection<Calendar.NextMonthCallback>? = null
    var onNextYear: SignalConnection<Calendar.NextYearCallback>? = null
    var onPreviousMonth: SignalConnection<Calendar.PrevMonthCallback>? = null
    var onPreviousYear: SignalConnection<Calendar.PrevYearCallback>? = null
}

/**
 * Creates a [org.gnome.gtk.Calendar] to display a Gregorian calendar, one month at a time.
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param day The selected day.
 * @param month The selected month.
 * @param year The selected year.
 * @param showDayNames Whether the calendar shows day names.
 * @param showHeading Whether the calendar should show a heading.
 * @param showWeekNumber Whether week numbers are shown in the calendar.
 * @param onDaySelect Callback triggered when a day is selected.
 * @param onNextMonth Callback triggered when the calendar moves to the next month.
 * @param onNextYear Callback triggered when the calendar moves to the next year.
 * @param onPreviousMonth Callback triggered when the calendar moves to the previous month.
 * @param onPreviousYear Callback triggered when the calendar moves to the previous year.
 */
@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    day: Int = 1,
    month: Int = 0,
    year: Int = DateTime.nowLocal().year,
    showDayNames: Boolean = true,
    showHeading: Boolean = true,
    showWeekNumber: Boolean = false,
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
        set(year) { this.widget.year = it }
        set(showDayNames) { this.widget.showDayNames = it }
        set(showHeading) { this.widget.showHeading = it }
        set(showWeekNumber) { this.widget.showWeekNumbers = it }
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

/**
 * Creates a [org.gnome.gtk.Calendar] to display a Gregorian calendar, one month at a time.
 *
 * @param modifier Compose [Modifier] for layout and styling.
 * @param date The selected date.
 * @param showDayNames Whether the calendar shows day names.
 * @param showHeading Whether the calendar should show a heading.
 * @param showWeekNumber Whether week numbers are shown in the calendar.
 * @param onDaySelect Callback triggered when a day is selected.
 * @param onNextMonth Callback triggered when the calendar moves to the next month.
 * @param onNextYear Callback triggered when the calendar moves to the next year.
 * @param onPreviousMonth Callback triggered when the calendar moves to the previous month.
 * @param onPreviousYear Callback triggered when the calendar moves to the previous year.
 */
@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    date: LocalDate = LocalDate(year = DateTime.nowLocal().year, month = Month.JANUARY, day = 1),
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
        day = date.day,
        month = date.month.number - 1,
        showDayNames = showDayNames,
        showHeading = showHeading,
        showWeekNumber = showWeekNumber,
        year = date.year,
        onDaySelect = if (onDaySelect != null) {
            { day, month, year -> onDaySelect(LocalDate(year = year, month = month + 1, day = day)) }
        } else {
            null
        },
        onNextMonth = onNextMonth,
        onNextYear = onNextYear,
        onPreviousMonth = onPreviousMonth,
        onPreviousYear = onPreviousYear,
    )
}
