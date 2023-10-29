package cash.atto.work

import io.cucumber.java.ParameterType
import java.time.Instant
import kotlin.time.Duration.Companion.days

enum class Day(private val supplier: () -> Instant) {
    TODAY({ Instant.now() }), TOMORROW({ Instant.now().plusSeconds(24.days.inWholeSeconds) });

    fun getInstant(): Instant {
        return supplier.invoke()
    }
}

class DayParameterType {
    @ParameterType("TODAY|TOMORROW")
    fun day(day: String): Day {
        return Day.valueOf(day)
    }
}