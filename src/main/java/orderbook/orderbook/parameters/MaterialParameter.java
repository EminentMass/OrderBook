package orderbook.orderbook.parameters;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MaterialParameter implements ItemParameter {

    private final Material material;

    public MaterialParameter(Material item) {

        assert item.isItem() : "Attempted to create MaterialParameter with non item Material";

        material = item;
    }

    @Override
    public ItemStack toItemStack(PlayerInventory inv, int count) {

        // if one of these is not air or zero make sure they both are
        if(material == Material.AIR || count == 0) {
            return new ItemStack(Material.AIR, 0);
        }

        return new ItemStack(material, count);
    }
}
