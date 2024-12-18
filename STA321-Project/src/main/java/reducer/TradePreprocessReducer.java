package reducer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class TradePreprocessReducer extends Reducer<Text, Text, Text, Text> {

    private static final long TIME_WINDOW = 10; // 10分钟时间窗口

    private final Text outputValueText = new Text(); // 复用 Text 对象，减少对象创建

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            try {
                // 手动解析字段，避免正则解析的开销
                String line = value.toString();
                String[] records = parseRecord(line);

                if (records == null) {
                    continue; // 数据格式错误，跳过
                }

                long bidApplSeqNum = Long.parseLong(records[10]);
                long offerApplSeqNum = Long.parseLong(records[11]);
                String price = records[12];
                String tradeQty = records[13];
                long tradeTime = Long.parseLong(records[15]);

                // 计算时间窗口 ID
                long timeWindowID = calculateTimeWindowID(tradeTime);

                int tradeType = (bidApplSeqNum > offerApplSeqNum) ? 1 : 2;

                // 构造输出值
                String outputValue = String.format("%d %d %s %s %d %d %d",
                        bidApplSeqNum, offerApplSeqNum, price, tradeQty, tradeTime, timeWindowID, tradeType);

                outputValueText.set(outputValue);
                context.write(key, outputValueText);
            } catch (Exception e) {
                // 控制错误日志的打印频率
                if (Math.random() < 0.01) { // 仅打印 1% 的错误
                    System.err.println("Error processing record in Reducer: " + value.toString());
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    // 从时间戳中提取分钟数
    private int getMinutesFromTimestamp(long tradeTime) {
        int hour = (int) ((tradeTime / 1000000) % 100);
        int minute = (int) ((tradeTime / 10000) % 100);
        return hour * 60 + minute;
    }

    // 计算时间窗口 ID
    private long calculateTimeWindowID(long tradeTime) {
        int minutes = getMinutesFromTimestamp(tradeTime);
        if (tradeTime >= 20190102093000000L && tradeTime <= 20190102113000000L) {
            return ((minutes - 570) / TIME_WINDOW) + 1; // 9:30 => 570分钟
        } else if (tradeTime >= 20190102130000000L && tradeTime <= 20190102150000000L) {
            return ((minutes - 780) / TIME_WINDOW) + 13; // 13:00 => 780分钟
        }
        return -1; // 默认非法时间处理
    }

    // 高效解析一行数据
    private String[] parseRecord(String line) {
        String[] records = new String[16];
        int idx = 0, lastIdx = 0;

        for (int i = 0; i < 15; i++) {
            int nextIdx = line.indexOf(' ', lastIdx);
            if (nextIdx == -1) {
                return null; // 数据不完整，返回 null
            }
            records[idx++] = line.substring(lastIdx, nextIdx).trim();
            lastIdx = nextIdx + 1;
        }
        records[idx] = line.substring(lastIdx).trim(); // 最后一列
        return records;
    }
}
