package orderbook.orderbook.parameters.parsers;

import orderbook.orderbook.parameters.ItemParameter;
import org.bukkit.command.CommandSender;

import java.util.UUID;

import static java.lang.String.format;

public class ParseSender {

    public static ItemParameter parseSendItem(String arg, CommandSender sender) {
        ItemParameter param = ItemParameter.matchArgument(arg);

        if(param == null) {
            sender.sendMessage(format("Invalid item type parameter: %s", arg));
        }
        return param;
    }

    @SuppressWarnings("unused")
    public static Boolean parseSendBoolean(String arg, CommandSender sender) {

        if(arg.equalsIgnoreCase("true")) {
            return true;
        } else if( arg.equalsIgnoreCase("false")) {
            return false;
        } else {
            sender.sendMessage("Boolean parameters must be either \"true\" or \"false\"");
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static Integer parseSendInteger(String arg, CommandSender sender) {
        try {
            return Integer.parseInt(arg);
        }catch(NumberFormatException e) {
            sender.sendMessage(format("Integer parameters must be a value between %d %d", Integer.MIN_VALUE, Integer.MAX_VALUE));
            return null;
        }
    }

    public static Integer parseSendUnsignedInteger(String arg, CommandSender sender) {
        try {
            return Integer.parseUnsignedInt(arg);
        }catch(NumberFormatException e) {
            sender.sendMessage(format("Unsigned Integer parameters must be a value between %d %d", 0, Integer.MAX_VALUE));
            return null;
        }
    }

    public static UUID parseSendUUID(String arg, CommandSender sender) {
        try {
            return UUID.fromString(arg);
        }catch(IllegalArgumentException e) {
            sender.sendMessage("UUID parameters must be a properly formatted Universal Unique Identifier");
            return null;
        }
    }
}
