package orderbook.orderbook;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class OrderStage {

    private UUID match;
    private OrderStep step;

    // OrderStage is meant to have a life cycle where its step goes through
    // POSTED -> MATCHED -> COMPLETED
    // It is possible to skip steps by using the constructors.
    // They are only public for use in loading OrderStage from a file or maybe a network connection.
    // When creating a new OrderStage use posted, setMatched, then setCompleted to go through its life cycle
    public OrderStage(OrderStep step) {
        this(null, step);
    }
    public OrderStage(UUID matchId, OrderStep orderStep) {
        match = matchId;
        step = orderStep;
    }

    public static OrderStage posted() {
        return new OrderStage(OrderStep.POSTED);
    }

    public boolean setMatched(UUID id) {
        if(step != OrderStep.POSTED) {
            return false;
        }else{
            match = id;
            step = OrderStep.MATCHED;
            return true;
        }
    }

    public boolean setCompleted(){
        if(step != OrderStep.MATCHED) {
            return false;
        }else{
            step = OrderStep.COMPLETED;
            return true;
        }
    }

    public OrderStep getStep() {
        return step;
    }

    public Optional<UUID> getMatch() {
        if(match == null) {
            return Optional.empty();
        }else{
            return Optional.of(match);
        }
    }

    public boolean isPosted() {
        return step == OrderStep.POSTED;
    }

    @SuppressWarnings("unused")
    public boolean isMatched() {
        return step == OrderStep.MATCHED;
    }

    public boolean isCompleted() {
        return step == OrderStep.COMPLETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderStage that = (OrderStage) o;
        return Objects.equals(match, that.match) && step == that.step;
    }

    @Override
    public int hashCode() {
        return Objects.hash(match, step);
    }
}

