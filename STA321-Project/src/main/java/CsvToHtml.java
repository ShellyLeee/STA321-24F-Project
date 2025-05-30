/**
 * CsvToHtml.java
 *
 * 作者: 李怡萱
 * 功能: 该程序用于读取CSV文件中的股票数据并生成对应的JavaScript文件，以便在网页中进行资金流向可视化。
 *      主要通过读取CSV文件并将数据格式化后，生成适用于HTML页面的JavaScript代码，支持动态更新图表数据。
 * 实现方式:
 * 1. 使用`BufferedReader`读取CSV文件，将每行数据分割为数组并存储。
 * 2. 通过`FileWriter`生成JavaScript文件，配置图表的初始化以及更新功能。
 * 3. 使用`Chart.js`库在网页中绘制折线图、饼状图和柱状图，展示不同类型的资金流向数据。
 *
 * 数据读取过程:
 * 1. 从CSV文件中读取数据，每一行对应一个时间窗口的股票数据。
 * 2. 根据数据生成适用于JavaScript的数据格式，并将其写入文件。
 * 3. JavaScript文件会在网页中动态更新图表，通过点击按钮或选择时间窗口来更新显示的内容。
 *
 * 注意事项:
 * 1. CSV文件的数据格式必须符合标准要求，确保正确读取数据。
 * 2. 生成的JavaScript文件依赖于Chart.js库，所以需要在HTML中引用Chart.js。
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvToHtml {
    public static void main(String[] args) {
        String csvFile = "data/Final_10.csv"; // CSV文件路径
        String jsFile = "visualization/script.js"; // JavaScript文件路径
        List<String[]> data = readCsv(csvFile);
        generateJs(jsFile, data);
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
            writer.write("    const ctxPie2 = document.getElementById('myPieChart2').getContext('2d');\n");
            writer.write("    const ctxPie3 = document.getElementById('myPieChart3').getContext('2d');\n");
            writer.write("    const ctxPie4 = document.getElementById('myPieChart4').getContext('2d');\n");
            writer.write("    const ctxPie5 = document.getElementById('myPieChart5').getContext('2d');\n");
            writer.write("    const ctxPie6 = document.getElementById('myPieChart6').getContext('2d');\n");
            writer.write("    const ctxPie7 = document.getElementById('myPieChart7').getContext('2d');\n");
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
            writer.write("let myPieChart2;\n");
            writer.write("let myPieChart3;\n");
            writer.write("let myPieChart4;\n");
            writer.write("let myPieChart5;\n");
            writer.write("let myPieChart6;\n");
            writer.write("let myPieChart7;\n");
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

            writer.write("        // 更新饼状图1数据\n");
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
            writer.write("        // 更新饼状图2数据\n");
            writer.write("        const pieData2 = [parseFloat(rowData[4]), parseFloat(rowData[8])];\n");
            writer.write("        if (myPieChart2) {\n");
            writer.write("            myPieChart2.destroy(); // 销毁旧的图表\n");
            writer.write("        }\n");
            writer.write("        myPieChart2 = new Chart(ctxPie2, {\n");
            writer.write("            type: 'pie',\n");
            writer.write("            data: {\n");
            writer.write("                labels: ['超大买单成交额', '大买单成交额'],\n");
            writer.write("                datasets: [{\n");
            writer.write("                    data: pieData2,\n");
            writer.write("                    backgroundColor: ['#d5285f', '#e393bf'],\n");
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
            writer.write("        // 更新饼状图3数据\n");
            writer.write("        const pieData3 = [parseFloat(rowData[6]), parseFloat(rowData[10])];\n");
            writer.write("        if (myPieChart3) {\n");
            writer.write("            myPieChart3.destroy(); // 销毁旧的图表\n");
            writer.write("        }\n");
            writer.write("        myPieChart3 = new Chart(ctxPie3, {\n");
            writer.write("            type: 'pie',\n");
            writer.write("            data: {\n");
            writer.write("                labels: ['超大卖单成交额', '大卖单成交额'],\n");
            writer.write("                datasets: [{\n");
            writer.write("                    data: pieData3,\n");
            writer.write("                    backgroundColor: ['#5374c7', '#4dd4f1'],\n");
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
            writer.write("        // 更新饼状图4数据\n");
            writer.write("        const pieData4 = [parseFloat(rowData[3]), parseFloat(rowData[7]), parseFloat(rowData[11]), parseFloat(rowData[15])];\n");
            writer.write("        if (myPieChart4) {\n");
            writer.write("            myPieChart4.destroy(); // 销毁旧的图表\n");
            writer.write("        }\n");
            writer.write("        myPieChart4 = new Chart(ctxPie4, {\n");
            writer.write("            type: 'pie',\n");
            writer.write("            data: {\n");
            writer.write("                labels: ['超大买单成交量', '大买单成交量', '中买单成交量', '小买单成交量'],\n");
            writer.write("                datasets: [{\n");
            writer.write("                    data: pieData4,\n");
            writer.write("                    backgroundColor: ['#FF6384', '#36A2EB', '#ebd336', '#36ebdc'],\n");
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
            writer.write("        // 更新饼状图5数据\n");
            writer.write("        const pieData5 = [parseFloat(rowData[4]), parseFloat(rowData[8]), parseFloat(rowData[12]), parseFloat(rowData[16])];\n");
            writer.write("        if (myPieChart5) {\n");
            writer.write("            myPieChart5.destroy(); // 销毁旧的图表\n");
            writer.write("        }\n");
            writer.write("        myPieChart5 = new Chart(ctxPie5, {\n");
            writer.write("            type: 'pie',\n");
            writer.write("            data: {\n");
            writer.write("                labels: ['超大买单成交额', '大买单成交额', '中买单成交额', '小买单成交额'],\n");
            writer.write("                datasets: [{\n");
            writer.write("                    data: pieData5,\n");
            writer.write("                    backgroundColor: ['#FF6384', '#36A2EB', '#ebd336', '#36ebdc'],\n");
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
            writer.write("        // 更新饼状图6数据\n");
            writer.write("        const pieData6 = [parseFloat(rowData[5]), parseFloat(rowData[9]), parseFloat(rowData[13]), parseFloat(rowData[17])];\n");
            writer.write("        if (myPieChart6) {\n");
            writer.write("            myPieChart6.destroy(); // 销毁旧的图表\n");
            writer.write("        }\n");
            writer.write("        myPieChart6 = new Chart(ctxPie6, {\n");
            writer.write("            type: 'pie',\n");
            writer.write("            data: {\n");
            writer.write("                labels: ['超大卖单成交量', '大卖单成交量', '中卖单成交量', '小卖单成交量'],\n");
            writer.write("                datasets: [{\n");
            writer.write("                    data: pieData6,\n");
            writer.write("                    backgroundColor: ['#FF6384', '#36A2EB', '#ebd336', '#36ebdc'],\n");
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
            writer.write("        // 更新饼状图7数据\n");
            writer.write("        const pieData7 = [parseFloat(rowData[6]), parseFloat(rowData[10]), parseFloat(rowData[14]), parseFloat(rowData[18])];\n");
            writer.write("        if (myPieChart7) {\n");
            writer.write("            myPieChart7.destroy(); // 销毁旧的图表\n");
            writer.write("        }\n");
            writer.write("        myPieChart7 = new Chart(ctxPie7, {\n");
            writer.write("            type: 'pie',\n");
            writer.write("            data: {\n");
            writer.write("                labels: ['超大卖单成交额', '大卖单成交额', '中卖单成交额', '小卖单成交额'],\n");
            writer.write("                datasets: [{\n");
            writer.write("                    data: pieData7,\n");
            writer.write("                    backgroundColor: ['#FF6384', '#36A2EB', '#ebd336', '#36ebdc'],\n");
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
            writer.write("        option.textContent = '时间窗口' + index + ': '+ row[19];\n");
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
            labels.append("\"").append(data.get(i)[19]).append("\""); // 假设时间区间在第19列
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
}