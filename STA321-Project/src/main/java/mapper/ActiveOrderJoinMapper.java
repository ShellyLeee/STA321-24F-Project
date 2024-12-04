package mapper;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ActiveOrderJoinMapper extends Mapper<LongWritable, Text, Text, Text> {

    private Map<String, String> orderData = new HashMap<>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // 从 DistributedCache 获取 Preprocessed_order.txt 文件
        Path[] cacheFiles = context.getLocalCacheFiles();

        if (cacheFiles != null && cacheFiles.length > 0) {
            // 假设你的文件是 part-r-00000
            Path file = cacheFiles[0];
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.toString())));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\s+");
                String channelNo = fields[0];
                String applSeqNum = fields[1];
                String transactionTime = fields[2];

                // 使用 ChannelNo 和 ApplSeqNum 作为唯一标识符
                orderData.put(channelNo + "-" + applSeqNum, transactionTime);
            }

            reader.close();
        } else {
            System.err.println("Distributed cache file not found or not loaded.");
        }
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] records = value.toString().split("\\s+");

        LongWritable ChannelNo = new LongWritable(Long.parseLong(records[0]));
        LongWritable ApplSeqNum = new LongWritable(Long.parseLong(records[1]));

        String BidApplSeqNum = records[2];
        String OfferApplSeqNum = records[3];

        // 拼接键值，ChannelNo + ApplSeqNum
        String keyStr = ChannelNo.toString() + "-" + ApplSeqNum.toString();

        // 根据 BidApplSeqNum 查找对应的 TransactionTime
        String bidTransactionTime = orderData.get(ChannelNo.toString() + "-" + BidApplSeqNum);
        // 根据 OfferApplSeqNum 查找对应的 TransactionTime
        String offerTransactionTime = orderData.get(ChannelNo.toString() + "-" + OfferApplSeqNum);

        // 输出日志查看是否有找到对应的时间数据
        System.out.println("Bid Transaction Time: " + bidTransactionTime + ", Offer Transaction Time: " + offerTransactionTime);

        // 判断 BidApplSeqNum 和 OfferApplSeqNum 对应的时间，来决定是主动买单还是主动卖单
        if (bidTransactionTime != null && offerTransactionTime != null) {
            // 比较 Bid 和 Offer 的 TransactionTime 来判断主动单类型
            int compareResult = bidTransactionTime.compareTo(offerTransactionTime);
            int TradeType = (compareResult > 0) ? 1 : 2; // 1 为主动买，2 为主动卖

            // 输出结果（加入 TradeType 字段）
            context.write(new Text(keyStr), new Text(value.toString() + " " + TradeType));

        }
    }
}

