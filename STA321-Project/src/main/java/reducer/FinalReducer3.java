/**
 * FinalReducer.java
 *
 * FinalReducer 类是 Hadoop MapReduce 作业的 Reducer 类，负责根据时间窗口对交易数据进行汇总和统计。
 *
 * 作者: 欧炜娟
 * 功能: 该类处理 Mapper 输出的交易数据，通过时间窗口 ID 将所有相同时间窗口的数据汇总，
 *      并计算各类交易指标（如：主力流入、主力流出、买单和卖单的成交量和成交额等）。
 *      最终输出每个时间窗口内的详细统计数据，并按时间区间输出。
 * 实现方式:
 * - 在 `reduce` 方法中对每个时间窗口 ID 进行汇总。
 * - 计算每个时间窗口内的各类交易数据。
 * - 输出每个时间窗口对应的统计信息。
 */

package reducer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FinalReducer3 extends Reducer<LongWritable, Text, Text, Text> {

    private static final double CIRCULATION_STOCK = 17170245800.0; // 流通盘总量

    private static final long TIME_WINDOW_MILLIS = 600000; // 时间窗口

    // 时间段的起始和结束时间戳
    private static final long MORNING_START = 20190102093000000L;
    private static final long MORNING_END = 20190102113000000L;
    private static final long AFTERNOON_START = 20190102130000000L;
    private static final long AFTERNOON_END = 20190102150000000L;

    // 判断是否有缺失的时间窗口的指标
    private long index = 1;

    // 标记表头是否已输出
    private boolean isHeaderWritten = false;

    @Override
    public void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

        // 如果是第一次输出，则添加表头
        if (!isHeaderWritten) {
            String header = "主力净流入,主力流入,主力流出,超大买单成交量,超大买单成交额,超大卖单成交量,超大卖单成交额,"
                    + "大买单成交量,大买单成交额,大卖单成交量,大卖单成交额,中买单成交量,中买单成交额,中卖单成交量,中卖单成交额,"
                    + "小买单成交量,小买单成交额,小卖单成交量,小卖单成交额";
            context.write(new Text(header), new Text("时间区间"));  // 输出表头
            isHeaderWritten = true;  // 标记表头已输出
        }


        // 获取当前时间窗口对应的时间区间，并补足缺失的时间区间
        long timeWindowID = key.get();

        if (index < timeWindowID){
            long initial = index;
            for (long i = initial; i < key.get(); i++){
                String timeInterval = calculateTimeInterval(index);
                context.write(new Text("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0"), new Text(timeInterval)); // 补足缺失的时间区间
                index++;
            }
        }

        // 存储当前id的时间窗口
        String timeInterval = calculateTimeInterval(timeWindowID);

        // 存储每个主动委托索引的累计成交量、成交额和买卖类型
        Map<String, Object[]> activeOrderData = new HashMap<>();

        // 遍历所有值，累加每个主动委托索引的成交量和成交额，并记录买卖类型
        for (Text value : values) {
            String[] fields = value.toString().split("\t");

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

        Text outputValue = new Text(resultBuilder.toString());

        context.write(outputValue, new Text(timeInterval));
        index++;
    }

    // 将时间字符串（yyyyMMddHHmmssSSS）转化为毫秒
    public static long getTimeInMillis(long tradetime) {
        // 转换 tradetime
        String timeStr = String.valueOf(tradetime).substring(8); // 提取 "HHmmssSSS"
        int hour = Integer.parseInt(timeStr.substring(0, 2));
        int minute = Integer.parseInt(timeStr.substring(2, 4));
        int second = Integer.parseInt(timeStr.substring(4, 6));
        int millisecond = Integer.parseInt(timeStr.substring(6, 9));

        // 将时间转换为从午夜开始的毫秒数
        return hour * 3600 * 1000 + minute * 60 * 1000 + second * 1000 + millisecond;
    }

    // 时间毫秒的运算
    public static String addTimeMillis(String time, long n, long b) {
        // 解析输入的时间，获取小时、分钟、秒和毫秒
        long hour = Long.parseLong(time.substring(0, 2));
        long minute = Long.parseLong(time.substring(2, 4));
        long second = Long.parseLong(time.substring(4, 6));
        long millisecond = Long.parseLong(time.substring(6, 9));

        // 计算总的毫秒数
        long totalMillis = (hour * 3600 + minute * 60 + second) * 1000 + millisecond + (n * b);

        // 计算新的时间
        long newHour = (totalMillis / (3600 * 1000)) % 24; // 处理 24 小时制
        long newMinute = (totalMillis / (60 * 1000)) % 60;
        long newSecond = (totalMillis / 1000) % 60;
        long newMillisecond = totalMillis % 1000;

        // 格式化输出，保证输出为九位数 (HHmmssSSS)
        return String.format("%02d%02d%02d%03d", newHour, newMinute, newSecond, newMillisecond);
    }

    // 计算时间区间
    public static String calculateTimeInterval(long timeWindowID) {

        String timeWindowBegin = "";
        String timeWindowEnd = "";

        // 获取早上和下午的开始时间的毫秒数
        long morningStartInMillis = getTimeInMillis(MORNING_START);
        long morningEndInMillis = getTimeInMillis(MORNING_END);
        long afternoonStartInMillis = getTimeInMillis(AFTERNOON_START);
        long afternoonEndInMillis = getTimeInMillis(AFTERNOON_END);

        // 获取早上和下午的 timeWindowID 间隔
        long interval = (morningEndInMillis - morningStartInMillis) / TIME_WINDOW_MILLIS;

        String morningStart = String.valueOf(MORNING_START).substring(8); // HHmmssSSS
        String afternoonStart = String.valueOf(AFTERNOON_START).substring(8); // HHmmssSSS

        if (timeWindowID <= interval) {
            // 早上 9:30 - 11:30 的时间段，计算属于哪个区间
            timeWindowBegin = addTimeMillis(morningStart, timeWindowID - 1, TIME_WINDOW_MILLIS);
            timeWindowEnd = addTimeMillis(morningStart, timeWindowID, TIME_WINDOW_MILLIS);
        } else if (timeWindowID > interval) {
            // 下午 13:00 - 15:00 的时间段，计算属于哪个区间
            timeWindowBegin = addTimeMillis(afternoonStart, timeWindowID - 1 - interval, TIME_WINDOW_MILLIS);
            timeWindowEnd = addTimeMillis(afternoonStart, timeWindowID - interval, TIME_WINDOW_MILLIS);
        }

        return "20190102" + timeWindowBegin + " to 20190102" + timeWindowEnd;
    }

}
