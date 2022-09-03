package orderbook.orderbook

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import orderbook.orderbook.adapters.ItemStackAdapter
import orderbook.orderbook.adapters.OrderAdapter
import orderbook.orderbook.adapters.OrderStageAdapter
import orderbook.orderbook.adapters.TradeAdapter
import org.bukkit.inventory.ItemStack
import java.io.*
import java.util.*

@Throws(Exception::class)
fun OrderManager.save() {

    // clean up manager for saving
    matchOrders()
    clearCompleted()

    // set up writers and handle if file isn't there
    val gson = gson()
    val file = saveFile(plugin)

    val dirExists = file.parentFile.exists() || file.parentFile.mkdir()
    if (!dirExists) {
        throw Exception("Failed to save orders non existent save file parent directory")
    }
    val fileExists = file.exists() || file.createNewFile()
    if (!fileExists) {
        throw Exception("Failed to save orders unable to create save file")
    }
    val writer: Writer = FileWriter(file, false)

    // Type is an array of Order
    val tt = object : TypeToken<Array<Order?>?>() {}.type
    gson.toJson(ArrayList(getOrders()), tt, writer)
    writer.flush()
    writer.close()
}

@Throws(Exception::class)
fun loadOrderManager(plugin: OrderBook): OrderManager {
    val gson = gson()
    val file = saveFile(plugin)
    if (!file.exists()) {
        throw Exception("Failed to load order book from file possible loss of floating orders or is this the first execution.")
    }
    val reader: Reader = FileReader(file)
    val ordersArray = gson.fromJson(reader, Array<Order>::class.java)
        ?: throw Exception("Failed to load order book null from reader")
    return OrderManager(plugin, ordersArray.toList())
}

private fun saveFile(plugin: OrderBook): File {
    return File(plugin.dataFolder.absolutePath + "/order.json")
}

private fun gson(): Gson {
    val gson = GsonBuilder()
    gson.registerTypeAdapter(OrderStage::class.java, OrderStageAdapter())
    gson.registerTypeAdapter(Trade::class.java, TradeAdapter())
    gson.registerTypeAdapter(ItemStack::class.java, ItemStackAdapter())
    gson.registerTypeAdapter(Order::class.java, OrderAdapter())
    return gson.create()
}