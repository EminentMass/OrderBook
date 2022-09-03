package orderbook.orderbook.commands

import orderbook.orderbook.*
import orderbook.orderbook.log.logPost
import orderbook.orderbook.parameters.ItemParameter
import orderbook.orderbook.parameters.parsers.parseSendItem
import orderbook.orderbook.parameters.parsers.parseSendUnsignedInteger
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

class OrderBookCommand(plugin: OrderBook) : AbstractOrderBookCommand(plugin) {
    override fun onPlayerCommand(
        sender: CommandSender,
        player: Player,
        ignoredCommand: Command,
        ignoredLabel: String,
        args: Array<String>
    ): Boolean {
        if (args.size < 4) {
            return false
        }
        val inventory: PlayerInventory = player.inventory
        val sellParam: ItemParameter = parseSendItem(args[0], sender) ?: return true
        val sellCount: Int = parseSendUnsignedInteger(args[1], sender) ?: return true
        val buyParam: ItemParameter = parseSendItem(args[2], sender) ?: return true
        val buyCount: Int = parseSendUnsignedInteger(args[3], sender) ?: return true
        val itemSell: ItemStack = sellParam.toItemStack(inventory, sellCount)
        val itemBuy: ItemStack = buyParam.toItemStack(inventory, buyCount)
        val trade = Trade(itemSell, itemBuy)
        val maxStack = 9 * 4
        if (trade.inventoryRequirement() > maxStack) {
            sender.sendMessage(
                String.format(
                    "Unable to create order, requires %d inventory slots maximum %d",
                    trade.inventoryRequirement(),
                    maxStack
                )
            )
            return true
        }
        if (!(inventory hasSellItems trade)) {
            sender.sendMessage("Unable to create order you do not have the required sell items")
            return true
        }
        val order: Order = plugin.orderManager?.postOrder(trade) ?: return false
        inventory takeSellItems trade
        inventory addBookSet order
        sender.sendMessage(order.chatPostDisplay())
        plugin.logger.logPost(player.name, order.sellItem, order.buyItem, order.id)
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): List<String> {

        // /orderbook <sell count> <sell item> <buy count> <buy item>
        return when (args.size) {
            1 -> tabCompleteItem(args[0])
            2 -> tabCompleteNumber(args[1])
            3 -> tabCompleteItem(args[2])
            4 -> tabCompleteNumber(args[3])
            else -> emptyList()
        }
    }
}