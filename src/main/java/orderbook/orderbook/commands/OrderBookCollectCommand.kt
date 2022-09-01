package orderbook.orderbook.commands

import orderbook.orderbook.OrderBook.Companion.instance
import orderbook.orderbook.OrderManager
import orderbook.orderbook.giveBuyItems
import orderbook.orderbook.hasBookSet
import orderbook.orderbook.log.logComplete
import orderbook.orderbook.takeBookSet
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OrderBookCollectCommand(om: OrderManager) : AbstractIdOrderBookCommand(om) {
    override fun onPlayerCommand(
        sender: CommandSender,
        player: Player,
        ignoredCommand: Command,
        ignoredLabel: String,
        args: Array<String>
    ): Boolean {
        val id = parseArgs(args, sender) ?: return true
        val inventory = player.inventory
        val order = orderManager.getOrder(id)
        order ?: run {
            sender.sendMessage("Unable to find order ", id.toString())
            return true
        }

        if (!order.stage.isMatched) {
            sender.sendMessage("Unable to collect order. Order is not matched")
            return true
        }

        if (!(inventory hasBookSet order)) {
            sender.sendMessage("You do not have the required trade books")
            return true
        }
        inventory takeBookSet order
        inventory giveBuyItems order
        if (!order.setCompleted()) {
            instance!!.logger.warning("Somehow failed to set order to completed even after checking the stage")
        }
        logComplete(player.name, order.sellItem, order.buyItem, order.id)
        return true
    }
}