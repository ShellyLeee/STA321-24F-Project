package com.stockvisualization;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        // 读取CSV数据
        String filePath = "data/Final_10min.csv";
        List<DataProcessor.StockData> data = DataProcessor.readCSV(filePath);
        System.out.println(data);

        // 创建HTML文件
        String htmlFilePath = "index.html";
        createHTMLFile(data, htmlFilePath);

        // 自动打开浏览器查看HTML文件
        java.awt.Desktop.getDesktop().browse(new java.net.URI("file://" + new File(htmlFilePath).getAbsolutePath()));
    }

    // 生成HTML文件
    // 生成HTML文件
    public static void createHTMLFile(List<DataProcessor.StockData> data, String htmlFilePath) throws IOException {
        List<String> timeWindows = DataProcessor.getTimeWindows(data);
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n<html lang='en'>\n<head>\n<meta charset='UTF-8'>\n<title>股票数据可视化</title>\n")
                .append("<link rel='stylesheet' type='text/css' href='/Users/shellyli/Documents/GitHub/STA321-24F-Project/STA321-Project/src/main/java/com/stockvisualization/style.css'>\n") // 引入外部CSS文件
                .append("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>\n")
                .append("<script src='/Users/shellyli/Documents/GitHub/STA321-24F-Project/STA321-Project/src/main/java/com/stockvisualization/script.js'></script>\n") // 引入外部JS文件
                .append("</head>\n<body>\n")
                .append("<h1>股票交易数据可视化</h1>\n");

        // 创建选择时间窗口的下拉菜单
        html.append("<label for='timeWindowSelect'>选择时间窗口：</label>")
                .append("<select id='timeWindowSelect' onchange='updateData()'>\n");
        for (String timeWindow : timeWindows) {
            html.append("<option value='").append(timeWindow).append("'>时间窗口 ").append(timeWindow).append("</option>\n");
        }
        html.append("</select>\n");

        // 展示数据部分
        html.append("<div id='dataDisplay'>\n</div>\n");

        // 折线图和饼状图区域
        html.append("<canvas id='lineChart'></canvas>\n")
                .append("<canvas id='pieChart'></canvas>\n")
                .append("<canvas id='barChart'></canvas>\n");

        // 数据传递给 JavaScript 部分
        html.append("<script>\n")
                .append("let data = ").append(dataToJson(data)).append(";\n")
                .append("updateData();\n") // 默认调用 updateData() 更新图表
                .append("</script>\n")
                .append("</body>\n</html>");

        // 写入到HTML文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFilePath))) {
            writer.write(html.toString());
        }
    }


    // 将数据转换为JSON格式
    public static String dataToJson(List<DataProcessor.StockData> data) {
        StringBuilder json = new StringBuilder("[");
        for (DataProcessor.StockData stockData : data) {
            json.append("{")
                    .append("\"主力净流入\":").append(stockData.主力净流入).append(",")
                    .append("\"主力流入\":").append(stockData.主力流入).append(",")
                    .append("\"主力流出\":").append(stockData.主力流出).append(",")
                    .append("\"时间窗口id\":\"").append(stockData.时间窗口id).append("\"")
                    .append("},");
        }
        if (json.length() > 1) {
            json.setLength(json.length() - 1);  // 去掉最后的逗号
        }
        json.append("]");
        return json.toString();
    }
}

