package orderbook.orderbook.commands;

import orderbook.orderbook.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OrderBookMatchCommand extends AbstractIdOrderBookCommand {
    public OrderBookMatchCommand(OrderManager om) {
        super(om);
    }

    @Override
    public boolean onPlayerCommand(@NotNull CommandSender sender, @NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        UUID id = parseArgs(args, sender);
        if(id == null) { return true; }

        PlayerInventory inventory = player.getInventory();

        Order order = orderManager.getOrder(id);

        if(order == null) {
            sender.sendMessage("Unable to find order. It may have already been completed");
            return true;
        }

        if(!order.getStage().isPosted()) {
            sender.sendMessage("Unable to match order is already ", order.getStage().getStep().toString());
            return true;
        }

        // trade that will match
        Trade trade = order.getTrade().getInverse();

        // No need to check inventory requirements as they would be the same as the matched order

        if(!TradeInventoryManager.hasSellItems(trade, inventory)) {
            sender.sendMessage("Unable to create order you do not have the required sell items");
            return true;
        }

        Order newMatchOrder = orderManager.postOrder(trade);

        TradeInventoryManager.takeSellItems(trade, inventory);

        TradeInventoryManager.addBookSet(newMatchOrder, inventory);

        sender.sendMessage(newMatchOrder.chatPostDisplay());

        return true;
    }
}