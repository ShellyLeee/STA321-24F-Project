package mapper;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class StockFlowMapper1 extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 使用正则表达式分割
        String[] fields = value.toString().split("\\s+");

        // 检查字段数量
        if (fields.length < 10) {
            System.err.println("Skipping incomplete record: " + value.toString());
            return; // 跳过不完整的数据
        }

        try {
            // 提取字段
            String bidApplSeqNum = fields[3].trim();      // 买方委托索引
            String offerApplSeqNum = fields[4].trim();    // 卖方委托索引
            double price = Double.parseDouble(fields[5].trim());       // 成交价格
            double tradeQty = Double.parseDouble(fields[6].trim());    // 成交数量
            int tradeType = Integer.parseInt(fields[9].trim());        // 1=主动买，2=主动卖
            String timeWindowID = fields[8].trim();                   // 时间窗口 ID

            // 计算成交额
            double amount = price * tradeQty;

            // 确定 Key 为 "委托索引_时间窗口"
            String keyBidOffer = (tradeType == 1) ? bidApplSeqNum : offerApplSeqNum;
            String compositeKey = keyBidOffer + "_" + timeWindowID;

            // 构建输出的 Value，保留时间窗口 ID
            String outputValue = String.join(",",
                    String.valueOf(tradeQty),    // 成交量
                    String.valueOf(amount),      // 成交额
                    String.valueOf(tradeType),   // 买卖类型
                    timeWindowID                 // 时间窗口 ID
            );

            // 输出 Key 和 Value
            context.write(new Text(compositeKey), new Text(outputValue));
        } catch (NumberFormatException e) {
            System.err.println("Skipping invalid record: " + value.toString());
        }
    }
}
