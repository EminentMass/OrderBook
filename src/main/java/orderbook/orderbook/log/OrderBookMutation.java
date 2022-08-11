package orderbook.orderbook.log;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/*
Post:       time, id, sellItem, buyItem, playerName,
Match:      time, id, sellItem, buyItem, idOther,
Remove:     time, id, sellItem, buyItem, playerName,
Complete:   time, id, sellItem, buyItem, playerName,
*/

@SuppressWarnings("unused")
public class OrderBookMutation implements Serializable {

    public ChangeType getType() {
        return type;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public UUID getId() {
        return id;
    }

    public ItemStack getSellItem() {
        return sellItem;
    }

    public ItemStack getBuyItem() {
        return buyItem;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getIdOther() {
        return idOther;
    }

    public static OrderBookMutation post(UUID uniqueId, ItemStack sell, ItemStack buy, String name) {
        return new OrderBookMutation(
                ChangeType.POST,
                LocalDateTime.now(),
                uniqueId,
                sell,
                buy,
                name,
                null
        );
    }

    public static OrderBookMutation match(UUID uniqueId, ItemStack sell, ItemStack buy, UUID otherId) {
        return new OrderBookMutation(
            ChangeType.MATCH,
            LocalDateTime.now(),
            uniqueId,
            sell,
            buy,
            null,
            otherId
        );
    }

    public static OrderBookMutation remove(UUID uniqueId, ItemStack sell, ItemStack buy, String name) {
        return new OrderBookMutation(
                ChangeType.REMOVE,
                LocalDateTime.now(),
                uniqueId,
                sell,
                buy,
                name,
                null
        );
    }

    public static OrderBookMutation complete(UUID uniqueId, ItemStack sell, ItemStack buy, String name) {
        return new OrderBookMutation(
                ChangeType.COMPLETE,
                LocalDateTime.now(),
                uniqueId,
                sell,
                buy,
                name,
                null
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBookMutation that = (OrderBookMutation) o;
        return type == that.type && Objects.equals(time, that.time) && Objects.equals(id, that.id) && Objects.equals(sellItem, that.sellItem) && Objects.equals(buyItem, that.buyItem) && Objects.equals(playerName, that.playerName) && Objects.equals(idOther, that.idOther);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, time, id, sellItem, buyItem, playerName, idOther);
    }

    @CsvCustomBindByName(converter = ChangeTypeConverter.class)
    private final ChangeType type;
    @CsvBindByName
    @CsvDate
    private final LocalDateTime time;
    @CsvBindByName
    private final UUID id;
    @CsvCustomBindByName(converter = ItemStackConverter.class)
    private final ItemStack sellItem;
    @CsvCustomBindByName(converter = ItemStackConverter.class)
    private final ItemStack buyItem;
    @CsvBindByName
    private final String playerName;
    @CsvBindByName
    private final UUID idOther;
    private OrderBookMutation(@NotNull ChangeType changeType, @NotNull LocalDateTime t, @NotNull UUID mainId, @NotNull ItemStack sell, @NotNull ItemStack buy, @Nullable String name, @Nullable UUID otherId) {
        type = changeType;
        time = t;
        id = mainId;
        sellItem = sell;
        buyItem = buy;
        playerName = name;
        idOther = otherId;

    }




}
