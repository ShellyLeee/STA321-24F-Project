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
import java.net.URI;

public class FixedPathDriver {

    // 固定输入和输出路径
    private static final String ORDER_INPUT_PATH1 = "/data/project/input/am_hq_order_spot.txt";
    private static final String ORDER_INPUT_PATH2 = "/data/project/input/pm_hq_order_spot.txt";
    private static final String TRADE_INPUT_PATH1 = "/data/project/input/am_hq_trade_spot.txt";
    private static final String TRADE_INPUT_PATH2 = "/data/project/input/pm_hq_trade_spot.txt";
    private static final String OUTPUT_BASE_PATH = "/data/project/output";
    private static final String PREPROCESSED_ORDER_PATH = OUTPUT_BASE_PATH + "/Preprocessed_order.txt";
    private static final String PREPROCESSED_TRADE_PATH = OUTPUT_BASE_PATH + "/Preprocessed_trade.txt";
    private static final String JOINED_OUTPUT_PATH = OUTPUT_BASE_PATH + "/Active_trade_order.txt";
    private static final String TEMP_OUTPUT_PATH = OUTPUT_BASE_PATH + "/job1_temp";
    private static final String FINAL_OUTPUT_PATH = OUTPUT_BASE_PATH + "/final";

    public static void main(String[] args) throws Exception {
        // 初始化配置和文件系统
        Configuration conf = new Configuration();

        // 设置任务超时时间为20分钟
        conf.setLong("mapreduce.task.timeout", 1200000);

        // 设置Map任务内存为4GB
        conf.setInt("mapreduce.map.memory.mb", 4096);
        conf.set("mapreduce.map.java.opts", "-Xmx3072m");  // 设置JVM堆内存为3GB

        // 设置YARN容器资源为8GB
        conf.setInt("yarn.nodemanager.resource.memory-mb", 8192);


        FileSystem fs = FileSystem.get(conf);

        // 确保输出路径不存在以避免冲突
        ensurePathDoesNotExist(fs, PREPROCESSED_ORDER_PATH);
        ensurePathDoesNotExist(fs, PREPROCESSED_TRADE_PATH);
        ensurePathDoesNotExist(fs, JOINED_OUTPUT_PATH);
        ensurePathDoesNotExist(fs, TEMP_OUTPUT_PATH);
        ensurePathDoesNotExist(fs, FINAL_OUTPUT_PATH);

        // 执行各阶段作业
        if (!runOrderPreprocessing(conf, ORDER_INPUT_PATH1, ORDER_INPUT_PATH2, PREPROCESSED_ORDER_PATH)) {
            System.err.println("Order preprocessing failed.");
            System.exit(1);
        }

        if (!runTradePreprocessing(conf, TRADE_INPUT_PATH1, TRADE_INPUT_PATH2, PREPROCESSED_TRADE_PATH)) {
            System.err.println("Trade preprocessing failed.");
            System.exit(1);
        }

        if (!runActiveOrderJoin(conf, PREPROCESSED_ORDER_PATH, PREPROCESSED_TRADE_PATH, JOINED_OUTPUT_PATH)) {
            System.err.println("Active order join failed.");
            System.exit(1);
        }

        if (!runStockFlowAnalysis(conf, JOINED_OUTPUT_PATH, TEMP_OUTPUT_PATH, FINAL_OUTPUT_PATH)) {
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

    // Order预处理作业
    private static boolean runOrderPreprocessing(Configuration conf, String inputPath1, String inputPath2, String outputPath) throws Exception {
        System.out.println("Starting Order Preprocessing...");
        Job job = Job.getInstance(conf, "Order Preprocessing");
        job.setJarByClass(FixedPathDriver.class);
        job.setMapperClass(OrderPreprocessingMapper.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        MultipleInputs.addInputPath(job, new Path(inputPath1), TextInputFormat.class, OrderPreprocessingMapper.class);
        MultipleInputs.addInputPath(job, new Path(inputPath2), TextInputFormat.class, OrderPreprocessingMapper.class);
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        boolean success = job.waitForCompletion(true);
        if (success) {
            System.out.println("Order preprocessing completed successfully. Output: " + outputPath);
        }
        return success;
    }

    // Trade预处理作业
    private static boolean runTradePreprocessing(Configuration conf, String inputPath1, String inputPath2, String outputPath) throws Exception {
        System.out.println("Starting Trade Preprocessing...");
        Job job = Job.getInstance(conf, "Trade Preprocessing");
        job.setJarByClass(FixedPathDriver.class);
        job.setMapperClass(TradePreprocessingMapper.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        MultipleInputs.addInputPath(job, new Path(inputPath1), TextInputFormat.class, TradePreprocessingMapper.class);
        MultipleInputs.addInputPath(job, new Path(inputPath2), TextInputFormat.class, TradePreprocessingMapper.class);
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        boolean success = job.waitForCompletion(true);
        if (success) {
            System.out.println("Trade preprocessing completed successfully. Output: " + outputPath);
        }
        return success;
    }

    // Active Order Join作业
    private static boolean runActiveOrderJoin(Configuration conf, String orderInput, String tradeInput, String outputPath) throws Exception {
        System.out.println("Starting Active Order and Trade Join...");
        Job job = Job.getInstance(conf, "Active Order and Trade Join");
        job.setJarByClass(FixedPathDriver.class);
        job.setMapperClass(ActiveOrderJoinMapper.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(orderInput));
        FileInputFormat.addInputPath(job, new Path(tradeInput));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        job.addCacheFile(new URI("/data/project/output/Preprocessed_order.txt/part-r-00000#part-r-00000"));

        boolean success = job.waitForCompletion(true);
        if (success) {
            System.out.println("Active Order and Trade Join completed successfully. Output: " + outputPath);
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
        job.setJarByClass(FixedPathDriver.class);
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
        job.setJarByClass(FixedPathDriver.class);
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
