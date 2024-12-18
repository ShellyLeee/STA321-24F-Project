package com.stockvisualization;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.*;

public class DataProcessor {

    // 存储每一行的股票交易数据
    public static class StockData {
        public double 主力净流入;
        public double 主力流入;
        public double 主力流出;
        public double 超大买单成交量;
        public double 超大买单成交额;
        public double 超大卖单成交量;
        public double 超大卖单成交额;
        public double 大买单成交量;
        public double 大买单成交额;
        public double 大卖单成交量;
        public double 大卖单成交额;
        public double 中买单成交量;
        public double 中买单成交额;
        public double 中卖单成交量;
        public double 中卖单成交额;
        public double 小买单成交量;
        public double 小买单成交额;
        public double 小卖单成交量;
        public double 小卖单成交额;
        public String 时间窗口id;

        // 解析每一行数据
        public StockData(String[] line) {
            this.主力净流入 = Double.parseDouble(line[0]);
            this.主力流入 = Double.parseDouble(line[1]);
            this.主力流出 = Double.parseDouble(line[2]);
            this.超大买单成交量 = Double.parseDouble(line[3]);
            this.超大买单成交额 = Double.parseDouble(line[4]);
            this.超大卖单成交量 = Double.parseDouble(line[5]);
            this.超大卖单成交额 = Double.parseDouble(line[6]);
            this.大买单成交量 = Double.parseDouble(line[7]);
            this.大买单成交额 = Double.parseDouble(line[8]);
            this.大卖单成交量 = Double.parseDouble(line[9]);
            this.大卖单成交额 = Double.parseDouble(line[10]);
            this.中买单成交量 = Double.parseDouble(line[11]);
            this.中买单成交额 = Double.parseDouble(line[12]);
            this.中卖单成交量 = Double.parseDouble(line[13]);
            this.中卖单成交额 = Double.parseDouble(line[14]);
            this.小买单成交量 = Double.parseDouble(line[15]);
            this.小买单成交额 = Double.parseDouble(line[16]);
            this.小卖单成交量 = Double.parseDouble(line[17]);
            this.小卖单成交额 = Double.parseDouble(line[18]);
            this.时间窗口id = line[19];
        }
    }

    // 读取CSV文件
    public static List<StockData> readCSV(String filePath) throws IOException {
        List<StockData> dataList = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            reader.readNext(); // 跳过表头
            while ((line = reader.readNext()) != null) {
                dataList.add(new StockData(line));
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return dataList;
    }

    // 获取所有的时间窗口id
    public static List<String> getTimeWindows(List<StockData> data) {
        Set<String> timeWindows = new HashSet<>();
        for (StockData stockData : data) {
            timeWindows.add(stockData.时间窗口id);
        }
        return new ArrayList<>(timeWindows);
    }

    // 获取特定时间窗口的数据
    public static List<StockData> getDataByTimeWindow(List<StockData> data, String timeWindowId) {
        List<StockData> filteredData = new ArrayList<>();
        for (StockData stockData : data) {
            if (stockData.时间窗口id.equals(timeWindowId)) {
                filteredData.add(stockData);
            }
        }
        return filteredData;
    }
}

