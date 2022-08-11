package orderbook.orderbook.adapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ItemStackAdapter extends TypeAdapter<ItemStack> {

    public static final String itemStackName = "ItemStackData";

    @Override
    public void write(JsonWriter writer, ItemStack value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }

        // We wrap items in base64 streams because the underlying implementation is unknown, and it may not be compatible with json
        String data = ItemStackBase64Converter.toBase64(value);

        writer.beginObject();
        writer.name(itemStackName);
        writer.value(data);
        writer.endObject();
    }

    @Override
    public ItemStack read(JsonReader reader) throws IOException {
        if(reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        String data = null;

        // Allows the possibility of other data being associated with items.
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(itemStackName)) {
                data = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        if(data == null) {
            return null;
        }

        return ItemStackBase64Converter.fromBase64(data);
    }
}
