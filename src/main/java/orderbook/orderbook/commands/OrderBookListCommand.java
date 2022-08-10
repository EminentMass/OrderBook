package orderbook.orderbook.commands;

import net.kyori.adventure.text.Component;
import orderbook.orderbook.Order;
import orderbook.orderbook.OrderManager;
import orderbook.orderbook.parameters.ItemParameter;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static orderbook.orderbook.parameters.parsers.ParseSender.*;

public class OrderBookListCommand extends AbstractOrderBookCommand {
    public OrderBookListCommand(OrderManager om) {
        super(om);
    }

    @Override
    public boolean onPlayerCommand(@NotNull CommandSender sender, @NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        PlayerInventory inventory = player.getInventory();

        List<Component> entries;

        if( args.length == 0 ){
            entries = searchOrders();
        } else if (args.length == 1) {

            ItemParameter param = parseSendItem(args[0], sender);
            if(param == null) { return true; }

            entries = searchOrders(param.toItemStack(inventory, 1));

        } else if (args.length == 2) {
            ItemParameter param = parseSendItem(args[0], sender);
            if(param == null) { return true; }
            Integer count = parseSendUnsignedInteger(args[1], sender);
            if(count == null) { return true; }

            entries = searchOrders(param.toItemStack(inventory, count));

        } else {
            return false;
        }

        String bar = "-------------------------";

        Component header = Component.text(bar + "\n")
                .append(Component.text("Order List\n"))
                .append(Component.text(bar));

        sender.sendMessage(header);

        for(Component comp : entries) {
            sender.sendMessage(comp);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            return Collections.emptyList();
        } else if (args.length == 1) {
            return tabCompleteItem(args[0]);
        } else if (args.length == 2) {
            return tabCompleteNumber(args[1]);
        } else {
            return Collections.emptyList();
        }
    }

    private List<Component> searchOrders() {
        return orderManager.getActiveOrders().map(Order::chatListDisplay).collect(Collectors.toList());
    }

    private List<Component> searchOrders(ItemStack item) {

        Material mat = item.getType();
        int count = item.getAmount();

        return orderManager.getActiveOrders()
                .filter(o -> o.getSellItem().getType() == mat && o.getSellItem().getAmount() >= count)
                .map(Order::chatListDisplay)
                .collect(Collectors.toList());
    }
}
