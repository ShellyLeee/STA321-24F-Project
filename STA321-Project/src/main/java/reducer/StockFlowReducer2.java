package reducer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class StockFlowReducer2 extends Reducer<Text, Text, Text, Text> {

    // 变量用于存储各单子类型的累计数据
    private double superBigBuyAmount = 0.0, superBigBuyQty = 0.0;
    private double superBigSellAmount = 0.0, superBigSellQty = 0.0;
    private double bigBuyAmount = 0.0, bigBuyQty = 0.0;
    private double bigSellAmount = 0.0, bigSellQty = 0.0;
    private double middleBuyAmount = 0.0, middleBuyQty = 0.0;
    private double middleSellAmount = 0.0, middleSellQty = 0.0;
    private double smallBuyAmount = 0.0, smallBuyQty = 0.0;
    private double smallSellAmount = 0.0, smallSellQty = 0.0;

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // 遍历所有的值，累加成交量和成交额
        for (Text value : values) {
            String[] fields = value.toString().split(",");
            double tradeQty = Double.parseDouble(fields[0].trim());
            double amount = Double.parseDouble(fields[1].trim());
            int tradeType = Integer.parseInt(fields[2].trim());  // 1=买单，2=卖单

            // 只需要根据 key（单子类型）来直接累加
            if (key.toString().equals("超大单")) {
                if (tradeType == 1) {
                    superBigBuyAmount += amount;
                    superBigBuyQty += tradeQty;
                } else if (tradeType == 2) {
                    superBigSellAmount += amount;
                    superBigSellQty += tradeQty;
                }
            } else if (key.toString().equals("大单")) {
                if (tradeType == 1) {
                    bigBuyAmount += amount;
                    bigBuyQty += tradeQty;
                } else if (tradeType == 2) {
                    bigSellAmount += amount;
                    bigSellQty += tradeQty;
                }
            } else if (key.toString().equals("中单")) {
                if (tradeType == 1) {
                    middleBuyAmount += amount;
                    middleBuyQty += tradeQty;
                } else if (tradeType == 2) {
                    middleSellAmount += amount;
                    middleSellQty += tradeQty;
                }
            } else if (key.toString().equals("小单")) {
                if (tradeType == 1) {
                    smallBuyAmount += amount;
                    smallBuyQty += tradeQty;
                } else if (tradeType == 2) {
                    smallSellAmount += amount;
                    smallSellQty += tradeQty;
                }
            }
        }

        // 计算主力流入，主力流出，主力净流入
        double mainFlowIn = superBigBuyAmount + bigBuyAmount;  // 主力流入
        double mainFlowOut = superBigSellAmount + bigSellAmount; // 主力流出
        double netMainFlow = mainFlowIn - mainFlowOut; // 主力净流入

        // 输出所有统计结果
        String result = String.join(",",
                String.valueOf(superBigBuyQty), String.valueOf(superBigBuyAmount),
                String.valueOf(superBigSellQty), String.valueOf(superBigSellAmount),
                String.valueOf(bigBuyQty), String.valueOf(bigBuyAmount),
                String.valueOf(bigSellQty), String.valueOf(bigSellAmount),
                String.valueOf(middleBuyQty), String.valueOf(middleBuyAmount),
                String.valueOf(middleSellQty), String.valueOf(middleSellAmount),
                String.valueOf(smallBuyQty), String.valueOf(smallBuyAmount),
                String.valueOf(smallSellQty), String.valueOf(smallSellAmount),
                String.valueOf(mainFlowIn), String.valueOf(mainFlowOut), String.valueOf(netMainFlow)
        );

        context.write(key, new Text(result));
    }
}
