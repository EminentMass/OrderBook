package orderbook.orderbook.log;

import com.opencsv.bean.AbstractBeanField;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class ItemStackConverter extends AbstractBeanField<OrderBookMutation, Integer> {
    @Override
    protected Object convert(String value) {
        return null;
    }

    @Override
    protected String convertToWrite(Object value) {
        if(!(value instanceof ItemStack is)){
            return null;
        }

        return is.toString();
    }
}
