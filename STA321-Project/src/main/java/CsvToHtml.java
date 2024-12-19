import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvToHtml {
    public static void main(String[] args) {
        String csvFile = "data/Final_10min.csv"; // CSV文件路径
        String htmlFile = "data/visualization.html"; // 输出的HTML文件路径
        String jsFile = "data/script.js"; // JavaScript文件路径
        String cssFile = "data/style.css"; // CSS文件路径
        List<String[]> data = readCsv(csvFile);
        generateJs(jsFile, data);
        generateHtml(htmlFile, data); // 将data传递给generateHtml方法
    }

    // 读取CSV文件
    private static List<String[]> readCsv(String csvFile) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(",")); // 使用逗号分隔
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 生成JavaScript文件
    private static void generateJs(String jsFile, List<String[]> data) {
        try (FileWriter writer = new FileWriter(jsFile)) {
            // 折线图数据
            writer.write("const lineLabels = " + getLineLabels(data) + ";\n");
            writer.write("const lineData1 = " + getLineData(data, 0) + ";\n");
            writer.write("const lineData2 = " + getLineData(data, 1) + ";\n");
            writer.write("const lineData3 = " + getLineData(data, 2) + ";\n");
            writer.write("\n");

            // 饼状图和柱状图数据初始化
            writer.write("const data = " + dataToJson(data) + ";\n");
            writer.write("document.addEventListener('DOMContentLoaded', function() {\n");
            writer.write("    const timeWindowSelect = document.getElementById('timeWindowId');\n");
            writer.write("    const updateButton = document.getElementById('updateButton');\n");
            writer.write("    const dataTable = document.getElementById('dataDisplay');\n");
            writer.write("    const ctxPie = document.getElementById('myPieChart').getContext('2d');\n");
            writer.write("    const ctxBar = document.getElementById('myBarChart').getContext('2d');\n");
            writer.write("    const ctxBarSell = document.getElementById('myBarChartSell').getContext('2d');\n");
            writer.write("\n");

            // 折线图初始化
            writer.write("    const myLineChart = new Chart(document.getElementById('myLineChart').getContext('2d'), {\n");
            writer.write("        type: 'line',\n");
            writer.write("        data: {\n");
            writer.write("            labels: lineLabels,\n");
            writer.write("            datasets: [\n");
            writer.write("                { label: '主力净流入', data: lineData1, borderColor: '#FF6384', fill: false },\n");
            writer.write("                { label: '主力流入', data: lineData2, borderColor: '#36A2EB', fill: false },\n");
            writer.write("                { label: '主力流出', data: lineData3, borderColor: '#FFCE56', fill: false }\n");
            writer.write("            ]\n");
            writer.write("        },\n");
            writer.write("        options: {\n");
            writer.write("            responsive: true,\n");
            writer.write("            scales: {\n");
            writer.write("                x: { title: { display: true, text: '时间窗口' } },\n");
            writer.write("                y: { title: { display: true, text: '金额' } }\n");
            writer.write("            }\n");
            writer.write("        }\n");
            writer.write("    });\n");
            writer.write("\n");

            // 更新图表的函数
            writer.write("let myPieChart;\n");
            writer.write("let myBarChart;\n");
            writer.write("let myBarChartSell;\n");
            writer.write("function updateCharts(timeWindowId) {\n");
            writer.write("    const rowData = data[timeWindowId];\n");
            writer.write("    if (rowData) {\n");
            writer.write("        // 更新数据展示\n");
            writer.write("\n");

            writer.write("        const rows = dataTable.getElementsByTagName('tr');\n");
            writer.write("        while (rows.length > 1) { \n");
            writer.write("            dataTable.deleteRow(1); \n");
            writer.write("        }\n");
            writer.write("        const newRow = document.createElement('tr');\n");
            writer.write("        newRow.innerHTML = `<td>${rowData[0]}</td><td>${rowData[1]}</td><td>${rowData[2]}</td>\n");
            writer.write("                            <td>${rowData[3]}</td><td>${rowData[4]}</td>\n");
            writer.write("                            <td>${rowData[5]}</td><td>${rowData[6]}</td>\n");
            writer.write("                            <td>${rowData[7]}</td><td>${rowData[8]}</td>\n");
            writer.write("                            <td>${rowData[9]}</td><td>${rowData[10]}</td>\n");
            writer.write("                            <td>${rowData[11]}</td><td>${rowData[12]}</td>\n");
            writer.write("                            <td>${rowData[13]}</td><td>${rowData[14]}</td>\n");
            writer.write("                            <td>${rowData[15]}</td><td>${rowData[16]}</td>\n");
            writer.write("                            <td>${rowData[17]}</td><td>${rowData[18]}</td><td>${rowData[19]}</td>`;\n");
            writer.write("        dataTable.appendChild(newRow);\n");
            writer.write("\n");

            writer.write("        document.getElementById('mainNetInflow').textContent = rowData[0];\n");
            writer.write("        document.getElementById('mainInflow').textContent = rowData[1];\n");
            writer.write("        document.getElementById('mainOutflow').textContent = rowData[2];\n");
            writer.write("\n");

            writer.write("        // 更新饼状图数据\n");
            writer.write("        const pieData = [parseFloat(rowData[1]), parseFloat(rowData[2])];\n");
            writer.write("        if (myPieChart) {\n");
            writer.write("            myPieChart.destroy(); // 销毁旧的图表\n");
            writer.write("        }\n");
            writer.write("        myPieChart = new Chart(ctxPie, {\n");
            writer.write("            type: 'pie',\n");
            writer.write("            data: {\n");
            writer.write("                labels: ['主力流入', '主力流出'],\n");
            writer.write("                datasets: [{\n");
            writer.write("                    data: pieData,\n");
            writer.write("                    backgroundColor: ['#FF6384', '#36A2EB'],\n");
            writer.write("                }]\n");
            writer.write("            },\n");
            writer.write("            options: {\n");
            writer.write("                responsive: true,\n");
            writer.write("                plugins: {\n");
            writer.write("                    legend: { position: 'top' }\n");
            writer.write("                }\n");
            writer.write("            }\n");
            writer.write("        });\n");
            writer.write("\n");
            writer.write("        // 更新买单柱状图数据\n");
            writer.write("        const barVolumeData = [\n");
            writer.write("            parseFloat(rowData[3]), // 超大买单成交量\n");
            writer.write("            parseFloat(rowData[7]), // 大买单成交量\n");
            writer.write("            parseFloat(rowData[11]), // 中买单成交量\n");
            writer.write("            parseFloat(rowData[15])  // 小买单成交量\n");
            writer.write("        ];\n");
            writer.write("        const barAmountData = [\n");
            writer.write("            parseFloat(rowData[4]), // 超大买单成交额\n");
            writer.write("            parseFloat(rowData[8]), // 大买单成交额\n");
            writer.write("            parseFloat(rowData[12]), // 中买单成交额\n");
            writer.write("            parseFloat(rowData[16])  // 小买单成交额\n");
            writer.write("        ];\n");
            writer.write("        if (myBarChart) {\n");
            writer.write("            myBarChart.destroy(); // 销毁旧的图表\n");
            writer.write("        }\n");
            writer.write("        myBarChart = new Chart(ctxBar, {\n");
            writer.write("            type: 'bar',\n");
            writer.write("            data: {\n");
            writer.write("                labels: ['超大买单', '大买单', '中买单', '小买单'],\n");
            writer.write("                datasets: [\n");
            writer.write("                    { label: '成交量', data: barVolumeData, backgroundColor: '#FF6384' },\n");
            writer.write("                    { label: '成交额', data: barAmountData, backgroundColor: '#36A2EB' }\n");
            writer.write("                ]\n");
            writer.write("            },\n");
            writer.write("            options: {\n");
            writer.write("                responsive: true,\n");
            writer.write("                scales: {\n");
            writer.write("                    x: { title: { display: true, text: '买单类型' } },\n");
            writer.write("                    y: { title: { display: true, text: '金额' } }\n");
            writer.write("                }\n");
            writer.write("            }\n");
            writer.write("        });\n");
            writer.write("\n");
            writer.write("        // 更新卖单柱状图数据\n");
            writer.write("        const barSellVolumeData = [\n");
            writer.write("            parseFloat(rowData[5]), // 超大卖单成交量\n");
            writer.write("            parseFloat(rowData[9]), // 大卖单成交量\n");
            writer.write("            parseFloat(rowData[13]), // 中卖单成交量\n");
            writer.write("            parseFloat(rowData[17])  // 小卖单成交量\n");
            writer.write("        ];\n");
            writer.write("        const barSellAmountData = [\n");
            writer.write("            parseFloat(rowData[6]), // 超大卖单成交额\n");
            writer.write("            parseFloat(rowData[10]), // 大卖单成交额\n");
            writer.write("            parseFloat(rowData[14]), // 中卖单成交额\n");
            writer.write("            parseFloat(rowData[18])  // 小卖单成交额\n");
            writer.write("        ];\n");
            writer.write("        if (myBarChartSell) {\n");
            writer.write("            myBarChartSell.destroy(); // 销毁旧的图表\n");
            writer.write("        }\n");
            writer.write("        myBarChartSell = new Chart(ctxBarSell, {\n");
            writer.write("            type: 'bar',\n");
            writer.write("            data: {\n");
            writer.write("                labels: ['超大卖单', '大卖单', '中卖单', '小卖单'],\n");
            writer.write("                datasets: [\n");
            writer.write("                    { label: '成交量', data: barSellVolumeData, backgroundColor: '#FF6384' },\n");
            writer.write("                    { label: '成交额', data: barSellAmountData, backgroundColor: '#36A2EB' }\n");
            writer.write("                ]\n");
            writer.write("            },\n");
            writer.write("            options: {\n");
            writer.write("                responsive: true,\n");
            writer.write("                scales: {\n");
            writer.write("                    x: { title: { display: true, text: '卖单类型' } },\n");
            writer.write("                    y: { title: { display: true, text: '金额' } }\n");
            writer.write("                }\n");
            writer.write("            }\n");
            writer.write("        });\n");
            writer.write("    } else {\n");
            writer.write("        alert('未找到对应的时间窗口数据！');\n");
            writer.write("    }\n");
            writer.write("}\n");
            writer.write("\n");
            writer.write("    // 填充下拉框\n");
            writer.write("    data.forEach((row, index) => {\n");
            writer.write("        const option = document.createElement('option');\n");
            writer.write("        option.value = index;\n");
            writer.write("        option.textContent = '时间窗口 ' + index;\n");
            writer.write("        timeWindowSelect.appendChild(option);\n");
            writer.write("    });\n");
            writer.write("\n");
            writer.write("    updateButton.addEventListener('click', function() {\n");
            writer.write("        const timeWindowId = parseInt(timeWindowSelect.value);\n");
            writer.write("        updateCharts(timeWindowId);\n");
            writer.write("    });\n");
            writer.write("});\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取折线图标签
    private static String getLineLabels(List<String[]> data) {
        StringBuilder labels = new StringBuilder("[");
        for (int i = 1; i < data.size(); i++) {
            labels.append("\"").append(data.get(i)[19]).append("\""); // 假设时间窗口在第19列
            if (i < data.size() - 1) {
                labels.append(",");
            }
        }
        labels.append("]");
        return labels.toString();
    }

    // 获取折线图数据
    private static String getLineData(List<String[]> data, int index) {
        StringBuilder lineData = new StringBuilder("[");
        for (int i = 1; i < data.size(); i++) {
            lineData.append(data.get(i)[index]); // 假设主力净流入在第index列
            if (i < data.size() - 1) {
                lineData.append(",");
            }
        }
        lineData.append("]");
        return lineData.toString();
    }

    // 将数据转换为JSON格式
    private static String dataToJson(List<String[]> data) {
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < data.size(); i++) {
            jsonBuilder.append("[");
            for (int j = 0; j < data.get(i).length; j++) {
                jsonBuilder.append("\"").append(data.get(i)[j]).append("\"");
                if (j < data.get(i).length - 1) {
                    jsonBuilder.append(",");
                }
            }
            jsonBuilder.append("]");
            if (i < data.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    // 生成HTML文件
    private static void generateHtml(String htmlFile, List<String[]> data) {
        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html lang=\"en\">\n");
            writer.write("<head>\n");
            writer.write("    <meta charset=\"UTF-8\">\n");
            writer.write("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
            writer.write("    <title>股票交易数据可视化</title>\n");
            writer.write("    <link rel=\"stylesheet\" href=\"style.css\">\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write("    <h1>股票交易数据可视化</h1>\n");

            // 添加全天股票数据展示、下拉框和按钮
            writer.write("    <div class=\"chart-container\">\n");
            writer.write("        <div class=\"chart\">\n");
            writer.write("            <h2>全天股票数据折线图</h2>\n");
            writer.write("            <canvas id=\"myLineChart\"></canvas>\n");
            writer.write("        </div>\n");
            writer.write("        <div class=\"data-display\">\n");
            writer.write("            <h2>选择时间窗口</h2>\n");
            writer.write("            <label for=\"timeWindowId\">选择时间窗口 ID:</label>\n");
            writer.write("            <select id=\"timeWindowId\"></select>\n");
            writer.write("            <button id=\"updateButton\">更新数据</button>\n");
            writer.write("        </div>\n");
            writer.write("    </div>\n");

            // 数据展示
            writer.write("    <h2>数据展示</h2>\n");
            writer.write("    <table class=\"data-table\" id=\"dataDisplay\">\n");
            writer.write("        <tr>\n");
            writer.write("            <th>主力净流入</th>\n");
            writer.write("            <th>主力流入</th>\n");
            writer.write("            <th>主力流出</th>\n");
            writer.write("            <th>超大买单成交量</th>\n");
            writer.write("            <th>超大买单成交额</th>\n");
            writer.write("            <th>超大卖单成交量</th>\n");
            writer.write("            <th>超大卖单成交额</th>\n");
            writer.write("            <th>大买单成交量</th>\n");
            writer.write("            <th>大买单成交额</th>\n");
            writer.write("            <th>大卖单成交量</th>\n");
            writer.write("            <th>大卖单成交额</th>\n");
            writer.write("            <th>中买单成交量</th>\n");
            writer.write("            <th>中买单成交额</th>\n");
            writer.write("            <th>中卖单成交量</th>\n");
            writer.write("            <th>中卖单成交额</th>\n");
            writer.write("            <th>小买单成交量</th>\n");
            writer.write("            <th>小买单成交额</th>\n");
            writer.write("            <th>小卖单成交量</th>\n");
            writer.write("            <th>小卖单成交额</th>\n");
            writer.write("            <th>时间窗口id</th>\n");
            writer.write("        </tr>\n");
            writer.write("    </table>\n");

            // 图表展示
            writer.write("    <div class=\"chart-container\">\n");
            writer.write("        <div class=\"data-display\">\n");
            writer.write("            <h3 style=\"font-size: 36px; color: black;\">主力净流入: <span id=\"mainNetInflow\"></span></h3>\n");
            writer.write("            <h4 style=\"font-size: 24px; color: red;\">主力流入: <span id=\"mainInflow\"></span></h4>\n");
            writer.write("            <h4 style=\"font-size: 24px; color: green;\">主力流出: <span id=\"mainOutflow\"></span></h4>\n");
            writer.write("        </div>\n");
            writer.write("        <div class=\"chart\">\n");
            writer.write("    <h2>饼状图</h2>\n");
            writer.write("            <canvas id=\"myPieChart\"></canvas>\n");
            writer.write("        </div>\n");
            writer.write("    </div>\n");

            // 买单和卖单柱状图并排展示
            writer.write("    <h2>买单和卖单柱状图</h2>\n");
            writer.write("    <div class=\"chart-container\">\n");
            writer.write("        <div class=\"chart\">\n");
            writer.write("            <canvas id=\"myBarChart\"></canvas>\n");
            writer.write("        </div>\n");
            writer.write("        <div class=\"chart\">\n");
            writer.write("            <canvas id=\"myBarChartSell\"></canvas>\n");
            writer.write("        </div>\n");
            writer.write("    </div>\n");

            writer.write("    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n");
            writer.write("    <script src=\"script.js\"></script>\n");
            writer.write("</body>\n");
            writer.write("</html>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}