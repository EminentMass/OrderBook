package orderbook.orderbook.log;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import orderbook.orderbook.OrderBook;

import java.io.*;

public class OrderLogger {

    public OrderLogger() throws Exception {
        File file = logFile();
        boolean dirExists = file.getParentFile().exists() || file.getParentFile().mkdir();
        if(!dirExists) {
            throw new Exception("Failed to log orders non existent log file parent directory");
        }

        boolean fileExists = file.exists() || file.createNewFile();
        if(!fileExists){
            throw new Exception("Failed to log orders unable to create log file");
        }
        writer = new FileWriter(file, true);

        beanToCsv = new StatefulBeanToCsvBuilder<OrderBookMutation>(writer).build();
    }

    public void logOrderBookMutation(OrderBookMutation mutation) {

        try {
            beanToCsv.write(mutation);
            writer.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        writer.close();
    }

    private final Writer writer;
    private final StatefulBeanToCsv<OrderBookMutation> beanToCsv;

    private static File logFile() {
        assert OrderBook.getInstance() != null;
        return new File(OrderBook.getInstance().getDataFolder().getAbsolutePath() + "/order_log.csv");
    }
}
