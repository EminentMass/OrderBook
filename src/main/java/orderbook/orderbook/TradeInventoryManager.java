package orderbook.orderbook;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class TradeInventoryManager {

    public static void addBookSet(Order order, PlayerInventory inventory) {
        List<ItemStack> books = generateBookSet(order);

        for (ItemStack book : books) {
            TradeInventoryManager.addOrDrop(book, inventory);
        }
    }

    private static List<ItemStack> generateBookSet(Order order) {

        List<ItemStack> books = new ArrayList<>();

        int bookCount = order.inventoryRequirement();
        for(int i=1;i<=bookCount;i++) {
            ItemStack bookBase = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMeta = (BookMeta) bookBase.getItemMeta();

            TextComponent author = Component.text("Order Book Trading");
            TextComponent title = Component.text("Trade ")
                    .append(order.idDisplay())
                    .append(Component.text(format(" %d/%d", i, bookCount)));
            TextComponent page = Component.text("Rights for trade ")
                    .append(order.idDisplay())
                    .append(Component.text(" selling "))
                    .append(order.displaySellItem())
                    .append(Component.text(" for "))
                    .append(order.displayBuyItem())
                    .append(Component.text(format(" part %d/%d", i, bookCount)));

            BookMeta bookMetaFinal = bookMeta.toBuilder().author(author).title(title).addPage(page).build();

            bookMetaFinal.setGeneration(BookMeta.Generation.ORIGINAL);

            bookBase.setItemMeta(bookMetaFinal);

            // The Stamp and the Generation are used to verify the authenticity of an order book
            // The stamp for a given order is the same for each book for that order
            // It is only required that you have the necessary count of books with the stamp
            bookBase.lore(bookStamp(order));

            books.add(bookBase);
        }

        return books;
    }

    private static List<Component> bookStamp(Order order) {
        return Arrays.asList(bookStampHeader(), order.idDisplay());
    }

    private static TextComponent bookStampHeader() {
        return Component.text("Order Book Stamp").decorate(TextDecoration.UNDERLINED);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasBookSet(Order order, PlayerInventory inventory) {
        List<Component> stamp = bookStamp(order);

        return Arrays.stream(inventory.getContents()).filter(i -> stampedWith(i, stamp))
                .count() == order.inventoryRequirement();
    }

    private static boolean stampedWith(ItemStack item, List<Component> stamp) {

        if(item == null) {
            return false;
        }

        if(item.getType() != Material.WRITTEN_BOOK) {
            return false;
        }

        BookMeta meta = (BookMeta) item.getItemMeta();

        if(!meta.hasGeneration()) {
            return false;
        }

        if(meta.getGeneration() != BookMeta.Generation.ORIGINAL) {
            return false;
        }

        List<Component> lore = item.lore();

        if(lore == null) {
            return false;
        }

        if(lore.size() != 2) {
            return false;
        }

        return lore.equals(stamp);
    }

    public static void takeBookSet(Order order, PlayerInventory inventory) {

        List<Component> stamp = bookStamp(order);

        ItemStack[] contents = inventory.getContents();

        List<ItemStack> toRemove = Arrays.stream(contents).filter(i -> stampedWith(i, stamp)).collect(Collectors.toList());

        for (ItemStack book : toRemove) {
            inventory.removeItemAnySlot(book);
        }
    }

    public static void takeSellItems(Trade trade, PlayerInventory inventory) {
        inventory.removeItemAnySlot(trade.getSellItem());
    }
    public static void giveBuyItems(Order order, PlayerInventory inventory) {
        ItemStack item = order.getBuyItem();
        if(item.getType() == Material.AIR || item.getAmount() == 0) { return; }
        giveItems(item, inventory);
    }

    public static void giveSellItems(Order order, PlayerInventory inventory) {
        ItemStack item = order.getSellItem();
        if(item.getType() == Material.AIR || item.getAmount() == 0) { return; }
        giveItems(item, inventory);
    }

    private static void giveItems(ItemStack item, PlayerInventory inventory) {
        int stackSize = item.getMaxStackSize();
        int count = item.getAmount();

        int stacks = count/stackSize;

        int leftover = count % stackSize;

        for(int i=0; i<stacks; i++) {
            addOrDrop(item.asQuantity(stackSize), inventory);
        }
        addOrDrop(item.asQuantity(leftover), inventory);
    }

    public static void addOrDrop(ItemStack item, PlayerInventory inventory) {
        HashMap<Integer, ItemStack> leftover = inventory.addItem(item);
        if (leftover.size() > 0) {
            Location loc = inventory.getLocation();
            assert loc != null;
            World world = loc.getWorld();
            world.dropItemNaturally(loc, item);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasSellItems(Trade trade, PlayerInventory inventory) {
        return countOf(trade.getSellItem(), inventory) >= trade.getSellItem().getAmount();
    }

    private static int countOf(ItemStack item, PlayerInventory inventory) {

        ItemStack[] c = inventory.getContents();

        int[] available = {0};

        Arrays.stream(c).filter(i -> {
            if(i == null) {
                return false;
            }
            return i.asOne().equals(item.asOne());
        }).forEach(i -> available[0] += i.getAmount());

        return available[0];
    }
}
