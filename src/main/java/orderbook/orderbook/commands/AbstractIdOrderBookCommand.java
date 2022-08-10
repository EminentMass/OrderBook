package orderbook.orderbook.commands;

import orderbook.orderbook.OrderManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static orderbook.orderbook.parameters.parsers.ParseSender.parseSendUUID;

public abstract class AbstractIdOrderBookCommand extends AbstractOrderBookCommand {

    public AbstractIdOrderBookCommand(OrderManager om) {
        super(om);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            return Collections.emptyList();
        } else if (args.length == 1) {
            return tabCompleteID(args[0]);
        } else {
            return Collections.emptyList();
        }
    }

    protected UUID parseArgs(String[] args, CommandSender sender) {
        if( args.length < 1 ){
            return null;
        }

        return parseSendUUID(args[0], sender);
    }
}
