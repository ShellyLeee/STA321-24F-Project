package reducer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class StockFlowReducer2 extends Reducer<Text, Text, Text, Text> {

    // 累计各类型的成交数据
    private final double[] buyAmount = new double[4];   // [0: 超大单, 1: 大单, 2: 中单, 3: 小单]
    private final double[] buyQty = new double[4];
    private final double[] sellAmount = new double[4];
    private final double[] sellQty = new double[4];

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String orderType = key.toString();
        int index = getOrderTypeIndex(orderType);
        if (index == -1) {
            return; // 跳过未知类型
        }

        // 遍历每个值，累计买卖单的金额和数量
        for (Text value : values) {
            String[] fields = value.toString().split(",");
            if (fields.length < 4) {
                continue; // 跳过不完整数据
            }

            try {
                double tradeQty = Double.parseDouble(fields[0].trim());
                double amount = Double.parseDouble(fields[1].trim());
                int tradeType = Integer.parseInt(fields[3].trim()); // 1=买单，2=卖单

                if (tradeType == 1) { // 买单
                    buyAmount[index] += amount;
                    buyQty[index] += tradeQty;
                } else if (tradeType == 2) { // 卖单
                    sellAmount[index] += amount;
                    sellQty[index] += tradeQty;
                }
            } catch (NumberFormatException e) {
                // 捕获解析异常，跳过错误记录
                continue;
            }
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // 计算主力流入、主力流出、主力净流入
        double mainFlowIn = buyAmount[0] + buyAmount[1];  // 超大单和大单买入金额
        double mainFlowOut = sellAmount[0] + sellAmount[1]; // 超大单和大单卖出金额
        double netMainFlow = mainFlowIn - mainFlowOut;    // 主力净流入

        // 按要求的顺序输出统计结果
        String result = String.join(",",
                String.valueOf(netMainFlow),          // 主力净流入
                String.valueOf(mainFlowIn),           // 主力流入
                String.valueOf(mainFlowOut),          // 主力流出
                String.valueOf(buyQty[0]),            // 超大买单成交量
                String.valueOf(buyAmount[0]),         // 超大买单成交额
                String.valueOf(sellQty[0]),           // 超大卖单成交量
                String.valueOf(sellAmount[0]),        // 超大卖单成交额
                String.valueOf(buyQty[1]),            // 大买单成交量
                String.valueOf(buyAmount[1]),         // 大买单成交额
                String.valueOf(sellQty[1]),           // 大卖单成交量
                String.valueOf(sellAmount[1]),        // 大卖单成交额
                String.valueOf(buyQty[2]),            // 中买单成交量
                String.valueOf(buyAmount[2]),         // 中买单成交额
                String.valueOf(sellQty[2]),           // 中卖单成交量
                String.valueOf(sellAmount[2]),        // 中卖单成交额
                String.valueOf(buyQty[3]),            // 小买单成交量
                String.valueOf(buyAmount[3]),         // 小买单成交额
                String.valueOf(sellQty[3]),           // 小卖单成交量
                String.valueOf(sellAmount[3])         // 小卖单成交额
        );

        // 输出到上下文，Key 可以用 "Result" 或者其他描述性标签
        context.write(new Text("Result"), new Text(result));
    }

    // 根据单子类型获取索引
    private int getOrderTypeIndex(String orderType) {
        switch (orderType) {
            case "超大单": return 0;
            case "大单": return 1;
            case "中单": return 2;
            case "小单": return 3;
            default: return -1;
        }
    }
}
