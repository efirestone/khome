package khome.values

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class HSColor(val hue: Double, val saturation: Double) {

    companion object : KSerializer<HSColor> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("HSColor", PrimitiveKind.INT) // TODO

        override fun deserialize(decoder: Decoder): HSColor {
            TODO("Not yet implemented")
        }

        override fun serialize(encoder: Encoder, value: HSColor) {
            TODO("Not yet implemented")
        }

    }

//    @Suppress("UNCHECKED_CAST")
//    companion object : KhomeTypeAdapter<HSColor> {
//        fun from(hue: Double, saturation: Double) =
//            from(listOf(hue, saturation))
//
//        override fun <P> from(value: P): HSColor {
//            val values = value as List<Double>
//            check(values.size == 2) { "To many values for HSColor creation. ${values.size} values are too much. Allowed are exactly 2 values." }
//            return HSColor(hue = values[0], saturation = values[1])
//        }
//
//        override fun <P> to(value: HSColor): P {
//            return doubleArrayOf(value.hue, value.saturation) as P
//        }
//    }
}
