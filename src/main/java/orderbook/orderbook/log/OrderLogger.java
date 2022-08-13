package orderbook.orderbook.log;

import orderbook.orderbook.OrderBook;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OrderLogger {
    public static void logPost(String playerName, ItemStack sellItem, ItemStack buyItem, UUID id) {
        String msg = String.format("%s posted %s for %s id %s", playerName, sellItem.toString(), buyItem.toString(), id.toString());
        logBase(msg);
    }
    public static void logMatch(ItemStack sellItem, ItemStack buyItem, UUID id1, UUID id2) {
        String msg = String.format("Matched trade of %s for %s id's %s and %s", sellItem.toString(), buyItem.toString(), id1.toString(), id2.toString());
        logBase(msg);
    }
    public static void logComplete(String playerName, ItemStack sellItem, ItemStack buyItem, UUID id) {
        String msg = String.format("%s completed %s for %s id %s", playerName, sellItem.toString(), buyItem.toString(), id.toString());
        logBase(msg);
    }
    public static void logRemove(String playerName, ItemStack sellItem, ItemStack buyItem, UUID id) {
        String msg = String.format("%s removed %s for %s id %s", playerName, sellItem.toString(), buyItem.toString(), id.toString());
        logBase(msg);
    }
    public static void warnInvalidState(String msg) {
        OrderBook.getInstance().getLogger().warning("> Invalid State: " + msg);
    }
    private static void logBase(String msg) {
        OrderBook.getInstance().getLogger().info("> State Change: " + msg);
    }
}
