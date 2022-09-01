package orderbook.orderbook

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.inventory.ItemStack
import java.util.*

private val idColor: TextColor = NamedTextColor.DARK_PURPLE
private val itemNameColor: TextColor = NamedTextColor.GRAY
private fun ItemStack.displayItem() =
    Component.text(amount).append(Component.text(" ")).append(displayName().color(itemNameColor))

@Suppress("BooleanMethodIsAlwaysInverted")
data class Order(val id: UUID = UUID.randomUUID(), val stage: OrderStage = OrderStage(), val trade: Trade) {
    fun setMatched(id: UUID?): Boolean {
        return stage.setMatched(id)
    }

    fun setCompleted(): Boolean {
        return stage.setCompleted()
    }

    fun chatListDisplay(): TextComponent {
        val idText = idDisplay()

        // "Order [abcdefgh] buy 10 [Apple] for 20 [Potato]

        // inverted for the party exchanging with the order instead of the creator of the order
        return Component.text("Order ")
            .append(idText)
            .append(Component.text(" buy "))
            .append(displaySellItem())
            .append(Component.text(" for "))
            .append(displayBuyItem())
    }

    fun chatPostDisplay(): TextComponent {

        // "Posted [abcedfgh] selling 10 [Apple] for 20 [Potato]"
        return Component.text("Posted ")
            .append(idDisplay())
            .append(Component.text(" selling "))
            .append(displaySellItem())
            .append(Component.text(" for "))
            .append(displayBuyItem())
    }

    fun idDisplay(): TextComponent {
        return Component.text(String.format("[%s]", idPrefix()))
            .color(idColor)
            .hoverEvent(HoverEvent.showText(Component.text(id.toString())))
            .clickEvent(ClickEvent.copyToClipboard(id.toString()))
    }

    private fun idPrefix(): String {
        return id.toString().substring(0, 8)
    }

    fun displaySellItem() = sellItem.displayItem()

    fun displayBuyItem() = buyItem.displayItem()

    val sellItem: ItemStack
        get() = trade.sellItem
    val buyItem: ItemStack
        get() = trade.buyItem
}