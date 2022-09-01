package orderbook.orderbook.parameters

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

class MaterialParameter(mat: Material) : ItemParameter {
    private val material: Material

    init {
        assert(mat.isItem) { "Attempted to create MaterialParameter with non item Material" }
        material = mat
    }

    override fun toItemStack(inv: PlayerInventory, count: Int): ItemStack {
        // if one of these is not air or zero make sure they both are
        return if (material == Material.AIR || count == 0) {
            ItemStack(Material.AIR, 0)
        } else ItemStack(material, count)
    }
}