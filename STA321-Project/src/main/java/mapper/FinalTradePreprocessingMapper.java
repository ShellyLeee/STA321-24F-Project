package mapper;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FinalTradePreprocessingMapper extends Mapper<LongWritable, Text, Text, Text> {

    // 时间窗口大小：自定义
    private final long TIME_WINDOW = 10; // 10分钟

    // 时间段的起始和结束时间戳（即 9:30-11:30 和 13:00-15:00）
    private final long MORNING_START = 20190102093000000L; // 2019-01-02 09:30:00.000
    private final long MORNING_END = 20190102113000000L;   // 2019-01-02 11:30:00.000
    private final long AFTERNOON_START = 20190102130000000L; // 2019-01-02 13:00:00.000
    private final long AFTERNOON_END = 20190102150000000L;   // 2019-01-02 15:00:00.000

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        int TradeType = 0;

        // 每行数据以空格分隔
        String[] records = value.toString().split("\\s+");
        LongWritable ChannelNo = new LongWritable(Long.parseLong(records[5]));
        LongWritable ApplSeqNum = new LongWritable(Long.parseLong(records[7]));

        String SecurityID = records[8];
        long BidApplSeqNum = Long.parseLong(records[10]);
        long OfferApplSeqNum = Long.parseLong(records[11]);
        String Price = records[12];
        String TradeQty = records[13];
        String ExecType = records[14];
        long tradetime = new Long(Long.parseLong(records[15]));

        // 仅处理 "F" 类型的成交单，并且筛选出 SecurityID 为 "000001"
        if (ExecType.equals("F") && SecurityID.equals("000001")) {
            // 筛选出时间在 9:30-11:30 以及 13:00-15:00 时间段内的交易记录
            if ((tradetime >= MORNING_START && tradetime <= MORNING_END) || (tradetime >= AFTERNOON_START && tradetime <= AFTERNOON_END)) {

                // 计算该交易时间所属的 timeWindowID
                long timeWindowID = calculateTimeWindowID(tradetime);

                TradeType = (BidApplSeqNum > OfferApplSeqNum) ? 1 : 2; // 1 为主动买，2 为主动卖

                // 输出：key 为 ChannelNo 和 ApplSeqNum，value 为成交单信息和计算的 timeWindowID
                context.write(new Text(ChannelNo + "-" + ApplSeqNum), new Text(BidApplSeqNum + " " + OfferApplSeqNum + " " + Price + " " + TradeQty + " " + tradetime + " " + timeWindowID + " " + TradeType));
            }
        }
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
