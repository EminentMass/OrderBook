package orderbook.orderbook.adapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import orderbook.orderbook.Order;
import orderbook.orderbook.OrderStage;
import orderbook.orderbook.Trade;

import java.io.IOException;
import java.util.UUID;

public class OrderAdapter extends TypeAdapter<Order> {

    public static final String OrderIdName = "OrderId";
    public static final String OrderStageName = "OrderStage";
    public static final String OrderTradeName = "OrderTrade";

    @Override
    public void write(JsonWriter writer, Order value) throws IOException {
        if(value == null){
            writer.nullValue();
            return;
        }

        writer.beginObject();
        writer.name(OrderIdName);
        writer.value(value.getId().toString());
        writer.name(OrderStageName);
        new OrderStageAdapter().write(writer, value.getStage());
        writer.name(OrderTradeName);
        new TradeAdapter().write(writer, value.getTrade()); // cast value to trade
        writer.endObject();
    }

    @Override
    public Order read(JsonReader reader) throws IOException {
        if(reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        UUID id = null;
        OrderStage stage = null;
        Trade trade = null;

        // Allows possibility of other associated data that is not used here.
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case OrderIdName -> id = UUID.fromString(reader.nextString());
                case OrderStageName -> stage = new OrderStageAdapter().read(reader);
                case OrderTradeName -> trade = new TradeAdapter().read(reader);
                default -> reader.skipValue();
            }
        }
        reader.endObject();

        if(id == null || stage == null || trade == null) {
            return null;
        }

        return new Order(id, stage, trade);
    }
}
