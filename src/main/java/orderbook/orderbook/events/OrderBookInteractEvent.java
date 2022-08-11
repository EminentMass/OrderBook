package orderbook.orderbook.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class OrderBookInteractEvent implements Listener {

    public OrderBookInteractEvent() {

    }

    @EventHandler
    public static void onPlayerInteractEvent(PlayerInteractEvent event) {

        // None of this is used to verify the authenticity of the book.
        // It is only used to identify which order to attempt to collect
        // The authenticity check is done within the orderbookcollect command
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (!(meta instanceof BookMeta bMeta)) {
            return;
        }

        List<Component> lore = bMeta.lore();

        if (lore == null || lore.size() != 2) {
            return;
        }

        HoverEvent<?> hv = lore.get(1).hoverEvent();

        if(hv == null) {
            return;
        }

        Object v = hv.value();

        if(!(v instanceof TextComponent hoverValue)) {
            return;
        }

        String idString = hoverValue.content();

        UUID id;

        try {
            id = UUID.fromString(idString);
        }catch(IllegalArgumentException e) {
            return;
        }

        event.getPlayer().performCommand("obc " + id);

        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);

    }
}
