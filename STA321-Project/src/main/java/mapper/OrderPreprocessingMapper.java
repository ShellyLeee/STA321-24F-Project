package mapper;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class OrderPreprocessingMapper extends Mapper<LongWritable, Text, LongWritable, Text>{
    @Override
    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {
        // 每行数据以空格分隔
        String[] records = value.toString().split(" ");
        LongWritable ApplSeqNum = new LongWritable(Long.parseLong(records[7]));
        String TransactionTime = records[12];
        String Side = records[13];

        // 生成输出格式，Key: ApplSeqNum, Value: TransactionTime Side
        context.write(ApplSeqNum, new Text(TransactionTime + " " + Side)) ;

    }
}
