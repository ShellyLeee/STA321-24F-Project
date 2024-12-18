package driver;

import mapper.*;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import reducer.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FinalDriverUpdated {

    // 固定输入和输出路径
    private static final String TRADE_INPUT_PATH1 = "/data/project/input/am_hq_trade_spot.txt";
    private static final String TRADE_INPUT_PATH2 = "/data/project/input/pm_hq_trade_spot.txt";
    private static final String OUTPUT_BASE_PATH = "/data/project/output";
    private static final String TRADE_OUTPUT_PATH = OUTPUT_BASE_PATH + "/Active_trade_order.txt";
    private static final String TEMP_OUTPUT_PATH = OUTPUT_BASE_PATH + "/job1_temp";
    private static final String FINAL_OUTPUT_PATH = OUTPUT_BASE_PATH + "/final";

    public static void main(String[] args) throws Exception {
        // 初始化配置和文件系统
        Configuration conf = new Configuration();

        // 配置优化
        conf.setLong("mapreduce.task.timeout", 1800000); // 设置任务超时时间为30分钟
        conf.setInt("mapreduce.map.memory.mb", 6144); // 增加Map任务内存为6GB
        conf.set("mapreduce.map.java.opts", "-Xmx5120m"); // 设置JVM堆内存为5GB
        conf.setInt("yarn.nodemanager.resource.memory-mb", 12288); // 设置YARN容器资源为12GB
        conf.set("mapreduce.input.fileinputformat.split.minsize", "134217728"); // 最小128MB分片
        conf.set("mapreduce.input.fileinputformat.split.maxsize", "268435456"); // 最大256MB分片
        conf.setBoolean("mapreduce.map.speculative", true); // 启用推测执行

        // 启用详细日志记录
        conf.set("mapreduce.map.log.level", "DEBUG");

        FileSystem fs = FileSystem.get(conf);

        // 确保输出路径不存在以避免冲突
        ensurePathDoesNotExist(fs, TRADE_OUTPUT_PATH);
        ensurePathDoesNotExist(fs, TEMP_OUTPUT_PATH);
        ensurePathDoesNotExist(fs, FINAL_OUTPUT_PATH);

        // 执行各阶段作业
        if (!runTradePreprocessing(conf, TRADE_INPUT_PATH1, TRADE_INPUT_PATH2, TRADE_OUTPUT_PATH)) {
            System.err.println("Trade preprocessing failed.");
            System.exit(1);
        }

        if (!runStockFlowAnalysis(conf, TRADE_OUTPUT_PATH, TEMP_OUTPUT_PATH, FINAL_OUTPUT_PATH)) {
            System.err.println("Stock flow analysis failed.");
            System.exit(1);
        }

        System.out.println("All jobs completed successfully!");
        System.exit(0);
    }

    // 确保路径不存在（若存在则删除旧的文件）
    private static void ensurePathDoesNotExist(FileSystem fs, String pathStr) throws Exception {
        Path path = new Path(pathStr);
        if (fs.exists(path)) {
            System.out.println("Deleting existing path: " + pathStr);
            if (!fs.delete(path, true)) {
                throw new IOException("Failed to delete path: " + pathStr);
            }
        }
    }

    // 修改后的 Trade预处理作业
    private static boolean runTradePreprocessing(Configuration conf, String inputPath1, String inputPath2, String outputPath) throws Exception {
        System.out.println("Starting Trade Preprocessing...");
        Job job = Job.getInstance(conf, "Trade Preprocessing");
        job.setJarByClass(FinalDriverUpdated.class);

        // 添加不同的 Mapper，用于不同的输入路径
        MultipleInputs.addInputPath(job, new Path(inputPath1), TextInputFormat.class, FinalTradePreprocessingMapper.class); // AM 数据
        MultipleInputs.addInputPath(job, new Path(inputPath2), TextInputFormat.class, FinalTradePreprocessingMapper.class); // PM 数据

        // 输出配置
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        // 启动任务并检查结果
        boolean success = job.waitForCompletion(true);
        if (success) {
            System.out.println("Trade preprocessing completed successfully. Output: " + outputPath);
        }
        return success;
    }

    // Stock Flow分析作业
    private static boolean runStockFlowAnalysis(Configuration conf, String inputPath, String tempOutputPath, String finalOutputPath) throws Exception {
        System.out.println("Starting Stock Flow Analysis...");
        if (!runJob1(conf, inputPath, tempOutputPath)) {
            System.err.println("Stock Flow Job 1 failed.");
            return false;
        }

        if (!runJob2(conf, tempOutputPath, finalOutputPath)) {
            System.err.println("Stock Flow Job 2 failed.");
            return false;
        }
        return true;
    }

    // Stock Flow Job 1
    private static boolean runJob1(Configuration conf, String inputPath, String tempOutputPath) throws Exception {
        System.out.println("Starting Stock Flow Job 1...");
        Job job = Job.getInstance(conf, "Stock Flow Job 1");
        job.setJarByClass(FinalDriverUpdated.class);
        job.setMapperClass(StockFlowMapper1.class);
        job.setReducerClass(StockFlowReducer1.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(tempOutputPath));

        boolean success = job.waitForCompletion(true);
        if (success) {
            System.out.println("Stock Flow Job 1 completed successfully. Output: " + tempOutputPath);
        }
        return success;
    }

    // Stock Flow Job 2
    private static boolean runJob2(Configuration conf, String tempOutputPath, String finalOutputPath) throws Exception {
        System.out.println("Starting Stock Flow Job 2...");
        Job job = Job.getInstance(conf, "Stock Flow Job 2");
        job.setJarByClass(FinalDriverUpdated.class);
        job.setMapperClass(StockFlowMapper2.class);
        job.setReducerClass(StockFlowReducer2.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(tempOutputPath));
        FileOutputFormat.setOutputPath(job, new Path(finalOutputPath));

        boolean success = job.waitForCompletion(true);
        if (success) {
            System.out.println("Stock Flow Job 2 completed successfully. Output: " + finalOutputPath);
        }
        return success;
    }
}
