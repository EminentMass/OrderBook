package orderbook.orderbook.events

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.BookMeta
import java.util.*

class OrderBookInteractEvent : Listener {


    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {

        // None of this is used to verify the authenticity of the book.
        // It is only used to identify which order to attempt to collect
        // The authenticity check is done within the orderbookcollect command
        if (event.action != Action.LEFT_CLICK_BLOCK) {
            return
        }

        val bMeta: BookMeta = event.item?.itemMeta?.let {
            if(it !is BookMeta) return
            it
        } ?: return

        val lore: List<Component> = bMeta.lore()?.let {
            if(it.size != 2) return
            it
        } ?: return

        val hoverValue: TextComponent = lore[1].hoverEvent()?.value()?.let {
            if (it !is TextComponent) return
            it
        } ?: return

        val idString: String  = hoverValue.content()

        val id: UUID

        try {
            id = UUID.fromString(idString)
        }catch(e: IllegalArgumentException ) {
            return
        }

        event.player.performCommand("obc $id")

        event.setUseItemInHand(Event.Result.DENY)
        event.setUseInteractedBlock(Event.Result.DENY)

    }
}