package reducer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class FinalReducer extends Reducer<Text, Text, Text, Text> {

    private static final double CIRCULATION_STOCK = 17170245800.0; // 流通盘总量

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // 使用 TreeMap 确保时间窗口 ID 的排序
        TreeMap<Integer, String> sortedResults = new TreeMap<>();

        // 存储每个主动委托索引的累计成交量、成交额和买卖类型
        Map<String, Object[]> activeOrderData = new HashMap<>();

        // 遍历所有值，累加每个主动委托索引的成交量和成交额，并记录买卖类型
        for (Text value : values) {
            String[] fields = value.toString().split(" ");
            if (fields.length < 4) {
                continue; // 跳过不合法的记录
            }

            try {
                String activeOrderIndex = fields[0].trim();       // 主动委托索引
                double price = Double.parseDouble(fields[1]);    // 成交价格
                double tradeQty = Double.parseDouble(fields[2]); // 成交量
                double amount = price * tradeQty;                // 成交金额
                int tradeType = Integer.parseInt(fields[3].trim()); // 买卖类型

                activeOrderData.putIfAbsent(activeOrderIndex, new Object[]{0.0, 0.0, tradeType});
                Object[] data = activeOrderData.get(activeOrderIndex);
                data[0] = (double) data[0] + tradeQty; // 累加成交量
                data[1] = (double) data[1] + amount;   // 累加成交额
                data[2] = tradeType;                  // 更新买卖类型（确保一致）
            } catch (NumberFormatException e) {
                System.err.println("Error parsing record: " + value.toString());
            }
        }

        // 存储不同单子类型的统计数据
        double[] buyQty = new double[4];   // 买单成交量：0-超大单, 1-大单, 2-中单, 3-小单
        double[] buyAmount = new double[4]; // 买单成交额
        double[] sellQty = new double[4];  // 卖单成交量
        double[] sellAmount = new double[4]; // 卖单成交额

        double mainFlowIn = 0.0;   // 主力流入
        double mainFlowOut = 0.0;  // 主力流出
        double netMainFlow = 0.0;  // 主力净流入

        // 判断单子类型并分类统计
        for (Map.Entry<String, Object[]> entry : activeOrderData.entrySet()) {
            String activeOrderIndex = entry.getKey();
            Object[] data = entry.getValue();
            double totalTradeQty = (double) data[0];
            double totalAmount = (double) data[1];
            int tradeType = (int) data[2];
            double circulationRatio = totalTradeQty / CIRCULATION_STOCK;

            // 确定单子类型索引
            int orderTypeIndex;
            if (totalTradeQty >= 200000 || totalAmount >= 1000000 || circulationRatio >= 0.003) {
                orderTypeIndex = 0; // 超大单
            } else if (totalTradeQty >= 60000 || totalAmount >= 300000 || circulationRatio >= 0.001) {
                orderTypeIndex = 1; // 大单
            } else if (totalTradeQty >= 10000 || totalAmount >= 50000 || circulationRatio >= 0.00017) {
                orderTypeIndex = 2; // 中单
            } else {
                orderTypeIndex = 3; // 小单
            }

            // 根据买卖类型累加数据
            if (tradeType == 1) { // 买单
                buyQty[orderTypeIndex] += totalTradeQty;
                buyAmount[orderTypeIndex] += totalAmount;
                if (orderTypeIndex == 0 || orderTypeIndex == 1) {
                    mainFlowIn += totalAmount; // 主力流入
                }
            } else if (tradeType == 2) { // 卖单
                sellQty[orderTypeIndex] += totalTradeQty;
                sellAmount[orderTypeIndex] += totalAmount;
                if (orderTypeIndex == 0 || orderTypeIndex == 1) {
                    mainFlowOut += totalAmount; // 主力流出
                }
            }
        }

        // 计算主力净流入
        netMainFlow = mainFlowIn - mainFlowOut;

        // 构建输出结果
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(netMainFlow).append(",")
                .append(mainFlowIn).append(",")
                .append(mainFlowOut);

        for (int i = 0; i < 4; i++) {
            resultBuilder.append(",").append(buyQty[i])
                    .append(",").append(buyAmount[i])
                    .append(",").append(sellQty[i])
                    .append(",").append(sellAmount[i]);
        }

        // 将结果与时间窗口 ID 一起存储
        int timeWindowID = Integer.parseInt(key.toString());
        resultBuilder.append(",").append(timeWindowID);

        sortedResults.put(timeWindowID, resultBuilder.toString());

        // 遍历排序后的结果并输出
        for (Map.Entry<Integer, String> sortedEntry : sortedResults.entrySet()) {
            context.write(new Text(String.valueOf(sortedEntry.getKey())), new Text(sortedEntry.getValue()));
        }
    }
}
