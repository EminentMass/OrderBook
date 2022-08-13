package orderbook.orderbook;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static java.lang.String.format;

public class Order {

    private final UUID id;
    private final OrderStage stage;
    private final Trade trade;

    private static final TextColor idColor = NamedTextColor.DARK_PURPLE;
    private static final TextColor itemNameColor = NamedTextColor.GRAY;

    public Order(Trade orderTrade) {
        this(UUID.randomUUID(), orderTrade);
    }

    public Order(UUID uniqueId, Trade orderTrade) {
        this(uniqueId, OrderStage.posted(), orderTrade);
    }

    public Order(UUID uniqueId, OrderStage orderStage, Trade orderTrade) {
        trade = new Trade(orderTrade.getSellItem(), orderTrade.getBuyItem());
        id = uniqueId;
        stage = orderStage;
    }

    public boolean setMatched(UUID id) {
        return stage.setMatched(id);
    }

    public boolean setCompleted() {
        return stage.setCompleted();
    }

    @SuppressWarnings("SpellCheckingInspection")
    public TextComponent chatListDisplay() {
        TextComponent idText = idDisplay();

        // "Order [abcdefgh] buy 10 [Apple] for 20 [Potato]

        // inverted for the party exchanging with the order instead of the creator of the order
        return Component.text("Order ")
                .append(idText)
                .append(Component.text(" buy "))
                .append(displayItem(getSellItem()))
                .append(Component.text(" for "))
                .append(displayItem(getBuyItem()));
    }

    @SuppressWarnings("SpellCheckingInspection")
    public TextComponent chatPostDisplay() {

        // "Posted [abcedfgh] selling 10 [Apple] for 20 [Potato]"

        return Component.text("Posted ")
                .append(idDisplay())
                .append(Component.text(" selling "))
                .append(displayItem(getSellItem()))
                .append(Component.text(" for "))
                .append(displayItem(getBuyItem()));
    }

    public TextComponent idDisplay() {
        return Component.text(format("[%s]", idPrefix()))
                .color(idColor)
                .hoverEvent(HoverEvent.showText(Component.text(id.toString())))
                .clickEvent(ClickEvent.copyToClipboard(id.toString()));
    }

    public String idPrefix() {
        return getId().toString().substring(0, 8);
    }

    public TextComponent displaySellItem() {
        return Order.displayItem(getSellItem());
    }

    public TextComponent displayBuyItem() {
        return Order.displayItem(getBuyItem());
    }

    private static TextComponent displayItem(ItemStack item) {
        return Component.text(item.getAmount())
                .append(Component.text(" "))
                .append(item.displayName().color(itemNameColor));
    }

    public UUID getId() { return id; }
    public OrderStage getStage() { return stage; }
    public Trade getTrade() {
        return trade;
    }

    public ItemStack getSellItem() {
        return trade.getSellItem();
    }
    public ItemStack getBuyItem() {
        return trade.getBuyItem();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Order order = (Order) o;
        return id.equals(order.id) && stage.equals(order.stage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, stage);
    }
}
