package orderbook.orderbook.adapters;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ItemStackBase64Converter {

    public static @NotNull String toBase64(@NotNull ItemStack value) throws IOException {

        // We wrap items in base64 streams because the underlying implementation is unknown
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream stream = new BukkitObjectOutputStream(outputStream);

        stream.writeObject(value);
        stream.close();

        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    public static ItemStack fromBase64(@NotNull String data) throws IOException {
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
