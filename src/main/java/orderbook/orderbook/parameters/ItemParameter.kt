package orderbook.orderbook.parameters

import orderbook.orderbook.parameters.values.itemSpecialFromArgument
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

fun matchArgument(arg: String): ItemParameter? {
    val mat = Material.matchMaterial(arg)
    if (mat != null && mat.isItem) {
        return MaterialParameter(mat)
    }
    return itemSpecialFromArgument(arg)?.let { SpecialParameter(it) }
}

interface ItemParameter {
    fun toItemStack(inv: PlayerInventory, count: Int): ItemStack
}