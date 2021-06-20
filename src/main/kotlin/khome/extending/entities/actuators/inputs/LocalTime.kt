//package khome.extending.entities.actuators.inputs
//
//import kotlinx.serialization.Serializable
//
///**
// * kotlinx.datetime doesn't currently provide a LocalTime object, so we'll provide our own.
// * It's likely that an official one will exist at some point and we can remove this:
// *   https://github.com/Kotlin/kotlinx-datetime/issues/57
// */
//@Serializable
//data class LocalTime(val hour: Int, val minute: Int, val second: Int = 0, val nanosecond: Int = 0) : Comparable<LocalTime> {
//    override fun compareTo(other: LocalTime): Int {
//        if (hour > other.hour) { return 1 }
//        if (hour < other.hour) { return -1 }
//        if (minute > other.minute) { return 1 }
//        if (minute < other.minute) { return -1 }
//        if (second > other.second) { return 1 }
//        if (second < other.second) { return -1 }
//        if (nanosecond > other.nanosecond) { return 1 }
//        if (nanosecond < other.nanosecond) { return -1 }
//        return 0
//    }
//}
