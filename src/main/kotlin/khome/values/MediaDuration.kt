@file:Suppress("DataClassPrivateConstructor")

package khome.values

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class MediaDuration private constructor(val value: Double) {
    override fun toString(): String = value.toString()

    companion object : KSerializer<MediaDuration> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("MediaDuration", PrimitiveKind.DOUBLE)
        override fun deserialize(decoder: Decoder) = MediaDuration(decoder.decodeDouble())
        override fun serialize(encoder: Encoder, value: MediaDuration) = encoder.encodeDouble(value.value)
    }
}
