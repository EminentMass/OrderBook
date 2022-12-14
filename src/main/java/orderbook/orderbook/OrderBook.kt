package orderbook.orderbook

import orderbook.orderbook.commands.*
import orderbook.orderbook.events.OrderBookInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException

class OrderBook : JavaPlugin() {
    override fun onEnable() {

        // Load order manager from previously saved state
        // If this failed we initialize an empty one
        // Later saves will override and orders that were left in the save file
        try {
            orderManager = loadOrderManager(this)
            logger.info("Orders loaded")
        } catch (e: IOException) {
            e.printStackTrace()
            logger.warning("Failed to load order book state. Possible loss of floating orders")
            orderManager = OrderManager(this)
        } catch (e: Exception) {
            logger.warning(e.message)
            orderManager = OrderManager(this)
        }

        // Register commands
        getCommand("orderbook")?.setExecutor(OrderBookCommand(this))
        getCommand("orderbookremove")?.setExecutor(OrderBookRemoveCommand(this))
        getCommand("orderbookmatch")?.setExecutor(OrderBookMatchCommand(this))
        getCommand("orderbookcollect")?.setExecutor(OrderBookCollectCommand(this))
        getCommand("orderbooklist")?.setExecutor(OrderBookListCommand(this))

        // call orderbookcollect when punching with an order book
        server.pluginManager.registerEvents(OrderBookInteractEvent(), this)
        logger.info("Enabled")
    }

    override fun onDisable() {
        // Save the order manager state for later use
        // This will rarely fail
        try {
            orderManager?.save()
            logger.info("Orders saved")
        } catch (e: Exception) {
            e.printStackTrace()
            logger.warning(e.message)
        }
        orderManager = null
        logger.info("Disabled")
    }

    var orderManager: OrderManager? = null
        private set
}