package mapper;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class StockFlowMapper2 extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 解析输入数据（来自reducer1的输出）
        String[] fields = value.toString().split(",");
        double totalTradeQty = Double.parseDouble(fields[0].trim());  // 合并后的成交量
        double totalAmount = Double.parseDouble(fields[1].trim());    // 合并后的成交额
        int tradeType = Integer.parseInt(fields[2].trim());           // 买卖类型（1=买，2=卖）

        // 判断单子类型
        String orderType = getOrderType(totalTradeQty, totalAmount);

        // 构建输出的Value，包含成交量、成交额和买卖类型
        String result = String.join(",",
                String.valueOf(totalTradeQty),  // 成交量
                String.valueOf(totalAmount),    // 成交额
                String.valueOf(tradeType)       // 买卖类型
        );

        // 根据单子类型判断输出的Key
        context.write(new Text(orderType), new Text(result));
    }

    // 判断单子类型
    private String getOrderType(double totalTradeQty, double totalAmount) {
        if (totalTradeQty > 100000 || totalAmount > 1000000) {
            return "超大单";
        } else if (totalTradeQty > 50000 || totalAmount > 500000) {
            return "大单";
        } else if (totalTradeQty > 10000 || totalAmount > 100000) {
            return "中单";
        } else {
            return "小单";
        }
    }
}
