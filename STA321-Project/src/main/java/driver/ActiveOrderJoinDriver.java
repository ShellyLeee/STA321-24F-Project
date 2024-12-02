package driver;

import mapper.ActiveOrderJoinMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ActiveOrderJoinDriver {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: ActiveOrderJoinDriver <order input path> <trade input path> <output path>");
            System.exit(-1);
        }

        String orderInputPath = args[0];  // project/output/Preprocessed_order.txt
        String tradeInputPath = args[1];  // project/output/Preprocessed_trade.txt
        String outputPath = args[2];      // project/output/Active_trade_order.txt

        // 创建Hadoop Job配置
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Active Order and Trade Join");

        // 设置输入输出格式和类
        job.setJarByClass(ActiveOrderJoinDriver.class);

        // 设置Mapper
        job.setMapperClass(ActiveOrderJoinMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        // 设置输入和输出路径
        FileInputFormat.addInputPath(job, new Path(orderInputPath));
        FileInputFormat.addInputPath(job, new Path(tradeInputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        // 提交作业
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}


