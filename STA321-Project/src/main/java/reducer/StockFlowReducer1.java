package reducer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class StockFlowReducer1 extends Reducer<Text, Text, Text, Text> {
    private static final double circulationStock = 17170245800.0;  // 使用 double 类型的流通盘总量

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        double totalTradeQty = 0.0;  // 使用 double 类型累计成交量
        double totalAmount = 0.0;   // 使用 double 类型累计成交额
        int tradeType = -1;
        boolean tradeTypeConsistent = true;  // 检查 tradeType 是否一致
        int initialTradeType = -1;

        // 遍历相同 key 的所有值，合并成交量和成交额
        for (Text value : values) {
            String[] fields = value.toString().split(",");
            if (fields.length < 3) {
                // 跳过不合法的记录
                continue;
            }

            try {
                double tradeQty = Double.parseDouble(fields[0].trim());  // 成交量为 double 类型
                double amount = Double.parseDouble(fields[1].trim());   // 成交额为 double 类型
                int type = Integer.parseInt(fields[2].trim());          // 保持买卖类型为 int 类型

                // 检查 tradeType 是否一致
                if (initialTradeType == -1) {
                    initialTradeType = type;
                } else if (type != initialTradeType) {
                    tradeTypeConsistent = false;
                }

                // 累加成交量和成交额
                totalTradeQty += tradeQty;
                totalAmount += amount;

            } catch (NumberFormatException e) {
                // 捕获解析数字时的错误，跳过该记录
                continue;
            }
        }

        // 如果 tradeType 不一致，记录日志或处理逻辑
        if (!tradeTypeConsistent) {
            tradeType = -1;  // 设置为 -1 表示数据有问题
        } else {
            tradeType = initialTradeType;
        }

        // 计算流通盘占比
        double circulationRatio = totalTradeQty / circulationStock;

        // 输出合并后的结果，包含流通盘占比
        String result = String.join(",",
                String.valueOf(totalTradeQty),
                String.valueOf(totalAmount),
                String.valueOf(tradeType),
                String.valueOf(circulationRatio));
        context.write(key, new Text(result));
    }
}
