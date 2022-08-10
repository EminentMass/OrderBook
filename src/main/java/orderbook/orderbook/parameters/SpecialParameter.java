package orderbook.orderbook.parameters;

import orderbook.orderbook.parameters.values.ItemSpecial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SpecialParameter implements ItemParameter {

    private final ItemSpecial parameter;

    public SpecialParameter(ItemSpecial item_parameter) {
        parameter = item_parameter;
    }

    @Override
    public ItemStack toItemStack(PlayerInventory inv, int count) {

        ItemStack item;

        if(parameter == ItemSpecial.MAINHAND){
            item = inv.getItemInMainHand();
        } else if (parameter == ItemSpecial.OFFHAND) {
            item = inv.getItemInOffHand();
        } else { // ItemSpecial.NOTHING
            return new ItemStack(Material.AIR, 0);
        }

        // No need to check if item is null as all cases covered
        if(item.getType() == Material.AIR) {
            return new ItemStack(Material.AIR, 0);
        }

        return item.asQuantity(count);
    }
}
