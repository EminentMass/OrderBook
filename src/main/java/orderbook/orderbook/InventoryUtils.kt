package orderbook.orderbook

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.BookMeta
import java.util.*
import kotlin.math.ceil

fun ItemStack.inventoryRequirement(): Int {
    if (isNothing()) {
        return 0
    }
    val stackSize = type.maxStackSize
    val count = amount
    return ceil((count.toFloat() / stackSize.toFloat()).toDouble()).toInt()
}

fun ItemStack.isNothing() = type == Material.AIR || amount == 0

@Suppress("BooleanMethodIsAlwaysInverted")
infix fun PlayerInventory.hasSellItems(trade: Trade) = this countOf trade.sellItem >= trade.sellItem.amount

infix fun PlayerInventory.addOrDrop(item: ItemStack) {
    val leftover = addItem(item)
    if (leftover.size > 0) {
        location?.world?.let {
            location!!.world.dropItemNaturally(location!!, item)
        }
    }
}

infix fun PlayerInventory.giveSellItems(order: Order) = this giveItems order.sellItem
infix fun PlayerInventory.giveBuyItems(order: Order) = this giveItems order.buyItem

private infix fun PlayerInventory.giveItems(item: ItemStack) {
    if (item.type == Material.AIR || item.amount == 0) {
        return
    }

    val stackSize = item.maxStackSize
    val count = item.amount
    val stacks = count / stackSize
    val leftover = count % stackSize
    for (i in 0 until stacks) {
        this addOrDrop item.asQuantity(stackSize)
    }
    this addOrDrop item.asQuantity(leftover)
}

private infix fun PlayerInventory.countOf(item: ItemStack): Int {
    val available = intArrayOf(0)
    Arrays.stream(contents).filter { i: ItemStack? ->

        i?.let { i.asOne() == item.asOne() } ?: false

    }.forEach { i: ItemStack? -> available[0] += i!!.amount }
    return available[0]
}

infix fun PlayerInventory.addBookSet(order: Order) {
    val books = generateBookSet(order)
    for (book in books) {
        this addOrDrop book
    }
}

private fun generateBookSet(order: Order): List<ItemStack> {
    val books: MutableList<ItemStack> = ArrayList()
    val bookCount = order.trade.inventoryRequirement()
    for (i in 1..bookCount) {
        val bookBase = ItemStack(Material.WRITTEN_BOOK)
        val bookMeta = bookBase.itemMeta as BookMeta
        val author = Component.text("Order Book Trading")
        val title = Component.text("Trade ")
            .append(order.idDisplay())
            .append(Component.text(String.format(" %d/%d", i, bookCount)))
        val page = Component.text("Rights for trade ")
            .append(order.idDisplay())
            .append(Component.text(" selling "))
            .append(order.displaySellItem())
            .append(Component.text(" for "))
            .append(order.displayBuyItem())
            .append(Component.text(String.format(" part %d/%d", i, bookCount)))
        val bookMetaFinal = bookMeta.toBuilder().author(author).title(title).addPage(page).build()
        bookMetaFinal.generation = BookMeta.Generation.ORIGINAL
        bookBase.itemMeta = bookMetaFinal

        // The Stamp and the Generation are used to verify the authenticity of an order book
        // The stamp for a given order is the same for each book for that order
        // It is only required that you have the necessary count of books with the stamp
        bookBase.lore(bookStamp(order))
        books.add(bookBase)
    }
    return books
}

private fun bookStamp(order: Order): List<Component> {
    return listOf<Component>(bookStampHeader(), order.idDisplay())
}

private fun bookStampHeader(): TextComponent {
    return Component.text("Order Book Stamp").decorate(TextDecoration.UNDERLINED)
}

@Suppress("BooleanMethodIsAlwaysInverted")
infix fun PlayerInventory.hasBookSet(order: Order): Boolean {
    val stamp = bookStamp(order)
    return Arrays.stream(contents).filter { i: ItemStack? ->
        i?.stampedWith(stamp) == true
    }.count() >= order.trade.inventoryRequirement().toLong()
}

@Suppress("BooleanMethodIsAlwaysInverted")
private infix fun ItemStack.stampedWith(stamp: List<Component>): Boolean {
    if (type != Material.WRITTEN_BOOK) {
        return false
    }
    val meta = itemMeta as BookMeta
    if (!meta.hasGeneration()) {
        return false
    }
    if (meta.generation != BookMeta.Generation.ORIGINAL) {
        return false
    }
    val lore = this.lore() ?: return false
    return if (lore.size != 2) {
        false
    } else lore == stamp
}

infix fun PlayerInventory.takeBookSet(order: Order) {
    val stamp = bookStamp(order)
    for(i in contents) {
        if (i?.stampedWith(stamp) == true) {
            removeItemAnySlot(i)
        }
    }
}

infix fun PlayerInventory.takeSellItems(trade: Trade) = this.removeItemAnySlot(trade.sellItem)

