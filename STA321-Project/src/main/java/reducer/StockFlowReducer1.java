package reducer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class StockFlowReducer1 extends Reducer<Text, Text, Text, Text> {

    private static final double circulationStock = 17170245800.0; // 流通盘总量

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        double totalTradeQty = 0.0; // 累计成交量
        double totalAmount = 0.0;  // 累计成交额
        int tradeType = -1;
        String timeWindowID = null;

        // 遍历相同 Key 的所有值
        for (Text value : values) {
            String[] fields = value.toString().split(",");
            if (fields.length < 4) {
                continue; // 跳过不合法的记录
            }

            try {
                double tradeQty = Double.parseDouble(fields[0].trim());  // 成交量
                double amount = Double.parseDouble(fields[1].trim());   // 成交额
                int type = Integer.parseInt(fields[2].trim());          // 买卖类型
                timeWindowID = fields[3].trim();                        // 时间窗口 ID

                totalTradeQty += tradeQty; // 累加成交量
                totalAmount += amount;     // 累加成交额
                tradeType = type;          // 保留买卖类型
            } catch (NumberFormatException e) {
                // 捕获解析错误，跳过记录
                continue;
            }
        }

        // 计算流通盘占比
        double circulationRatio = totalTradeQty / circulationStock;

        // 输出合并后的结果，包含 timeWindowID 和流通盘占比
        String result = String.join(",",
                String.valueOf(totalTradeQty),  // 总成交量
                String.valueOf(totalAmount),    // 总成交额
                String.valueOf(circulationRatio), // 流通盘占比
                timeWindowID,                   // 时间窗口 ID
                String.valueOf(tradeType)       // 买卖类型
        );
        context.write(key, new Text(result));
    }
}
