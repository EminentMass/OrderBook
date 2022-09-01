package orderbook.orderbook.parameters.values

import java.util.Locale

fun itemSpecialFromArgument(arg: String): ItemSpecial? {
    return try {
        ItemSpecial.valueOf(arg.uppercase(Locale.getDefault()))
    } catch (e: Exception) {
        null
    }
}

enum class ItemSpecial {
    MAINHAND, OFFHAND, NOTHING;

    fun nameLower(): String {
        return name.lowercase(Locale.getDefault())
    }
}