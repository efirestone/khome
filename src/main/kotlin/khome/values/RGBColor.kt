package khome.values

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure

@Serializable
data class RGBColor(val red: Int, val green: Int, val blue: Int) {
    companion object : KSerializer<RGBColor> {
        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor = listSerialDescriptor<Int>()

//            buildSerialDescriptor(
//            serialName = "RGBColor",
//            kind = StructureKind.LIST
//        )
        override fun deserialize(decoder: Decoder): RGBColor {
            decoder.decodeStructure(ListSerializer(Int.serializer()).descriptor) {
                // this.decodeCollectionSize
                val one = this.decodeIntElement(Int.serializer().descriptor, 0)

//                check(integers.size == 3) { "To many values for RGB creation. List has size ${integers.size}. Allowed are exactly 3 values." }
//                return RGBColor(red = integers[0], green = integers[1], blue = integers[2])
                return RGBColor(0, 0, 0)
            }
        }
//        override fun serialize(encoder: Encoder, value: RGBColor) = encoder.encodeInt(value.value)
// .. serialize method of a corresponding serializer
        override fun serialize(encoder: Encoder, value: RGBColor) {

//            .encodeSerializableValue(ListSerializer()) encoder.encodeStructure(descriptor) {
    // encodeStructure encodes beginning and end of the structure
    // encode 'int' property as Int
//    encodeIntElement(descriptor, index = 0, value.int)
    // encode 'stringList' property as List<String>
//    encodeSerializableElement(descriptor, index = 1, serializer<List<String>>, value.stringList)
    // don't encode 'alwaysZero' property because we decided to do so
        } // end of the structure
    }

//    companion object : KhomeTypeAdapter<RGBColor> {
//        override fun <P> from(value: P):
//
//        @Suppress("UNCHECKED_CAST")
//        override fun <P> to(value: RGBColor): P {
//            return intArrayOf(value.red, value.green, value.blue) as P
//        }
//    }
}

//object RGBColorSerializer : JsonTransformingSerializer<RGBColor>(ListSerializer(Int.serializer())) {
//    // If response is not an array, then it is a single object that should be wrapped into the array
//    override fun transformDeserialize(element: JsonElement): JsonElement =
//        if (element !is JsonArray) JsonArray(listOf(element)) else element
//}