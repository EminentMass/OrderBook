package orderbook.orderbook.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import orderbook.orderbook.Trade;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class TradeAdapter extends TypeAdapter<Trade> {

    public static final String TradeSellItemName = "TradeSellItem";
    public static final String TradeBuyItemName = "TradeBuyItem";

    @Override
    public void write(JsonWriter writer, Trade value) throws IOException {
        if(value == null) {
            writer.nullValue();
            return;
        }

        writer.beginObject();
        writer.name(TradeSellItemName);
        new ItemStackAdapter().write(writer, value.getSellItem());
        writer.name(TradeBuyItemName);
        new ItemStackAdapter().write(writer, value.getBuyItem());
        writer.endObject();
    }

    @Override
    public Trade read(JsonReader reader) throws IOException {
        if(reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        ItemStack buyItem = null;
        ItemStack sellItem = null;

        // Allows possibility of other associated data that is not used here.
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(TradeSellItemName)) {
                sellItem = new ItemStackAdapter().read(reader);
            } else if (name.equals(TradeBuyItemName)) {
                buyItem = new ItemStackAdapter().read(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        if(buyItem == null || sellItem == null) {
            return null;
        }

        return new Trade(sellItem, buyItem);
    }
}
