package orderbook.orderbook.commands

import orderbook.orderbook.OrderBook
import orderbook.orderbook.parameters.values.ItemSpecial
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

private val itemTabCompletions: List<String> = Material.values()
        .filter {it.isItem}
        .map {it.key.toString()}
        .plus(ItemSpecial.values().map { it.nameLower() })

private val numberTabCompletions: List<String> = (0..10).map { it.toString() }

abstract class AbstractOrderBookCommand(val plugin: OrderBook): TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if(sender !is Player) {
            sender.sendMessage("Only players can use this command.")
            return true
        }

        return onPlayerCommand(sender ,sender , command, label, args)
    }

    abstract fun onPlayerCommand(sender: CommandSender, player: Player, ignoredCommand: Command, ignoredLabel: String, args: Array<String>): Boolean

    protected fun tabCompleteItem( partial: String): List<String> {
        return itemTabCompletions
            .filter { s ->
                s.startsWith(partial) || run {
                    val parts: List<String> = s.split(":")
                    parts.size == 2 && parts[1].startsWith(partial)
                }
            }
    }

    protected fun tabCompleteNumber(partial: String): List<String> = numberTabCompletions.filter { s -> s.startsWith(partial) }

    protected fun tabCompleteID( partial: String): List<String>? = plugin.orderManager?.getOrders()
        ?.filter { o -> !o.stage.isCompleted }
        ?.map { o -> o.id.toString() }
        ?.filter { i -> i.startsWith(partial) }
}
