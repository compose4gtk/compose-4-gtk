import io.github.compose4gtk.adw.adwApplication
import io.github.compose4gtk.adw.components.ApplicationWindow
import io.github.compose4gtk.adw.components.HeaderBar
import io.github.compose4gtk.gtk.components.Box
import io.github.compose4gtk.gtk.components.Calendar
import io.github.compose4gtk.modifier.Modifier
import io.github.compose4gtk.modifier.margin
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.gnome.gtk.Orientation

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    adwApplication("my.example.hello-app", args) {
        ApplicationWindow("Test", onClose = ::exitApplication) {
            Box(orientation = Orientation.VERTICAL) {
                HeaderBar()

                Box(
                    modifier = Modifier
                        .margin(16),
                ) {
                    Calendar(
                        date = LocalDate(year = 1989, month = Month.JANUARY, dayOfMonth = 1),
                        onDaySelect = { date ->
                            logger.info { "selected $date" }
                        },
                        onNextYear = {
                            logger.info { "navigated to next year" }
                        },
                    )
                }
            }
        }
    }
}
