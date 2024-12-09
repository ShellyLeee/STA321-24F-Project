package driver;

import mapper.ActiveOrderJoinMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.net.URI;

public class ActiveOrderJoinDriver {

    public static void main(String[] args) throws Exception {

        String orderInputPath = "/data/project/output/Preprocessed_order.txt";
        String tradeInputPath = "/data/project/output/Preprocessed_trade.txt";
        String outputPath = "/data/project/output/Active_trade_order.txt";

        // 创建Hadoop Job配置
        Configuration conf = new Configuration();

        // 设置任务超时时间为20分钟
        conf.setLong("mapreduce.task.timeout", 1200000);

        // 设置Map任务内存为4GB
        conf.setInt("mapreduce.map.memory.mb", 4096);
        conf.set("mapreduce.map.java.opts", "-Xmx3072m");  // 设置JVM堆内存为3GB

        // 设置YARN容器资源为8GB
        conf.setInt("yarn.nodemanager.resource.memory-mb", 8192);

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

        job.addCacheFile(new URI("/data/project/output/Preprocessed_order.txt/part-r-00000#part-r-00000"));


        // 提交作业
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}


