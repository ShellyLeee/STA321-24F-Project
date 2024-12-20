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

public class FinalDriver {

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
        job.setJarByClass(FinalDriver.class);

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
