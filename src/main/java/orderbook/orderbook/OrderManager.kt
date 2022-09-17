package orderbook.orderbook

import orderbook.orderbook.log.logMatch
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class Saver(private val plugin: OrderBook, private var lastSaveHash: Int = 0) : BukkitRunnable() {
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

class OrderManager(val plugin: OrderBook, list: List<Order> = emptyList()) {
    private val orders: MutableMap<UUID, Order>
    private val saver: Saver

    init {
        orders = list.associateBy { it.id }.toMutableMap()

        // register saving
        val period = (30 * 60 * 20).toLong() // 30 minutes * 60 seconds/minute * 20 ticks/second
        saver = Saver(plugin)
        saver.runTaskTimer(plugin, period, period)
    }

    fun getOrder(id: UUID): Order? = orders[id]

    fun removeOrder(id: UUID): Boolean = orders.remove(id) != null

    fun getOrders(): MutableCollection<Order> = orders.values

    fun getPostedOrders(): List<Order> = getOrders().filter { (_, stage): Order -> stage.isPosted }

    fun clearCompleted() {
        // completed orders will have no elements in openTrades.
        val iter = orders.iterator()
        iter.forEach { (_, o) -> if (o.stage.isCompleted ) iter.remove() }
    }

    fun postOrder(trade: Trade): Order {
        val order = trade.toNewOrder()
        orders[order.id] = order
        matchOrder(order.id)
        return order
    }

    fun matchOrders() = orders.keys.forEach { orderId -> matchOrder(orderId) }

    @Suppress("BooleanMethodIsAlwaysInverted")
    private fun matchOrder(id1: UUID): Boolean {

        val order1 = orders[id1] ?: return false
        if (!order1.stage.isPosted) {
            return false
        }

        // get list of matching trades
        val matches: List<UUID> = orders
            .filter { (_, order2) -> order2 isMatch order1 && order2.stage.isPosted }
            .map { (id, _) -> id }.toList()

        val id2: UUID = matches.firstOrNull() ?: return false

        // Set both trades to matched, so they aren't open for later matching
        orders[id1]!!.setMatched(id2)
        orders[id2]!!.setMatched(id1)

        // Log change of Order state
        plugin.logger.logMatch(order1.sellItem, order1.buyItem, id1, id2)
        return true
    }

    // The implementations for hashCode and equals only care about the OrderMap
    // The Saver has no important state
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