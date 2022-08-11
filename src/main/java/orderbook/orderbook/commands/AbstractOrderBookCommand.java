package orderbook.orderbook.commands;

import orderbook.orderbook.OrderManager;
import orderbook.orderbook.parameters.values.ItemSpecial;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractOrderBookCommand implements TabExecutor {
    protected final OrderManager orderManager;
    protected static final List<String> itemTabCompletions = generateItemTabCompletions();
    protected static final List<String> numberTabCompletions = generateNumberTabCompletions();

    private static List<String> generateItemTabCompletions() {
        Stream<String> mats = Arrays.stream(Material.values()).filter(Material::isItem).map(m -> m.getKey().toString());
        Stream<String> specials = Arrays.stream(ItemSpecial.values()).map(ItemSpecial::nameLower);

        return Stream.concat(mats, specials).collect(Collectors.toList());
    }

    private static List<String> generateNumberTabCompletions() {
        return Stream.iterate(0, n -> n+1)
                .map(String::valueOf)
                .limit(10)
                .collect(Collectors.toList());
    }

    public AbstractOrderBookCommand(OrderManager om) {
        orderManager = om;
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if( !(sender instanceof Player player) ){
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        return onPlayerCommand(sender ,player, command, label, args);
    }

    public abstract boolean onPlayerCommand(@NotNull CommandSender sender, @NotNull Player player, @SuppressWarnings("unused") @NotNull Command command, @SuppressWarnings("unused") @NotNull String label, @NotNull String[] args);

    protected List<String> tabCompleteItem( @NotNull String partial ) {
        return itemTabCompletions.stream()
                .filter(s -> {
                    if( s.startsWith(partial) ) {
                        return true;
                    }
                    String[] arr = s.split(":");

                    if(arr.length == 2) {
                        return arr[1].startsWith(partial);
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

    protected List<String> tabCompleteNumber( @NotNull String partial ) {
        return numberTabCompletions.stream()
                .filter(s -> s.startsWith(partial))
                .collect(Collectors.toList());
    }

    protected List<String> tabCompleteID( @NotNull String partial) {

        return orderManager.getOrders().stream()
                .filter(o -> !o.getStage().isCompleted() )
                .map(o -> o.getId().toString())
                .filter(i -> i.startsWith(partial))
                .collect(Collectors.toList());
    }
}
