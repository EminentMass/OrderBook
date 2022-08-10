package orderbook.orderbook.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import orderbook.orderbook.OrderStage;
import orderbook.orderbook.OrderStep;

import java.io.IOException;
import java.util.UUID;

public class OrderStageAdapter extends TypeAdapter<OrderStage> {

    public static final String OrderStageStepName = "OrderStageStep";
    public static final String OrderStageMatchName = "OrderStageMatch";

    @Override
    public void write(JsonWriter writer, OrderStage value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }

        OrderStep step = value.getStep();

        writer.beginObject();
        writer.name(OrderStageStepName);
        writer.value(step.toString());

        // only write the match id if the order has been matched, and the id is present
        if(step != OrderStep.POSTED && value.getMatch().isPresent()) {
            writer.name(OrderStageMatchName);
            writer.value(value.getMatch().get().toString());
        }
        writer.endObject();
    }

    @Override
    public OrderStage read(JsonReader reader) throws IOException {
        if(reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        OrderStep step = null;
        UUID match = null;

        // Allows possibility of other associated data that is not used here.
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals(OrderStageStepName)) {
                step = OrderStep.valueOf(reader.nextString());
            } else if(name.equals(OrderStageMatchName)) {
                match = UUID.fromString(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        // Match can be null as long as step is posted
        if(step == null || ( step != OrderStep.POSTED && match == null) ) {
            return null;
        }

        return new OrderStage(match, step);
    }
}
