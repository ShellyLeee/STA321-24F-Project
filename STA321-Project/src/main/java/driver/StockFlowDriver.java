package driver;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import mapper.StockFlowMapper1;
import mapper.StockFlowMapper2;
import reducer.StockFlowReducer1;
import reducer.StockFlowReducer2;

import java.io.IOException;

public class StockFlowDriver {
    public static void main(String[] args) throws Exception {

        // 初始化配置和文件系统
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        // 定义路径
        Path inputFilePath = new Path("/data/project/output/Active_trade_order.txt");
        Path tempOutputPath = new Path( "/data/project/output/job1_temp");
        Path finalOutputPath = new Path("/data/project/output/final");

        // 清理临时和最终输出目录
        deletePathIfExists(fs, tempOutputPath);
        deletePathIfExists(fs, finalOutputPath);

        // 启动 Job1
        if (!runJob1(conf, inputFilePath, tempOutputPath)) {
            System.err.println("Job 1 failed!");
            System.exit(1);
        }

        // 启动 Job2
        if (!runJob2(conf, tempOutputPath, finalOutputPath)) {
            System.err.println("Job 2 failed!");
            System.exit(1);
        }

        // 清理 Job1 的临时输出目录
        //deletePathIfExists(fs, tempOutputPath);

        System.out.println("All jobs completed successfully!");
        System.exit(0);
    }

    // 删除已存在的路径
    private static void deletePathIfExists(FileSystem fs, Path path) throws Exception {
        if (fs.exists(path)) {
            System.out.println("Deleting existing path: " + path.toString());
            if (!fs.delete(path, true)) {
                throw new IOException("Failed to delete path: " + path);
            }
            // 检查是否删除成功
            if (fs.exists(path)) {
                throw new IOException("Path still exists after deletion: " + path);
            }
        }
    }

    // 运行 Job1
    private static boolean runJob1(Configuration conf, Path inputFilePath, Path tempOutputPath) throws Exception {
        System.out.println("Starting Stock Flow Job 1...");
        Job job1 = Job.getInstance(conf, "Stock Flow Job 1");
        job1.setJarByClass(StockFlowDriver.class);
        job1.setMapperClass(StockFlowMapper1.class);
        job1.setReducerClass(StockFlowReducer1.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job1, inputFilePath);
        FileOutputFormat.setOutputPath(job1, tempOutputPath);

        boolean success = job1.waitForCompletion(true);
        if (success) {
            System.out.println("Job 1 completed successfully.");
        }
        return success;
    }

    // 运行 Job2
    private static boolean runJob2(Configuration conf, Path tempOutputPath, Path finalOutputPath) throws Exception {
        System.out.println("Starting Stock Flow Job 2...");
        System.out.println("Checking and deleting existing output path for Job2: " + finalOutputPath.toString());

        Job job2 = Job.getInstance(conf, "Stock Flow Job 2");
        job2.setJarByClass(StockFlowDriver.class);
        job2.setMapperClass(StockFlowMapper2.class);
        job2.setReducerClass(StockFlowReducer2.class);

        // 更新 Key 和 Value 类型
        job2.setOutputKeyClass(IntWritable.class); // 更新为 IntWritable 类型
        job2.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job2, tempOutputPath);
        FileOutputFormat.setOutputPath(job2, finalOutputPath);

        boolean success = job2.waitForCompletion(true);
        if (success) {
            System.out.println("Job 2 completed successfully.");
        }
        return success;
    }
}
