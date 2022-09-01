package orderbook.orderbook.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import orderbook.orderbook.Order
import orderbook.orderbook.OrderStage
import orderbook.orderbook.Trade
import java.io.IOException
import java.util.*

class OrderAdapter : TypeAdapter<Order?>() {

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: Order?) {

        value ?: run { writer.nullValue(); return }

        writer.beginObject()
        writer.name(orderIdName)
        writer.value(value.id.toString())
        writer.name(orderStageName)
        OrderStageAdapter().write(writer, value.stage)
        writer.name(orderTradeName)
        TradeAdapter().write(writer, value.trade) // cast value to trade
        writer.endObject()
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Order? {

        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }

        var id: UUID? = null
        var stage: OrderStage? = null
        var trade: Trade? = null

        // Allows possibility of other associated data that is not used here.
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                orderIdName -> id = UUID.fromString(reader.nextString())
                orderStageName -> stage = OrderStageAdapter().read(reader)
                orderTradeName -> trade = TradeAdapter().read(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return if (id == null || stage == null || trade == null) {
            null
        } else Order(id, stage, trade)
    }


}