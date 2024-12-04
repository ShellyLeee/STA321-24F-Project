package driver;

import mapper.OrderPreprocessingMapper;
import mapper.TradePreprocessingMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PreprocessingDriver {

    public static void main(String[] args) throws Exception {

        String orderInputPath = "/data/project/input/am_hq_order_spot.txt";
        String tradeInputPath = "/data/project/input/am_hq_trade_spot.txt";
        String outputPath = "/data/project/output";

        // 处理 Order 数据的 Job 配置
        Configuration confOrder = new Configuration();
        Job orderJob = Job.getInstance(confOrder, "Order Preprocessing");

        orderJob.setJarByClass(PreprocessingDriver.class);
        orderJob.setMapperClass(OrderPreprocessingMapper.class);
        orderJob.setMapOutputKeyClass(Text.class);
        orderJob.setMapOutputValueClass(Text.class);

        FileInputFormat.addInputPath(orderJob, new Path(orderInputPath));
        FileOutputFormat.setOutputPath(orderJob, new Path(outputPath + "/Preprocessed_order.txt"));

        // 提交 Order 处理作业
        if (!orderJob.waitForCompletion(true)) {
            System.exit(1);
        }

        // 处理 Trade 数据的 Job 配置
        Configuration confTrade = new Configuration();
        Job tradeJob = Job.getInstance(confTrade, "Trade Preprocessing");

        tradeJob.setJarByClass(PreprocessingDriver.class);
        tradeJob.setMapperClass(TradePreprocessingMapper.class);
        tradeJob.setMapOutputKeyClass(Text.class);
        tradeJob.setMapOutputValueClass(Text.class);

        FileInputFormat.addInputPath(tradeJob, new Path(tradeInputPath));
        FileOutputFormat.setOutputPath(tradeJob, new Path(outputPath + "/Preprocessed_trade.txt"));

        // 提交 Trade 处理作业
        System.exit(tradeJob.waitForCompletion(true) ? 0 : 1);
    }
}

