package orderbook.orderbook.commands

import orderbook.orderbook.*
import orderbook.orderbook.log.logRemove
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

class OrderBookRemoveCommand(om: OrderManager) : AbstractIdOrderBookCommand(om) {
    override fun onPlayerCommand(
        sender: CommandSender,
        player: Player,
        ignoredCommand: Command,
        ignoredLabel: String,
        args: Array<String>
    ): Boolean {
        val id = parseArgs(args, sender) ?: return true
        val inventory: PlayerInventory = player.inventory
        val order: Order? = orderManager.getOrder(id)

        order ?: run {
            sender.sendMessage("Unable to find order %s", id.toString())
            return true
        }

        if (!order.stage.isPosted) {
            sender.sendMessage("Unable to remove order. Order is no longer posted")
            return true
        }

        if (!(inventory hasBookSet order)) {
            sender.sendMessage("You do not have the required trade books")
            return true
        }
        inventory takeBookSet order
        inventory giveSellItems order
        if (!orderManager.removeOrder(id)) {
            OrderBook.instance!!.logger.warning("Somehow failed to remove order after checking it was there")
        }
        logRemove(player.name, order.sellItem, order.buyItem, order.id)
        return true
    }
}