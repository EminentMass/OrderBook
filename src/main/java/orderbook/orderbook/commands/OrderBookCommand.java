package orderbook.orderbook.commands;

import orderbook.orderbook.*;
import orderbook.orderbook.log.OrderLogger;
import orderbook.orderbook.parameters.ItemParameter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.lang.String.format;
import static orderbook.orderbook.parameters.parsers.ParseSender.parseSendItem;
import static orderbook.orderbook.parameters.parsers.ParseSender.parseSendUnsignedInteger;

public class OrderBookCommand extends AbstractOrderBookCommand {
    public OrderBookCommand(OrderManager om) {
        super(om);
    }
    @Override
    public boolean onPlayerCommand(@NotNull CommandSender sender, @NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String [] args) {

        if( args.length < 4 ){
            return false;
        }

        PlayerInventory inventory = player.getInventory();

        ItemParameter sellParam = parseSendItem(args[0], sender);
        if( sellParam == null) { return true; }

        Integer sellCount = parseSendUnsignedInteger(args[1], sender);
        if( sellCount == null ) { return true; }

        ItemParameter buyParam = parseSendItem(args[2], sender);
        if( buyParam  == null) { return true; }

        Integer buyCount = parseSendUnsignedInteger(args[3], sender);
        if( buyCount  == null ) { return true; }


        ItemStack itemSell = sellParam.toItemStack(inventory, sellCount);
        ItemStack itemBuy = buyParam.toItemStack(inventory, buyCount);

        Trade trade = new Trade(itemSell, itemBuy);

        int maxStack = 9*4;
        if(trade.inventoryRequirement() > maxStack) {
            sender.sendMessage(format("Unable to create order, requires %d inventory slots maximum %d", trade.inventoryRequirement(), maxStack));
            return true;
        }

        if(!TradeInventoryManager.hasSellItems(trade, inventory)) {
            sender.sendMessage("Unable to create order you do not have the required sell items");
            return true;
        }

        Order order = orderManager.postOrder(trade);

        TradeInventoryManager.takeSellItems(trade, inventory);

        TradeInventoryManager.addBookSet(order, inventory);

        sender.sendMessage(order.chatPostDisplay());

        OrderLogger.logPost(player.getName(), order.getSellItem(), order.getBuyItem(), order.getId());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

         // /orderbook <sell count> <sell item> <buy count> <buy item>

        if(args.length == 0) {
            return Collections.emptyList();
        } else if (args.length == 1) {
            return tabCompleteItem(args[0]);
        } else if (args.length == 2) {
            return tabCompleteNumber(args[1]);
        } else if (args.length == 3) {
            return tabCompleteItem(args[2]);
        } else if (args.length == 4) {
            return tabCompleteNumber(args[3]);
        } else {
            return Collections.emptyList(); // no optional parameters yet
        }
    }

}
