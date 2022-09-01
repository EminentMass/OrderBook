package orderbook.orderbook.commands

import orderbook.orderbook.OrderManager
import orderbook.orderbook.parameters.parsers.parseSendUUID
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.*

abstract class AbstractIdOrderBookCommand(om: OrderManager) : AbstractOrderBookCommand(om) {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): List<String>? = when (args.size) {
        1 -> tabCompleteID(args[0])
        else -> emptyList()
    }

    protected fun parseArgs(args: Array<String>, sender: CommandSender): UUID? = args.firstOrNull()?.let { parseSendUUID(it, sender) }
}