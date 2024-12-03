package reducer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class StockFlowReducer1 extends Reducer<Text, Text, Text, Text> {
    private static final double circulationStock = 10000000;  // 假设流通盘是固定的

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        double totalTradeQty = 0.0;
        double totalAmount = 0.0;
        int tradeType = -1;

        // 遍历相同 key 的所有值，合并成交量和成交额
        for (Text value : values) {
            String[] fields = value.toString().split(",");
            double tradeQty = Double.parseDouble(fields[0].trim());
            double amount = Double.parseDouble(fields[1].trim());
            int type = Integer.parseInt(fields[2].trim());

            // 如果 tradeType 未确定，使用第一个记录的 tradeType
            if (tradeType == -1) {
                tradeType = type;
            }

            // 累加成交量和成交额
            totalTradeQty += tradeQty;
            totalAmount += amount;
        }

        // 输出合并后的结果
        String result = String.join(",", String.valueOf(totalTradeQty), String.valueOf(totalAmount), String.valueOf(tradeType));
        context.write(key, new Text(result));
    }
}
