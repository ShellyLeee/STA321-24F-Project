package mapper;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class StockFlowMapper1 extends Mapper<LongWritable, Text, Text, Text> {
    private static final double circulationStock = 10000000;  // 假设流通盘是固定的

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 使用正则表达式分割，匹配任意数量的空格
        String[] fields = value.toString().split("\\s+");

        // 检查字段数量
        if (fields.length < 9) {
            return; // 跳过不完整的数据
        }

        try {
            // 提取字段
            String bidApplSeqNum = fields[3].trim();     // 买方委托索引
            String offerApplSeqNum = fields[4].trim();   // 卖方委托索引
            double price = Double.parseDouble(fields[5].trim());    // 成交价格
            double tradeQty = Double.parseDouble(fields[6].trim());  // 成交数量
            int tradeType = Integer.parseInt(fields[8].trim());     // 1=主动买，2=主动卖

            // 计算成交额
            double amount = price * tradeQty;

            // 根据 BidApplSeqNum 或 OfferApplSeqNum 进行去重
            String keyBidOffer = (tradeType == 1) ? bidApplSeqNum : offerApplSeqNum;

            // 计算流通盘占比
            double circulationRatio = tradeQty / circulationStock;

            // 输出 Key-Value：Key 为 BidApplSeqNum 或 OfferApplSeqNum，Value 为成交量、成交额、买卖类型
            String outputValue = String.join(",",
                    String.valueOf(tradeQty),   // 成交量
                    String.valueOf(amount),     // 成交额
                    String.valueOf(tradeType)   // 买卖类型
            );

            context.write(new Text(keyBidOffer), new Text(outputValue));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // 捕获异常，跳过无效记录
            return;
        }
    }
}
