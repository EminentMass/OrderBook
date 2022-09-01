package orderbook.orderbook.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import orderbook.orderbook.OrderStage
import orderbook.orderbook.OrderStep
import java.io.IOException
import java.util.*

class OrderStageAdapter : TypeAdapter<OrderStage?>() {

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: OrderStage?) {

        value ?: run { writer.nullValue(); return }

        val step = value.step
        writer.beginObject()
        writer.name(orderStageStepName)
        writer.value(step.toString())

        // only write the match id if the order has been matched, and the id is present
        if (step !== OrderStep.POSTED && value.match != null) {
            writer.name(orderStageMatchName)
            writer.value(value.match.toString())
        }
        writer.endObject()
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): OrderStage? {

        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }

        var step: OrderStep? = null
        var match: UUID? = null

        // Allows possibility of other associated data that is not used here.
        reader.beginObject()
        while (reader.hasNext()) {
            when(reader.nextName()) {
                orderStageStepName -> step = OrderStep.valueOf(reader.nextString())
                orderStageMatchName -> match = UUID.fromString(reader.nextString())
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        // Match can be null as long as step is posted
        return if (step == null || step !== OrderStep.POSTED && match == null) {
            null
        } else OrderStage(match, step)
    }
}