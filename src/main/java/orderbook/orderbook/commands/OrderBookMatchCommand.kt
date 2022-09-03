package orderbook.orderbook.commands

import orderbook.orderbook.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OrderBookMatchCommand(plugin: OrderBook) : AbstractIdOrderBookCommand(plugin) {
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
            sender.sendMessage("Unable to find order. It may have already been completed")
            return true
        }

        if (!order.stage.isPosted) {
            sender.sendMessage("Unable to match order is already ", order.stage.step.toString())
            return true
        }

        // trade that will match
        val trade = order.trade.inverse

        // No need to check inventory requirements as they would be the same as the matched order
        if (!(inventory hasSellItems trade)) {
            sender.sendMessage("Unable to create order you do not have the required sell items")
            return true
        }
        val newMatchOrder = plugin.orderManager?.postOrder(trade) ?: return false
        inventory takeSellItems trade
        inventory addBookSet newMatchOrder
        sender.sendMessage(newMatchOrder.chatPostDisplay())
        return true
    }
}