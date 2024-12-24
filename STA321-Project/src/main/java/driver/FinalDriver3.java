/**
 * FinalDriver.java
 *
 * FinalDriver 类是整个 Hadoop MapReduce 作业的驱动程序，负责配置作业、设置输入输出路径、并提交作业。
 *
 * 作者: 欧炜娟
 * 功能: 该类实现了最终的交易分析流程，配置了输入路径和输出路径，并将 Mapper 和 Reducer 类链接到作业中。
 *      它支持从两个输入文件（上午和下午的交易数据）进行多输入处理，并输出计算结果。
 * 实现方式:
 * - 配置作业并初始化 Hadoop 作业对象。
 * - 使用 `MultipleInputs` 设置多个输入路径。
 * - 配置 `FinalMapper` 作为 Mapper 类来处理输入数据。
 * - 配置 `FinalReducer` 作为 Reducer 类来聚合计算结果。
 * - 最后，提交作业并等待执行结果。
 */

package driver;

import mapper.FinalMapper3;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import reducer.FinalReducer3;


public class FinalDriver3 {

    private static final String TRADE_INPUT_PATH1 = "/data/project/input/am_hq_trade_spot.txt";
    private static final String TRADE_INPUT_PATH2 = "/data/project/input/pm_hq_trade_spot.txt";
    private static final String OUTPUT_BASE_PATH = "/data/project/output";

    public static void main(String[] args) throws Exception {
        // 配置作业
        Configuration conf = new Configuration();

        FileSystem fs = FileSystem.get(conf);

        // 检查输出路径是否存在，如果存在则删除
        Path outputPath = new Path(OUTPUT_BASE_PATH);
        if (fs.exists(outputPath)) {
            System.out.println("Output path exists. Deleting: " + OUTPUT_BASE_PATH);
            fs.delete(outputPath, true);
        }

        Job job = Job.getInstance(conf, "Final Trade Analysis");
        job.setJarByClass(FinalDriver3.class);

        // 设置输出格式的分隔符，例如设置为逗号
        job.getConfiguration().set("mapreduce.output.textoutputformat.separator", ","); // 修改为逗号分隔

        // 设置 Mapper
        MultipleInputs.addInputPath(job, new Path(TRADE_INPUT_PATH1), TextInputFormat.class, FinalMapper3.class);
        MultipleInputs.addInputPath(job, new Path(TRADE_INPUT_PATH2), TextInputFormat.class, FinalMapper3.class);
        job.setMapOutputKeyClass(LongWritable.class);  // Mapper的输出Key类型
        job.setMapOutputValueClass(Text.class);        // Mapper的输出Value类型

        // 设置 Reducer
        job.setReducerClass(FinalReducer3.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // 设置输出路径
        FileOutputFormat.setOutputPath(job, outputPath);

        // 提交作业
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
