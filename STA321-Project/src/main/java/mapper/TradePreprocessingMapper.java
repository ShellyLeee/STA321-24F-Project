package mapper;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class TradePreprocessingMapper extends Mapper<LongWritable, Text, LongWritable, Text>{
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 每行数据以空格分隔
        String[] records = value.toString().split(" ");
        LongWritable ApplSeqNum = new LongWritable(Long.parseLong(records[7]));
        String SecurityID = records[8];
        String BidApplSeqNum = records[10];
        String OfferApplSeqNum = records[11];
        String Price = records[12];
        String TradeQty = records[13];
        String ExecType = records[14];

        // 判断ExecType为成交【先Selection，后Projection】
        if (ExecType.equals("F")) {
            // 生成输出格式，Key: ApplSeqNum, Value: SecurityID BidApplSeqNum OfferApplSeqNum Price TradeQty
            context.write(ApplSeqNum, new Text(SecurityID + " " + BidApplSeqNum + " " + OfferApplSeqNum + " " + Price + " " + TradeQty)) ;

        }
    }
}
