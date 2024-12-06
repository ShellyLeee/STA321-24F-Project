package mapper;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class StockFlowMapper2 extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 按空格分割，解析 Key 和 Value
        String[] keyValueParts = value.toString().split("\\s+", 2);
        if (keyValueParts.length < 2) { // 检查是否包含 Key 和 Value
            System.err.println("Invalid record: " + value);
            return;
        }

        // Key 部分
        String recordValue = keyValueParts[1]; // Value 部分

        // 按逗号分割 Value 部分
        String[] fields = recordValue.split(",");
        if (fields.length < 4) { // 检查字段数量
            System.err.println("Invalid value: " + recordValue);
            return;
        }

        try {
            double totalTradeQty = Double.parseDouble(fields[0].trim());  // 合并后的成交量
            double totalAmount = Double.parseDouble(fields[1].trim());    // 合并后的成交额
            double circulationRatio = Double.parseDouble(fields[3].trim()); // 流通盘占比
            int tradeType = Integer.parseInt(fields[2].trim());           // 买卖类型（1=买，2=卖）

            // 判断单子类型
            String orderType = getOrderType(totalTradeQty, totalAmount, circulationRatio);

            // 构建输出的 Value，包含成交量、成交额、流通盘占比和买卖类型
            String result = String.join(",",
                    String.valueOf(totalTradeQty),  // 成交量
                    String.valueOf(totalAmount),    // 成交额
                    String.valueOf(circulationRatio), // 流通盘占比
                    String.valueOf(tradeType)       // 买卖类型
            );

            // 根据单子类型判断输出的 Key
            context.write(new Text(orderType), new Text(result));
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse value: " + recordValue);
        }
    }

    // 判断单子类型
    private String getOrderType(double totalTradeQty, double totalAmount, double circulationRatio) {
        if (totalTradeQty >= 200000 || totalAmount >= 1000000 || circulationRatio >= 0.003) {
            return "超大单";
        } else if ((totalTradeQty >= 60000 && totalTradeQty < 200000) ||
                (totalAmount >= 300000 && totalAmount < 1000000) ||
                (circulationRatio >= 0.001 && circulationRatio < 0.003)) {
            return "大单";
        } else if ((totalTradeQty >= 10000 && totalTradeQty < 60000) ||
                (totalAmount >= 50000 && totalAmount < 300000) ||
                (circulationRatio >= 0.00017 && circulationRatio < 0.001)) {
            return "中单";
        } else {
            return "小单";
        }
    }
}
