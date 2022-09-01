package orderbook.orderbook.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import orderbook.orderbook.Trade
import org.bukkit.inventory.ItemStack
import java.io.IOException

class TradeAdapter : TypeAdapter<Trade?>() {

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: Trade?) {

        value ?: run {writer.nullValue(); return}

        writer.beginObject()
        writer.name(tradeSellItemName)
        ItemStackAdapter().write(writer, value.sellItem)
        writer.name(tradeBuyItemName)
        ItemStackAdapter().write(writer, value.buyItem)
        writer.endObject()
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Trade? {

        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }

        var buyItem: ItemStack? = null
        var sellItem: ItemStack? = null

        // Allows possibility of other associated data that is not used here.
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                tradeSellItemName -> sellItem = ItemStackAdapter().read(reader)
                tradeBuyItemName -> buyItem = ItemStackAdapter().read(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return if (buyItem == null || sellItem == null) {
            null
        } else Trade(sellItem, buyItem)
    }
}