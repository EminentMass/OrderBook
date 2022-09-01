package orderbook.orderbook.commands

import net.kyori.adventure.text.Component
import orderbook.orderbook.Order
import orderbook.orderbook.OrderManager
import orderbook.orderbook.parameters.parsers.parseSendItem
import orderbook.orderbook.parameters.parsers.parseSendUnsignedInteger
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class OrderBookListCommand(om: OrderManager) : AbstractOrderBookCommand(om) {
    override fun onPlayerCommand(
        sender: CommandSender,
        player: Player,
        ignoredCommand: Command,
        ignoredLabel: String,
        args: Array<String>
    ): Boolean {
        val inventory = player.inventory
        val entries: List<Component> = when (args.size) {
            0 -> searchOrders()
            1 -> {
                val param = parseSendItem(args[0], sender) ?: return true
                searchOrders(param.toItemStack(inventory, 1))
            }
            2 -> {
                val param = parseSendItem(args[0], sender) ?: return true
                val count = parseSendUnsignedInteger(args[1], sender) ?: return true
                searchOrders(param.toItemStack(inventory, count))
            }
            else -> return false
        }
        val bar = "-------------------------"
        val header: Component = Component.text("$bar\nOrderList\n$bar")

        sender.sendMessage(header)
        for (comp in entries) {
            sender.sendMessage(comp)
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): List<String> {
        return when (args.size) {
            1 -> tabCompleteItem(args[0])
            2 -> tabCompleteNumber(args[1])
            else -> emptyList()
        }
    }

    private fun searchOrders(): List<Component> = orderManager.getActiveOrders().map { obj: Order -> obj.chatListDisplay() }

    private fun searchOrders(item: ItemStack): List<Component> {
        val mat = item.type
        val count = item.amount
        return orderManager.getActiveOrders()
            .filter {o -> o.sellItem.type === mat && o.sellItem.amount >= count }
            .map { obj: Order -> obj.chatListDisplay() }
    }
}