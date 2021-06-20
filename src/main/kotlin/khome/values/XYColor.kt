package khome.values

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class XYColor(val x: Double, val y: Double) {
    companion object : KSerializer<XYColor> {
        override fun deserialize(decoder: Decoder): XYColor {
            TODO("Not yet implemented")
        }

        override val descriptor: SerialDescriptor
            get() = TODO("Not yet implemented")

        override fun serialize(encoder: Encoder, value: XYColor) {
            TODO("Not yet implemented")
        }

    }

//    @Suppress("UNCHECKED_CAST")
//    companion object : KhomeTypeAdapter<XYColor> {
//        fun from(x: Double, y: Double) =
//            from(listOf(x, y))
//
//        override fun <P> from(value: P): XYColor {
//            val values = value as List<Double>
//            check(values.size == 2) { "To many values for XYColor creation. ${values.size} values are too much. Allowed are exactly 2 values." }
//            return XYColor(x = values[0], y = values[1])
//        }
//
//        override fun <P> to(value: XYColor): P {
//            return doubleArrayOf(value.x, value.y) as P
//        }
//    }
}
