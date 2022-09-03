package orderbook.orderbook.log

import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.logging.Logger

fun Logger.logPost(playerName: String?, sellItem: ItemStack, buyItem: ItemStack, id: UUID) {
    val msg = String.format(
        "%s posted %s for %s id %s",
        playerName,
        sellItem.toString(),
        buyItem.toString(),
        id.toString()
    )
    logBase(msg)
}

fun Logger.logMatch(sellItem: ItemStack, buyItem: ItemStack, id1: UUID, id2: UUID) {
    val msg = String.format(
        "Matched trade of %s for %s id's %s and %s",
        sellItem.toString(),
        buyItem.toString(),
        id1.toString(),
        id2.toString()
    )
    logBase(msg)
}

fun Logger.logComplete(playerName: String?, sellItem: ItemStack, buyItem: ItemStack, id: UUID) {
    val msg = String.format(
        "%s completed %s for %s id %s",
        playerName,
        sellItem.toString(),
        buyItem.toString(),
        id.toString()
    )
    logBase(msg)
}

fun Logger.logRemove(playerName: String?, sellItem: ItemStack, buyItem: ItemStack, id: UUID) {
    val msg = String.format(
        "%s removed %s for %s id %s",
        playerName,
        sellItem.toString(),
        buyItem.toString(),
        id.toString()
    )
    logBase(msg)
}

fun Logger.warnInvalidState(msg: String) {
    warning("> Invalid State: $msg")
}

private fun Logger.logBase(msg: String) {
    info("> State Change: $msg")
}