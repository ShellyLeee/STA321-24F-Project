package mapper;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class TradePreprocessMapper extends Mapper<LongWritable, Text, Text, Text> {

    // 定义时间段常量
    private static final long MORNING_START = 20190102093000000L;
    private static final long MORNING_END = 20190102113000000L;
    private static final long AFTERNOON_START = 20190102130000000L;
    private static final long AFTERNOON_END = 20190102150000000L;

    // 判断时间是否在有效区间
    private boolean isWithinTradingHours(long tradetime) {
        // 合并时间段判断逻辑，减少重复比较
        return (tradetime >= MORNING_START && tradetime <= MORNING_END) ||
                (tradetime >= AFTERNOON_START && tradetime <= AFTERNOON_END);
    }

    // Mapper 核心逻辑
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();

        // 手动按空格分割字段，避免正则解析的开销
        int idx = 0;
        int lastIdx = 0;
        String[] records = new String[16];
        while (idx < 16 && (lastIdx = line.indexOf(" ", lastIdx)) != -1) {
            records[idx++] = line.substring(0, lastIdx).trim();
            line = line.substring(lastIdx + 1);
        }
        records[idx] = line.trim(); // 最后一个字段

        // 数据完整性校验
        if (records[15] == null || records[5] == null || records[7] == null || records[8] == null || records[14] == null) {
            return; // 跳过数据不完整的记录
        }

        try {
            String execType = records[14];
            String securityID = records[8];
            long tradetime = Long.parseLong(records[15]);

            // 筛选条件：ExecType = F 且 SecurityID = 000001 且时间在有效区间
            if ("F".equals(execType) && "000001".equals(securityID) && isWithinTradingHours(tradetime)) {
                // 构造 Key 和 Value
                String outputKey = records[5] + "-" + records[7];
                context.write(new Text(outputKey), value); // Value 为原始记录
            }
        } catch (NumberFormatException e) {
            // 捕获数字转换异常，忽略此条记录
        }
    }
}
