package driver;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import mapper.StockFlowMapper1;
import mapper.StockFlowMapper2;
import reducer.StockFlowReducer1;
import reducer.StockFlowReducer2;

public class StockFlowDriver {
    public static void main(String[] args) throws Exception {
        // 配置作业
        Configuration conf = new Configuration();

        // 第一个作业 - mapper1 -> reducer1
        Job job1 = Job.getInstance(conf, "Stock Flow Job 1");
        job1.setJarByClass(StockFlowDriver.class);
        job1.setMapperClass(StockFlowMapper1.class);
        job1.setReducerClass(StockFlowReducer1.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(Text.class);

        // 设置输入输出路径
        FileInputFormat.addInputPath(job1, new Path(args[0]));
        FileOutputFormat.setOutputPath(job1, new Path(args[1] + "/temp_output"));

        // 提交第一个作业
        if (!job1.waitForCompletion(true)) {
            System.exit(1);
        }

        // 第二个作业 - mapper2 -> reducer2
        Job job2 = Job.getInstance(conf, "Stock Flow Job 2");
        job2.setJarByClass(StockFlowDriver.class);
        job2.setMapperClass(StockFlowMapper2.class);
        job2.setReducerClass(StockFlowReducer2.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);

        // 设置输入输出路径
        FileInputFormat.addInputPath(job2, new Path(args[1] + "/temp_output"));
        FileOutputFormat.setOutputPath(job2, new Path(args[2]));

        // 提交第二个作业
        System.exit(job2.waitForCompletion(true) ? 0 : 1);
    }
}
