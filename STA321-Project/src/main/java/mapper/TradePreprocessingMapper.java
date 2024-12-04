package mapper;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class TradePreprocessingMapper extends Mapper<LongWritable, Text, Text, Text>{

    // 设置的时间窗口
    protected LongWritable startTime = new LongWritable(20190102091500000L);
    protected LongWritable endTime = new LongWritable(20190102113000000L);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 每行数据以空格分隔
        String[] records = value.toString().split("\\s+");
        LongWritable ChannelNo = new LongWritable(Long.parseLong(records[5]));
        LongWritable ApplSeqNum = new LongWritable(Long.parseLong(records[7]));

        String SecurityID = records[8];
        String BidApplSeqNum = records[10];
        String OfferApplSeqNum = records[11];
        String Price = records[12];
        String TradeQty = records[13];
        String ExecType = records[14];
        LongWritable tradetime = new LongWritable(Long.parseLong(records[15]));

        // 判断ExecType为成交【先Selection，后Projection】
        if (ExecType.equals("F") && SecurityID.equals("002436")) {
            // 生成输出格式，Key: ChannelNo, ApplSeqNum, Value: SecurityID BidApplSeqNum OfferApplSeqNum Price TradeQty
            context.write(new Text(ChannelNo + " " + ApplSeqNum), new Text(BidApplSeqNum + " " + OfferApplSeqNum + " " + Price + " " + TradeQty + " " + tradetime)) ;
        }

    }
}
