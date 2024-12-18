package driver;

import mapper.FinalTradePreprocessingMapper;
import mapper.TradePreprocessMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FinalTradePreprocessingDriver {

    public static void main(String[] args) throws Exception {
        // 初始化 Hadoop 配置
        Configuration conf = new Configuration();

        // ------------------- 参数优化 -------------------
        conf.set("mapreduce.map.memory.mb", "3072"); // Mapper 3GB 内存
        conf.set("mapreduce.map.java.opts", "-Xmx2457m"); // Mapper JVM 最大堆内存
        conf.set("mapreduce.input.fileinputformat.split.minsize", "268435456"); // 256MB 块大小
        conf.set("mapreduce.map.output.compress", "true");
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");

        // 固定输入路径和输出路径
        String inputPath1 = "/data/project/input/am_hq_trade_spot.txt";
        String inputPath2 = "/data/project/input/pm_hq_trade_spot.txt";
        String outputPath = "/output/project";

        // 删除旧的输出路径（如果存在）
        deleteOutputPathIfExists(conf, outputPath);

        // ------------------- 初始化 MapReduce Job -------------------
        Job job = Job.getInstance(conf, "Trade Preprocessing Job - Multi Input");
        job.setJarByClass(FinalTradePreprocessingDriver.class);

        // 设置 Mapper 类
        job.setMapperClass(FinalTradePreprocessingMapper.class);

        // 设置输出 Key 和 Value 类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        // 使用 CombineTextInputFormat 来合并小文件
        job.setInputFormatClass(CombineTextInputFormat.class);
        CombineTextInputFormat.setMaxInputSplitSize(job, 256 * 1024 * 1024); // 最大256MB 分片

        // Job 无需 Reducer，直接输出
        job.setNumReduceTasks(0);

        // 设置输入路径（多个文件）
        FileInputFormat.addInputPath(job, new Path(inputPath1));
        FileInputFormat.addInputPath(job, new Path(inputPath2));

        // 设置输出路径
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        // 提交 Job 并等待完成
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    /**
     * 删除旧的输出路径（如果存在）
     */
    private static void deleteOutputPathIfExists(Configuration conf, String outputPath) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path(outputPath);
        if (fs.exists(path)) {
            System.out.println("Output path already exists. Deleting: " + outputPath);
            fs.delete(path, true);
        }
    }
}
