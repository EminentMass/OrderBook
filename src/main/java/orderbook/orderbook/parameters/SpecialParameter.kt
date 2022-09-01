package orderbook.orderbook.parameters

import orderbook.orderbook.parameters.values.ItemSpecial
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

class SpecialParameter(private val parameter: ItemSpecial) : ItemParameter {
    override fun toItemStack(inv: PlayerInventory, count: Int): ItemStack {
        val item: ItemStack = when (parameter) {
            ItemSpecial.MAINHAND -> inv.itemInMainHand
            ItemSpecial.OFFHAND -> inv.itemInOffHand
            ItemSpecial.NOTHING -> ItemStack(Material.AIR, 0)
        }

        return if (item.type == Material.AIR) {
            ItemStack(Material.AIR, 0)
        } else item.asQuantity(count)
    }
}