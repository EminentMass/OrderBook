package orderbook.orderbook;

import com.google.common.collect.*;
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

        // cast to trade
        openTrades.remove(order, id);

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

        // No need to add trade to open trades if the order is immediately matched.
        if(matchOrder(order.getId())) {
            return order;
        }

        openTrades.put(trade, order.getId());

        return order;
    }

    public void matchOrders() {
        orders.keySet().forEach(this::matchOrder);
    }

    private boolean matchOrder(UUID orderId) {
        Order order = orders.get(orderId);

        if(!order.getStage().isPosted()){
            return false;
        }

        // get list of opposite trades
        List<UUID> matches = openTrades.get(order.getInverseTrade());

        if(matches.size() == 0) {
            return false;
        }

        UUID id1 = order.getId();
        UUID id2 = matches.iterator().next();

        // both orders should only be pointed to by openTrades if they are posted and yet to be matched or completed.
        boolean success     = orders.get(id1).setMatched(id2);
        success            &= orders.get(id2).setMatched(id1);

        if(!success) {
            assert OrderBook.getInstance() != null;
            OrderBook.getInstance().getLogger().warning(format(
                    "Failed matching orders %s %s possible half match current states are %s %s",
                    id1, id2,
                    orders.get(id1).getStage().getStep().toString(),
                    orders.get(id2).getStage().getStep().toString()
            ));
        }

        // remove trades from open trades so no one tries to match with these two orders.
        openTrades.remove(orders.get(id1), id1);

        openTrades.remove(orders.get(id1), id2);

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
