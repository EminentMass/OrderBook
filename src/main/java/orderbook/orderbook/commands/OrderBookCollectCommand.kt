package orderbook.orderbook.commands

import orderbook.orderbook.*
import orderbook.orderbook.log.logComplete
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OrderBookCollectCommand(plugin: OrderBook) : AbstractIdOrderBookCommand(plugin) {
    override fun onPlayerCommand(
        sender: CommandSender,
        player: Player,
        ignoredCommand: Command,
        ignoredLabel: String,
        args: Array<String>
    ): Boolean {
        val id = parseArgs(args, sender) ?: return true
        val inventory = player.inventory
        val order = plugin.orderManager?.getOrder(id)
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
        order.setCompleted()

        plugin.logger.logComplete(player.name, order.sellItem, order.buyItem, order.id)
        return true
    }
}