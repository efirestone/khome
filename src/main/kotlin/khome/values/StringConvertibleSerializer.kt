//package khome.values
//
//import kotlinx.serialization.KSerializer
//import kotlinx.serialization.descriptors.PrimitiveKind
//import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
//import kotlinx.serialization.descriptors.SerialDescriptor
//import kotlinx.serialization.encoding.Decoder
//import kotlinx.serialization.encoding.Encoder
//
//interface StringConvertible {
//    init(string: String)
//
//    override fun toString(): String
//}
//
//class StringConvertibleSerializer<T>() : KSerializer<T> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(T::class.toString(), PrimitiveKind.STRING)
//    override fun serialize(encoder: Encoder, value: T) {
//        encoder.encodeString(value.toString())
//    }
//
//    override fun deserialize(decoder: Decoder): T {
//        return T(decoder.decodeString())
//    }
//}