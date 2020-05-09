package khome.core.entities.system

import khome.core.entities.EntitySubject
import khome.core.safeGet
import mu.KLogger
import mu.KotlinLogging
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

class Sun : EntitySubject<String>("sun", "sun") {
    val logger: KLogger = KotlinLogging.logger {}
    val isUp get() = state == "above_horizon"
    val isDown get() = state == "below_horizon"

    val nextSunrise get() = getNextSunPosition("next_rising")
    val nextSunset get() = getNextSunPosition("next_setting")

    private fun getNextSunPosition(nextPosition: String): LocalDateTime {
        val nextSunPositionChange: String = attributes.safeGet(nextPosition)
        return convertUtcToLocalDateTime(nextSunPositionChange)
    }

    private fun convertUtcToLocalDateTime(utcDateTime: String): LocalDateTime {
        val offsetDateTime = OffsetDateTime.parse(utcDateTime)
        val zonedDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault())
        return zonedDateTime.toLocalDateTime()
    }
}
