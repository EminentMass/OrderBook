package orderbook.orderbook;

import orderbook.orderbook.commands.*;
import orderbook.orderbook.events.OrderBookInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

public final class OrderBook extends JavaPlugin {

    private static OrderBook instance;
    private static OrderManager orderManager;

    @Override
    public void onEnable() {
        instance = this;

        // Load order manager from previously saved state
        // If this failed we initialize an empty one
        // Later saves will override and orders that were left in the save file
        try {
            orderManager = OrderBookLoader.load();
            getLogger().info("Orders loaded");
        }catch(IOException e) {
            e.printStackTrace();
            getLogger().warning("Failed to load order book state. Possible loss of floating orders");
            orderManager = new OrderManager();
        }catch(Exception e) {
            getLogger().warning(e.getMessage());
            orderManager = new OrderManager();
        }

        // Register commands
        Objects.requireNonNull(getCommand("orderbook")).setExecutor(new OrderBookCommand(this.getOrderManager()));
        Objects.requireNonNull(getCommand("orderbookremove")).setExecutor(new OrderBookRemoveCommand(this.getOrderManager()));
        Objects.requireNonNull(getCommand("orderbookmatch")).setExecutor(new OrderBookMatchCommand(this.getOrderManager()));
        Objects.requireNonNull(getCommand("orderbookcollect")).setExecutor(new OrderBookCollectCommand(this.getOrderManager()));
        Objects.requireNonNull(getCommand("orderbooklist")).setExecutor(new OrderBookListCommand(this.getOrderManager()));

        // call orderbookcollect when punching with an order book
        getServer().getPluginManager().registerEvents(new OrderBookInteractEvent(), this);

        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        // Save the order manager state for later use
        // This will rarely fail
        try {
            OrderBookLoader.save(orderManager);
            getLogger().info("Orders saved");
        }catch(Exception e) {
            e.printStackTrace();
            getLogger().warning(e.getMessage());
        }

        orderManager = null;
        instance = null;

        getLogger().info("Disabled");
    }

    public static OrderBook getInstance() {
        return instance;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

}
