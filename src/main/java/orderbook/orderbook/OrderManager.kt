package orderbook.orderbook

import orderbook.orderbook.log.logMatch
import orderbook.orderbook.log.warnInvalidState
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.HashMap

// Multimap utilities
private fun<K, V> MutableMap<K, MutableList<V>>.removeSingle(key: K, value: V) {

    // remove element value in list at key
    // once it is removed if the list is empty remove it as well.
    this[key]?.remove(value)

    if( this[key]?.isEmpty() == true ){
        this.remove(key)
    }
}
private fun<K, V> MutableMap<K, MutableList<V>>.putSingle(key: K, value: V) {
    // if a list already exists at key, add value to it otherwise
    // create a new list with element value and add it at key.
    this[key]?.add(value) ?: run {
        this[key] = mutableListOf(value)
    }
}

class Saver(private var lastSaveHash: Int = 0) : BukkitRunnable() {
    override fun run() {

        // only save if the state has changed
        val instance = OrderBook.instance ?: return
        val manager = instance.orderManager ?: return
        if (manager.hashCode() == lastSaveHash) return

        try {
            manager.save()
            lastSaveHash = manager.hashCode()
        } catch (e: Exception) {
            e.printStackTrace()
            val logger = instance.logger
            logger.warning(e.message)
            logger.warning("Failed to save order book state. Save attempt scheduled in 10 seconds")

            // Possibly not blocked in 10 seconds
            this.runTaskLater(instance, (10 * 20).toLong())
        }
    }
}

class OrderManager(list: List<Order> = emptyList()) {
    private val orders: MutableMap<UUID, Order>
    private val openTrades: HashMap<Trade, MutableList<UUID>>

    init {
        orders = list.associateBy { it.id }.toMutableMap()
        openTrades = list.filter { it.stage.isPosted }
            .groupByTo( HashMap(), { o -> o.trade }, { o -> o.id })

        // register saving
        val period = (30 * 60 * 20).toLong() // 30 minutes * 60 seconds/minute * 20 ticks/second
        Saver().runTaskTimer(OrderBook.instance!!, period, period)
    }

    fun getOrder(id: UUID): Order? = orders[id]

    fun removeOrder(id: UUID): Boolean {
        val (_, _, trade) = orders.remove(id) ?: return false
        openTrades[trade]?.remove(id)
        return true
    }

    /*
    val orderMap: MutableMap<UUID, Order>
        get() = orders
     */

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
        openTrades.putSingle(trade, order.id)
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
        val matches: List<UUID> = openTrades[order1.trade.inverse]?.toList() ?: emptyList()
        if (matches.isEmpty()) return false

        val id1: UUID = order1.id
        val id2: UUID = matches.first()
        val order2 = orders[id2]
        if (!order2!!.stage.isPosted) {
            openTrades.removeSingle(order2.trade, id2)
            warnInvalidState(String.format("Order %s was in openTrades when not posted", id2))
            return false
        }


        // both orders should only be pointed to by openTrades if they are posted and yet to be matched or completed.
        orders[id1]!!.setMatched(id2)
        orders[id2]!!.setMatched(id1)

        // remove trades from open trades so no one tries to match with these two orders.
        openTrades.removeSingle(orders[id1]!!.trade, id1)
        openTrades.removeSingle(orders[id2]!!.trade, id2)

        // Log change of Order state
        logMatch(order1.sellItem, order1.buyItem, id1, id2)
        return true
    }

    // The implementations for hashCode and equals only care about the OrderMap
    // The Trade multimap is only used for fast order matching
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