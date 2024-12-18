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

public class FinalDriver0 {

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


        // 设置任务超时时间为20分钟
        //conf.setLong("mapreduce.task.timeout", 1200000);

        // 设置Map任务内存为4GB
        //conf.setInt("mapreduce.map.memory.mb", 4096);
        //conf.set("mapreduce.map.java.opts", "-Xmx3072m");  // 设置JVM堆内存为3GB

        // 设置YARN容器资源为8GB
        //conf.setInt("yarn.nodemanager.resource.memory-mb", 8192);
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

    // Trade预处理作业
    private static boolean runTradePreprocessing(Configuration conf, String inputPath1, String inputPath2, String outputPath) throws Exception {
        System.out.println("Starting Trade Preprocessing...");
        Job job = Job.getInstance(conf, "Trade Preprocessing");
        job.setJarByClass(FinalDriver0.class);
        job.setMapperClass(FinalTradePreprocessingMapper.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        MultipleInputs.addInputPath(job, new Path(inputPath1), TextInputFormat.class, FinalTradePreprocessingMapper.class);
        MultipleInputs.addInputPath(job, new Path(inputPath2), TextInputFormat.class, FinalTradePreprocessingMapper.class);
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

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
        job.setJarByClass(FinalDriver0.class);
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
        job.setJarByClass(FinalDriver0.class);
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
