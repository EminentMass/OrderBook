package orderbook.orderbook.log

import orderbook.orderbook.OrderBook
import org.bukkit.inventory.ItemStack
import java.util.*

fun logPost(playerName: String?, sellItem: ItemStack, buyItem: ItemStack, id: UUID) {
    val msg = String.format(
        "%s posted %s for %s id %s",
        playerName,
        sellItem.toString(),
        buyItem.toString(),
        id.toString()
    )
    logBase(msg)
}

fun logMatch(sellItem: ItemStack, buyItem: ItemStack, id1: UUID, id2: UUID) {
    val msg = String.format(
        "Matched trade of %s for %s id's %s and %s",
        sellItem.toString(),
        buyItem.toString(),
        id1.toString(),
        id2.toString()
    )
    logBase(msg)
}

fun logComplete(playerName: String?, sellItem: ItemStack, buyItem: ItemStack, id: UUID) {
    val msg = String.format(
        "%s completed %s for %s id %s",
        playerName,
        sellItem.toString(),
        buyItem.toString(),
        id.toString()
    )
    logBase(msg)
}

fun logRemove(playerName: String?, sellItem: ItemStack, buyItem: ItemStack, id: UUID) {
    val msg = String.format(
        "%s removed %s for %s id %s",
        playerName,
        sellItem.toString(),
        buyItem.toString(),
        id.toString()
    )
    logBase(msg)
}

fun warnInvalidState(msg: String) {
    OrderBook.instance?.logger?.warning("> Invalid State: $msg") ?:
    throw Exception("OrderBook can't access logger")
}

private fun logBase(msg: String) {
    OrderBook.instance?.logger?.info("> State Change: $msg") ?:
    throw Exception("OrderBook can't access logger")
}