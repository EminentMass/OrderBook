package orderbook.orderbook

import orderbook.orderbook.log.logMatch
import orderbook.orderbook.log.warnInvalidState
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.HashMap

private class OrderLookupTable(private var table: MutableMap<Trade, MutableList<UUID>>) {
    constructor(list: List<Order>) : this(
        list.filter { it.stage.isPosted }
        .groupByTo( HashMap(), { o -> o.trade }, { o -> o.id })
    )

    fun remove(key: Trade, value: UUID) {
        // remove element value in list at key
        // once it is removed if the list is empty remove it as well.
        table[key]?.remove(value)

        if( table[key]?.isEmpty() == true ){
            table.remove(key)
        }
    }

    fun put(key: Trade, value: UUID) {
        // if a list already exists at key, add value to it otherwise
        // create a new list with element value and add it at key.
        table[key]?.add(value) ?: run {
            table[key] = mutableListOf(value)
        }
    }

    fun get(key: Trade): MutableList<UUID>? = table[key]

    fun getInverse(key: Trade): MutableList<UUID>? = get(key.inverse)
}

class Saver(private var plugin: OrderBook, private var lastSaveHash: Int = 0) : BukkitRunnable() {
    override fun run() {

        // only save if the state has changed
        val manager = plugin.orderManager ?: return
        if (manager.hashCode() == lastSaveHash) return

        try {
            manager.save()
            lastSaveHash = manager.hashCode()
        } catch (e: Exception) {
            val logger = plugin.logger
            e.printStackTrace()
            logger.warning(e.message)
            logger.warning("Failed to save order book state.")
        }
    }
}

class OrderManager(plugin: OrderBook, list: List<Order> = emptyList()) {
    private val orders: MutableMap<UUID, Order>
    private val openTrades: OrderLookupTable
    private val saver: Saver

    init {
        orders = list.associateBy { it.id }.toMutableMap()
        openTrades = OrderLookupTable(list)

        // register saving
        val period = (30 * 60 * 20).toLong() // 30 minutes * 60 seconds/minute * 20 ticks/second
        saver = Saver(plugin)
        saver.runTaskTimer(plugin, period, period)
    }

    fun getOrder(id: UUID): Order? = orders[id]

    fun removeOrder(id: UUID): Boolean {
        val (_, _, trade) = orders.remove(id) ?: return false
        openTrades.remove(trade, id)
        return true
    }

    fun getOrders(): MutableCollection<Order> = orders.values

    fun getActiveOrders(): List<Order> = getOrders().filter { (_, stage): Order -> stage.isPosted }

    fun clearCompleted() {
        // completed orders will have no elements in openTrades.
        val iter = orders.iterator()
        iter.forEach { (_, o) -> if (o.stage.isCompleted ) iter.remove() }
    }

    fun postOrder(trade: Trade): Order {
        val order = trade.toNewOrder()
        orders[order.id] = order
        openTrades.put(trade, order.id)
        matchOrder(order.id)
        return order
    }

    fun matchOrders() = orders.keys.forEach { orderId -> matchOrder(orderId) }

    @Suppress("BooleanMethodIsAlwaysInverted")
    private fun matchOrder(orderId: UUID): Boolean {
        val order1 = orders[orderId]
        if (!order1!!.stage.isPosted) {
            return false
        }

        // get list of opposite trades
        val matches: List<UUID> = openTrades.getInverse(order1.trade)?.toList() ?: return false

        val id1: UUID = order1.id
        val id2: UUID = matches.firstOrNull() ?: return false
        val order2 = orders[id2]
        if (!order2!!.stage.isPosted) {
            openTrades.remove(order2.trade, id2)
            warnInvalidState(String.format("Order %s was in openTrades when not posted", id2))
            return false
        }

        // Set both trades to matched and remove from open trades, so they aren't open for later matching
        orders[id1]!!.setMatched(id2)
        orders[id2]!!.setMatched(id1)

        openTrades.remove(orders[id1]!!.trade, id1)
        openTrades.remove(orders[id2]!!.trade, id2)

        // Log change of Order state
        logMatch(order1.sellItem, order1.buyItem, id1, id2)
        return true
    }

    // The implementations for hashCode and equals only care about the OrderMap
    // The lookup table is only used for fast lookups
    override fun hashCode(): Int = orders.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (javaClass != other.javaClass) {
            return false
        }
        val manager = other as OrderManager
        return orders == manager.orders
    }
}