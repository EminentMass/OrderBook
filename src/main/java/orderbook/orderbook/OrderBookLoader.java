package orderbook.orderbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import orderbook.orderbook.adapters.ItemStackAdapter;
import orderbook.orderbook.adapters.OrderAdapter;
import orderbook.orderbook.adapters.OrderStageAdapter;
import orderbook.orderbook.adapters.TradeAdapter;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class OrderBookLoader {

    public static void save(OrderManager manager) throws Exception {

        // clean up manager for saving
        manager.matchOrders();
        manager.clearCompleted();

        ArrayList<Order> orders = new ArrayList<>(manager.getOrders());

        Gson gson = gson();
        File file = saveFile();
        boolean dirExists = file.getParentFile().exists() || file.getParentFile().mkdir();
        if(!dirExists) {
            throw new Exception("Failed to save orders non existent save file parent directory");
        }

        boolean fileExists = file.exists() || file.createNewFile();
        if(!fileExists){
            throw new Exception("Failed to save orders unable to create save file");
        }
        Writer writer = new FileWriter(file, false);

        Type tt = new TypeToken<ArrayList<Order>>() {}.getType();

        gson.toJson(orders, tt, writer);

        writer.flush();
        writer.close();
    }

    public static OrderManager load() throws Exception {
        Gson gson = gson();
        File file = saveFile();

        if(!file.exists()) {
            throw new Exception("Failed to load order book from file possible loss of floating orders or is this the first execution.");
        }

        Reader reader = new FileReader(file);
        Order[] ordersArray = gson.fromJson(reader, Order[].class);

        if(ordersArray == null) {
            throw new Exception("Failed to load order book null from reader");
        }

        return new OrderManager(new ArrayList<>(Arrays.asList(ordersArray)));
    }

    private static File saveFile() {
        assert OrderBook.getInstance() != null;
        return new File(OrderBook.getInstance().getDataFolder().getAbsolutePath() + "/order.json");
    }

    private static Gson gson() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(OrderStage.class, new OrderStageAdapter());
        gson.registerTypeAdapter(Trade.class, new TradeAdapter());
        gson.registerTypeAdapter(ItemStack.class, new ItemStackAdapter());
        gson.registerTypeAdapter(Order.class, new OrderAdapter());

        return gson.create();
    }
}
