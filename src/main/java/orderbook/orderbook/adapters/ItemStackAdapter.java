package orderbook.orderbook.adapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream stream = new BukkitObjectOutputStream(outputStream);

        stream.writeObject(value);
        stream.close();

        String data = Base64Coder.encodeLines(outputStream.toByteArray());

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

        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        BukkitObjectInputStream stream = new BukkitObjectInputStream(inputStream);

        // on exception out will still equal null
        ItemStack out = null;
        try {
            out = (ItemStack) stream.readObject();
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }

        stream.close();

        return out;
    }
}
