package driver;

import mapper.FinalMapper;
import mapper.FinalTradePreprocessingMapper;
import org.apache.hadoop.io.LongWritable;
import reducer.FinalReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FinalDriver0 {

    private static final String TRADE_INPUT_PATH1 = "/data/project/input/am_hq_trade_spot.txt";
    private static final String TRADE_INPUT_PATH2 = "/data/project/input/pm_hq_trade_spot.txt";
    private static final String OUTPUT_BASE_PATH = "/data/project/output";

    public static void main(String[] args) throws Exception {
        // 配置作业
        Configuration conf = new Configuration();
        // MapReduce 配置
        conf.setLong("mapreduce.task.timeout", 600000); // 超时10分钟
        conf.setInt("mapreduce.map.memory.mb", 3072);  // Mapper内存3GB
        conf.set("mapreduce.map.java.opts", "-Xmx2048m"); // JVM堆内存2GB
        conf.setInt("mapreduce.reduce.memory.mb", 4096);  // Reducer内存4GB
        conf.set("mapreduce.reduce.java.opts", "-Xmx3072m"); // JVM堆内存3GB

// 启用压缩
        conf.setBoolean("mapreduce.map.output.compress", true);
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");

// 分片大小
        conf.set("mapreduce.input.fileinputformat.split.maxsize", "134217728"); // 每个split最大128MB
        conf.set("mapreduce.input.fileinputformat.split.minsize", "67108864");  // 每个split最小64MB

// 提高数据本地化概率
        conf.setInt("mapreduce.input.fileinputformat.split.minsize.per.node", 67108864); // 单节点最小64MB
        conf.setInt("mapreduce.input.fileinputformat.split.minsize.per.rack", 134217728); // 单机架最小128MB

// JVM重用
        conf.setInt("mapreduce.job.jvm.numtasks", -1);

// YARN 容器资源配置
        conf.setInt("yarn.nodemanager.resource.memory-mb", 12288); // YARN容器最大12GB
        conf.setInt("yarn.scheduler.maximum-allocation-mb", 12288); // YARN最大分配12GB

        FileSystem fs = FileSystem.get(conf);

        // 检查输出路径是否存在，如果存在则删除
        Path outputPath = new Path(OUTPUT_BASE_PATH);
        if (fs.exists(outputPath)) {
            System.out.println("Output path exists. Deleting: " + OUTPUT_BASE_PATH);
            fs.delete(outputPath, true);
        }

        Job job = Job.getInstance(conf, "Final Trade Analysis");
        job.setJarByClass(FinalDriver0.class);

        // 设置 Mapper
        MultipleInputs.addInputPath(job, new Path(TRADE_INPUT_PATH1), TextInputFormat.class, FinalMapper.class);
        MultipleInputs.addInputPath(job, new Path(TRADE_INPUT_PATH2), TextInputFormat.class, FinalMapper.class);
        job.setMapOutputKeyClass(LongWritable.class);  // Mapper的输出Key类型
        job.setMapOutputValueClass(Text.class);        // Mapper的输出Value类型

        // 设置 Reducer
        job.setReducerClass(FinalReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // 设置输出路径
        FileOutputFormat.setOutputPath(job, outputPath);

        // 提交作业
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
