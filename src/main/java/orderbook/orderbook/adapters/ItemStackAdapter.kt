package orderbook.orderbook.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.bukkit.inventory.ItemStack
import java.io.IOException

class ItemStackAdapter : TypeAdapter<ItemStack?>() {

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: ItemStack?) {
        value ?: run { writer.nullValue(); return }
        // We wrap items in base64 streams because the underlying implementation is unknown, and it may not be compatible with json
        val data = value.toBase64()
        writer.beginObject()
        writer.name(itemStackName)
        writer.value(data)
        writer.endObject()
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): ItemStack? {

        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }

        var data: String? = null

        // Allows the possibility of other data being associated with items.
        reader.beginObject()
        while (reader.hasNext()) {
            if (reader.nextName() == itemStackName) {
                data = reader.nextString()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()

        return data?.fromBase64()
    }
}