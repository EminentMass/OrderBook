package orderbook.orderbook.log;

import com.opencsv.bean.AbstractBeanField;

@SuppressWarnings("unused")
public class ChangeTypeConverter extends AbstractBeanField<OrderBookMutation, Integer> {

    @Override
    protected Object convert(String value) {
        if(value == null) {
            return null;
        }
        return ChangeType.valueOf(value);
    }

    @Override
    protected String convertToWrite(Object value) {
        if(!(value instanceof ChangeType ct)) {
            return null;
        }
        return ct.name();
    }
}