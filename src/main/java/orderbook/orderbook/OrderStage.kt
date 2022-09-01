package orderbook.orderbook

import java.util.*

data class OrderStage(var match: UUID? = null, var step: OrderStep = OrderStep.POSTED) {

    // OrderStage is meant to have a life cycle where its step goes through
    // POSTED -> MATCHED -> COMPLETED
    // It is possible to skip steps by using the constructors.
    // They are only public for use in loading OrderStage from a file or maybe a network connection.
    // When creating a new OrderStage use the default constructor, setMatched, then setCompleted to go through its life cycle

    fun setMatched(id: UUID?): Boolean {
        return if (step !== OrderStep.POSTED) {
            false
        } else {
            match = id
            step = OrderStep.MATCHED
            true
        }
    }

    fun setCompleted(): Boolean {
        return if (step !== OrderStep.MATCHED) {
            false
        } else {
            step = OrderStep.COMPLETED
            true
        }
    }

    @Suppress("BooleanMethodIsAlwaysInverted")
    val isPosted: Boolean
        get() = step === OrderStep.POSTED
    val isMatched: Boolean
        get() = step === OrderStep.MATCHED
    @Suppress("BooleanMethodIsAlwaysInverted")
    val isCompleted: Boolean
        get() = step === OrderStep.COMPLETED
}