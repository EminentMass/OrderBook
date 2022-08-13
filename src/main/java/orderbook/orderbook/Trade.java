package orderbook.orderbook;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Trade {
    private final ItemStack sellItem;
    private final ItemStack buyItem;

    public Trade(ItemStack sell, ItemStack buy) {
        sellItem = sell;
        buyItem = buy;
    }
    public Trade(Order order) {
        sellItem = order.getSellItem();
        buyItem = order.getBuyItem();
    }

    public Trade getInverse() {
        return new Trade(buyItem, sellItem);
    }

    public int inventoryRequirement() {
        return Math.max(req(sellItem), req(buyItem));
    }
    private int req(ItemStack stack) {

        if(isNothing(stack)) {
            return 0;
        }

        int stackSize = stack.getType().getMaxStackSize();
        int count = stack.getAmount();

        return (int) Math.ceil((float) count/ (float) stackSize);
    }

    public ItemStack getSellItem() {
        return sellItem;
    }
    public ItemStack getBuyItem() {
        return buyItem;
    }

    private boolean isNothing(ItemStack item) {
        return item.getType() == Material.AIR || item.getAmount() == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellItem, buyItem);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return sellItem.equals(trade.sellItem) && buyItem.equals(trade.buyItem);
    }
}
