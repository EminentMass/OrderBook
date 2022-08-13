package orderbook.orderbook.commands;

import orderbook.orderbook.Order;
import orderbook.orderbook.OrderBook;
import orderbook.orderbook.OrderManager;
import orderbook.orderbook.TradeInventoryManager;
import orderbook.orderbook.log.OrderLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OrderBookRemoveCommand extends AbstractIdOrderBookCommand {
    public OrderBookRemoveCommand(OrderManager om) {
        super(om);
    }

    @Override
    public boolean onPlayerCommand(@NotNull CommandSender sender, @NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        UUID id = parseArgs(args, sender);
        if(id == null) { return true; }

        PlayerInventory inventory = player.getInventory();

        Order order = orderManager.getOrder(id);

        if(order == null) {
            sender.sendMessage("Unable to find order %s", id.toString());
            return true;
        }

        if(!order.getStage().isPosted()) {
            sender.sendMessage("Unable to remove order. Order is no longer posted");
            return true;
        }

        if(!TradeInventoryManager.hasBookSet(order, inventory)){
            sender.sendMessage("You do not have the required trade books");
            return true;
        }

        TradeInventoryManager.takeBookSet(order, inventory);

        TradeInventoryManager.giveSellItems(order, inventory);

        if( !orderManager.removeOrder(id) ){
            assert OrderBook.getInstance() != null;
            OrderBook.getInstance().getLogger().warning("Somehow failed to remove order after checking it was there");
        }

        OrderLogger.logRemove(player.getName(), order.getSellItem(), order.getBuyItem(), order.getId());

        return true;
    }
}
