package reducer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class StockFlowReducer2 extends Reducer<IntWritable, Text, Text, Text> {

    // 标记表头是否已输出
    private boolean isHeaderWritten = false;

    @Override
    public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // 初始化统计数据
        double[] buyAmount = new double[4];   // [0: 超大单, 1: 大单, 2: 中单, 3: 小单]
        double[] buyQty = new double[4];
        double[] sellAmount = new double[4];
        double[] sellQty = new double[4];

        // 遍历记录
        for (Text value : values) {
            String[] fields = value.toString().split(",");
            if (fields.length < 4) {
                continue; // 跳过不完整数据
            }

            try {
                String orderType = fields[0].trim(); // 单子类型
                double tradeQty = Double.parseDouble(fields[1].trim());  // 成交量
                double amount = Double.parseDouble(fields[2].trim());    // 成交额
                int tradeType = Integer.parseInt(fields[3].trim());      // 1=买单, 2=卖单

                // 根据单子类型获取索引
                int index = getOrderTypeIndex(orderType);
                if (index == -1) {
                    continue; // 跳过未知类型
                }

                // 累计买卖单的金额和数量
                if (tradeType == 1) { // 买单
                    buyAmount[index] += amount;
                    buyQty[index] += tradeQty;
                } else if (tradeType == 2) { // 卖单
                    sellAmount[index] += amount;
                    sellQty[index] += tradeQty;
                }
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse value: " + value.toString());
            }
        }

        // 计算主力流入、主力流出、主力净流入
        double mainFlowIn = buyAmount[0] + buyAmount[1];  // 超大单和大单买入金额
        double mainFlowOut = sellAmount[0] + sellAmount[1]; // 超大单和大单卖出金额
        double netMainFlow = mainFlowIn - mainFlowOut;    // 主力净流入

        // 构建输出结果
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

        // 如果是第一次输出，则添加表头
        if (!isHeaderWritten) {
            String header = "主力净流入,主力流入,主力流出,超大买单成交量,超大买单成交额,超大卖单成交量,超大卖单成交额,"
                    + "大买单成交量,大买单成交额,大卖单成交量,大卖单成交额,中买单成交量,中买单成交额,中卖单成交量,中卖单成交额,"
                    + "小买单成交量,小买单成交额,小卖单成交量,小卖单成交额";
            context.write(new Text(header), new Text("," + "时间窗口id"));  // 输出表头
            isHeaderWritten = true;  // 标记表头已输出
        }

        // 输出 Key 为 timeWindowID，Value 为统计结果
        context.write(new Text(result), new Text("," + key.toString()));
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
