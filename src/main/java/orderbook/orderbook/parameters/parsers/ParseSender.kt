package orderbook.orderbook.parameters.parsers

import orderbook.orderbook.parameters.ItemParameter
import orderbook.orderbook.parameters.matchArgument
import org.bukkit.command.CommandSender
import java.util.*

fun parseSendItem(arg: String, sender: CommandSender): ItemParameter? {
    return matchArgument(arg) ?: run {
        sender.sendMessage(String.format("Invalid item type parameter: %s", arg))
        null
    }
}

@Suppress("unused")
fun parseSendBoolean(arg: String, sender: CommandSender): Boolean? {
    return if (arg.equals("true", ignoreCase = true)) {
        true
    } else if (arg.equals("false", ignoreCase = true)) {
        false
    } else {
        sender.sendMessage("Boolean parameters must be either \"true\" or \"false\"")
        null
    }
}

@Suppress("unused")
fun parseSendInteger(arg: String, sender: CommandSender): Int? {
    return try {
        arg.toInt()
    } catch (e: NumberFormatException) {
        sender.sendMessage(
            String.format(
                "Integer parameters must be a value between %d %d",
                Int.MIN_VALUE,
                Int.MAX_VALUE
            )
        )
        null
    }
}

fun parseSendUnsignedInteger(arg: String?, sender: CommandSender): Int? {
    return try {
        Integer.parseUnsignedInt(arg)
    } catch (e: NumberFormatException) {
        sender.sendMessage(
            String.format(
                "Unsigned Integer parameters must be a value between %d %d",
                0,
                Int.MAX_VALUE
            )
        )
        null
    }
}

fun parseSendUUID(arg: String?, sender: CommandSender): UUID? {
    return try {
        UUID.fromString(arg)
    } catch (e: IllegalArgumentException) {
        sender.sendMessage("UUID parameters must be a properly formatted Universal Unique Identifier")
        null
    }
}