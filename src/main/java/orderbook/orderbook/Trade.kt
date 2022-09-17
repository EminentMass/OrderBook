package orderbook.orderbook

import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.max

data class Trade(val sellItem: ItemStack, val buyItem: ItemStack) {

    val inverse: Trade
        get() = Trade(buyItem, sellItem)

    infix fun isInverse(other: Trade): Boolean = inverse == other

    fun inventoryRequirement(): Int {
        return max(sellItem.inventoryRequirement(), buyItem.inventoryRequirement())
    }

    fun toNewOrder(): Order = Order(UUID.randomUUID(), OrderStage(), this)
}