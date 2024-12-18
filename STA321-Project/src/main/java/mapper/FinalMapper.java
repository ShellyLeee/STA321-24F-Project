package mapper;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FinalMapper extends Mapper<LongWritable, Text, Text, Text> {

    private static final long TIME_WINDOW = 10; // 10分钟时间窗口

    // 时间段的起始和结束时间戳
    private static final long MORNING_START = 20190102093000000L;
    private static final long MORNING_END = 20190102113000000L;
    private static final long AFTERNOON_START = 20190102130000000L;
    private static final long AFTERNOON_END = 20190102150000000L;

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        // 使用局部变量来避免创建冗余对象
        String[] records = value.toString().split("\\s+");
        if (records.length < 16) {
            return; // 确保数据有效
        }

        try {
            String execType = records[14];
            String securityID = records[8];
            long tradeTime = Long.parseLong(records[15]);

            // 筛选条件：仅处理 ExecType = F 和 SecurityID = 000001
            if ("F".equals(execType) && "000001".equals(securityID)) {

                // 筛选时间窗口（早上和下午的时间段）
                if (isWithinTradingTime(tradeTime)) {
                    long bidApplSeqNum = Long.parseLong(records[10]);
                    long offerApplSeqNum = Long.parseLong(records[11]);
                    String price = records[12];
                    String tradeQty = records[13];

                    // 计算时间窗口ID
                    long timeWindowID = calculateTimeWindowID(tradeTime);

                    // 确定交易类型并获取主动单索引
                    String activeOrderIndex = (bidApplSeqNum > offerApplSeqNum) ? String.valueOf(bidApplSeqNum) : String.valueOf(offerApplSeqNum);
                    int tradeType = (bidApplSeqNum > offerApplSeqNum) ? 1 : 2;

                    // 输出 Key-Value 对
                    String outputKey = String.valueOf(timeWindowID);
                    String outputValue = activeOrderIndex + " " + price + " " + tradeQty + " " + tradeType;

                    context.write(new Text(outputKey), new Text(outputValue));
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // 记录错误并跳过此行数据
            System.err.println("Error processing record: " + value.toString());
        }
    }

    // 判断交易时间是否在合法的时间段内
    private boolean isWithinTradingTime(long tradeTime) {
        return (tradeTime >= MORNING_START && tradeTime <= MORNING_END) ||
                (tradeTime >= AFTERNOON_START && tradeTime <= AFTERNOON_END);
    }


    // 将时间字符串（yyyyMMddHHmmssSSS）转化为分钟
    public static int getTimeInMinutes(long tradetime) {
        // 转换 tradetime
        String timeStr = String.valueOf(tradetime).substring(8, 12); // 提取 "HHmm"
        int hour = Integer.parseInt(timeStr.substring(0, 2));
        int minute = Integer.parseInt(timeStr.substring(2, 4));

        // 将时间转换为从午夜开始的分钟数
        return hour * 60 + minute;
    }

    // 计算时间窗口ID
    public long calculateTimeWindowID(long tradetime) {
        // 获取输入时间的分钟数
        int currentTimeInMinutes = getTimeInMinutes(tradetime);

        // 获取早上和下午的开始时间的分钟数
        int morningStartInMinutes = getTimeInMinutes(MORNING_START);
        int afternoonStartInMinutes = getTimeInMinutes(AFTERNOON_START);

        // 判断当前时间是早上还是下午，并计算属于哪个时间窗口
        long timeWindowID = -1;

        if (currentTimeInMinutes >= morningStartInMinutes && currentTimeInMinutes <= getTimeInMinutes(MORNING_END)) {
            // 早上 9:30 - 11:30 的时间段，计算属于哪个窗口
            timeWindowID = (currentTimeInMinutes - morningStartInMinutes) / TIME_WINDOW + 1;
        } else if (currentTimeInMinutes >= afternoonStartInMinutes && currentTimeInMinutes < getTimeInMinutes(AFTERNOON_END)) {
            // 下午 13:00 - 15:00 的时间段，计算属于哪个窗口
            timeWindowID = (currentTimeInMinutes - afternoonStartInMinutes) / TIME_WINDOW + 13; // 下午的时间窗口ID从13开始
        } else if (currentTimeInMinutes == getTimeInMinutes(AFTERNOON_END)) {
            timeWindowID = (currentTimeInMinutes - afternoonStartInMinutes) / TIME_WINDOW + 12;
        }

        return timeWindowID;
    }
}
