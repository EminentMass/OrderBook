package orderbook.orderbook;

import com.google.common.collect.*;
import orderbook.orderbook.log.OrderLogger;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public class OrderManager {

    private final Map<UUID, Order> orders;
    private final ListMultimap<Trade, UUID> openTrades;

    public OrderManager(List<Order> list) {
        orders = list.stream().collect(Collectors.toMap(Order::getId, Function.identity()));

        openTrades = list.stream()
                .filter(o -> o.getStage().isPosted())
                .collect(Multimaps.toMultimap(Trade::new, Order::getId, MultimapBuilder.hashKeys().linkedListValues()::build));

        // register saving
        // Could you hashes of the order state to determine if saving is actually needed.
        // There could have been no changes since the last save.
        BukkitRunnable save = new BukkitRunnable() {

            // If we somehow get a orderManager state that hashes to zero, it will be resolved on next change when the hash is no longer equal to zero.
            int lastSaveHash = 0;

            @Override
            public void run() {

                assert OrderBook.getInstance() != null;
                OrderManager manager = OrderBook.getInstance().getOrderManager();

                assert manager != null;
                if(manager.hashCode() == lastSaveHash) {
                    return;
                }

                try {
                    OrderBookLoader.save(manager);
                    lastSaveHash = manager.hashCode();
                }catch(Exception e) {
                    e.printStackTrace();
                    Logger logger = OrderBook.getInstance().getLogger();
                    logger.warning(e.getMessage());
                    logger.warning("Failed to save order book state. Save attempt scheduled in 10 seconds");

                    // Possibly not blocked in 10 seconds
                    this.runTaskLater(OrderBook.getInstance(), 10 * 20);
                }
            }
        };

        long period = 30 * 60 * 20; // 30 minutes * 60 seconds/minute * 20 ticks/second
        assert OrderBook.getInstance() != null;
        save.runTaskTimer(OrderBook.getInstance(), period, period);
    }

    public OrderManager() {
        openTrades = ArrayListMultimap.create();
        orders = new HashMap<>();
    }

    public Order getOrder(UUID id) {
        return orders.get(id);
    }

    public boolean removeOrder(UUID id) {
        Order order = orders.remove(id);

        if(order == null) {
            return false;
        }

        openTrades.remove(order.getTrade(), id);

        return true;
    }

    public Map<UUID, Order> getOrderMap() {
        return orders;
    }

    public Collection<Order> getOrders() {
        return orders.values();
    }

    public Stream<Order> getActiveOrders() {
        return getOrders().stream().filter(o -> o.getStage().isPosted());
    }

    @SuppressWarnings("unused")
    public Set<UUID> getIds() {
        return orders.keySet();
    }

    public void clearCompleted() {

        // completed orders will have no elements in openTrades.
        List<UUID> toRemove = new ArrayList<>();
        orders.forEach((id, order) -> {
            if(order.getStage().isCompleted()){
                toRemove.add(id);
            }
        });

        for(UUID id : toRemove) {
            orders.remove(id);
        }
    }

    public Order postOrder(Trade trade) {
        Order order = new Order(trade);

        orders.put(order.getId(), order);
        openTrades.put(trade, order.getId());

        matchOrder(order.getId());

        return order;
    }

    public void matchOrders() {
        orders.keySet().forEach(this::matchOrder);
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean matchOrder(UUID orderId) {
        Order order1 = orders.get(orderId);

        if(!order1.getStage().isPosted()){
            return false;
        }

        // get list of opposite trades
        List<UUID> matches = openTrades.get(order1.getTrade().getInverse());

        if(matches.size() == 0) {
            return false;
        }

        UUID id1 = order1.getId();
        UUID id2 = matches.iterator().next();

        Order order2 = orders.get(id2);

        if(!order2.getStage().isPosted()){
            openTrades.remove(order2.getTrade(), id2);
            OrderLogger.warnInvalidState(format("Order %s was in openTrades when not posted", id2));
            return false;
        }


        // both orders should only be pointed to by openTrades if they are posted and yet to be matched or completed.
        orders.get(id1).setMatched(id2);
        orders.get(id2).setMatched(id1);

        // remove trades from open trades so no one tries to match with these two orders.
        openTrades.remove(orders.get(id1).getTrade(), id1);
        openTrades.remove(orders.get(id2).getTrade(), id2);

        // Log change of Order state
        OrderLogger.logMatch(order1.getSellItem(), order1.getBuyItem(), id1, id2);

        return true;
    }


    // The implementations for hashCode and equals only care about the OrderMap
    // The Trade multimap is only used for fast order matching
    @Override
    public int hashCode() {
        return orders.hashCode();
    }

    @Override
    public boolean equals(Object rhs) {

        if(this == rhs){
            return true;
        }

        if(rhs == null) {
            return false;
        }

        if(getClass() != rhs.getClass()) {
            return false;
        }

        OrderManager manager = (OrderManager) rhs;

        return orders.equals(manager.getOrderMap());
    }
}
