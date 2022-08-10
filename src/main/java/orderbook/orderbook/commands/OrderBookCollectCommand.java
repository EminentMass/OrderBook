package orderbook.orderbook.commands;

import orderbook.orderbook.Order;
import orderbook.orderbook.OrderBook;
import orderbook.orderbook.OrderManager;
import orderbook.orderbook.TradeInventoryManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OrderBookCollectCommand extends AbstractIdOrderBookCommand {
    public OrderBookCollectCommand(OrderManager om) {
        super(om);
    }

    @Override
    public boolean onPlayerCommand(@NotNull CommandSender sender, @NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        UUID id = parseArgs(args, sender);
        if(id == null) { return true; }

        PlayerInventory inventory = player.getInventory();

        Order order = orderManager.getOrder(id);

        if(order == null) {
            sender.sendMessage("Unable to find order ", id.toString());
            return true;
        }

        if(order.getStage().isPosted()) {
            sender.sendMessage("Unable to collect order. Order is currently posted and has not made it's trade");
            return true;
        }

        if(order.getStage().isCompleted()) {
            sender.sendMessage("Unable to collect order. Order is already completed");
            return true;
        }

        if(!TradeInventoryManager.hasBookSet(order, inventory)){
            sender.sendMessage("You do not have the required trade books");
            return true;
        }

        TradeInventoryManager.takeBookSet(order, inventory);

        TradeInventoryManager.giveBuyItems(order, inventory);

        if( !order.setCompleted() ){
            assert OrderBook.getInstance() != null;
            OrderBook.getInstance().getLogger().warning("Somehow failed to set order to completed even after checking the stage");
        }

        return true;
    }
}
