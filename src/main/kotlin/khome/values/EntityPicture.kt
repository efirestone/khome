@file:Suppress("DataClassPrivateConstructor")

package khome.values

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data class EntityPicture private constructor(val value: String) {
    override fun toString(): String = value

    companion object : KSerializer<EntityPicture> {
        override fun deserialize(decoder: Decoder): EntityPicture {
            TODO("Not yet implemented")
        }

        override val descriptor: SerialDescriptor
            get() = TODO("Not yet implemented")

        override fun serialize(encoder: Encoder, value: EntityPicture) {
            TODO("Not yet implemented")
        }

    }
//    companion object : KhomeTypeAdapter<EntityPicture> {
//        override fun <P> from(value: P): EntityPicture {
//            return EntityPicture(value as String)
//        }
//
//        @Suppress("UNCHECKED_CAST")
//        override fun <P> to(value: EntityPicture): P {
//            return value.value as P
//        }
//    }
}
