package orderbook.orderbook.parameters;

import orderbook.orderbook.parameters.values.ItemSpecial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public interface ItemParameter {

    static ItemParameter matchArgument(String arg) {
        Material mat = Material.matchMaterial(arg);

        if( mat != null && mat.isItem() ) {
            return new MaterialParameter(mat);
        }

        ItemSpecial param = ItemSpecial.matchParameter(arg);

        if( param != null ){
            return new SpecialParameter(param);
        }

        return null;
    }

    ItemStack toItemStack(PlayerInventory inv, int count);
}
