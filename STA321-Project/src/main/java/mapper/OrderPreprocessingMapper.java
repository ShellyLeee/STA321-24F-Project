package mapper;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class OrderPreprocessingMapper extends Mapper<LongWritable, Text, Text, Text>{
    @Override
    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {
        // 每行数据以空格分隔
        String[] records = value.toString().split("\\s+");
        LongWritable ChannelNo = new LongWritable(Long.parseLong(records[5]));
        LongWritable ApplSeqNum = new LongWritable(Long.parseLong(records[7]));
        String TransactionTime = records[12];
        String Side = records[13];

        // 生成输出格式，Key: ApplSeqNum, Value: TransactionTime Side
        context.write(new Text(ChannelNo + " " + ApplSeqNum), new Text(TransactionTime + " " + Side)) ;

    }
}
