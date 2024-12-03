package mapper;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class StockFlowMapper1 extends Mapper<LongWritable, Text, Text, Text> {
    private static final double circulationStock = 10000000;  // 假设流通盘是固定的

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 解析输入数据
        //注意！！！看一下输出给的文件是不是按照“，”分割的，如果不是，需要修改这里的分割符
        String[] fields = value.toString().split(",");
        String channelNo = fields[0].trim();         // 频道代码
        String applSeqNum = fields[1].trim();        // 委托索引
        String bidApplSeqNum = fields[2].trim();     // 买方委托索引
        String offerApplSeqNum = fields[3].trim();   // 卖方委托索引
        double price = Double.parseDouble(fields[4].trim());    // 成交价格
        double tradeQty = Double.parseDouble(fields[5].trim());  // 成交数量
        int tradeType = Integer.parseInt(fields[6].trim());     // 1=主动买，2=主动卖

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
    }
}
