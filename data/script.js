const lineLabels = ["20190102093000000 to 20190102095000000","20190102095000000 to 20190102101000000","20190102101000000 to 20190102103000000","20190102103000000 to 20190102105000000","20190102105000000 to 20190102111000000","20190102111000000 to 20190102113000000","20190102130000000 to 20190102132000000","20190102132000000 to 20190102134000000","20190102134000000 to 20190102140000000","20190102140000000 to 20190102142000000","20190102142000000 to 20190102144000000","20190102144000000 to 20190102150000000"];
const lineData1 = [-2.931893816E7,3519979.719999995,-8715520.330000002,-5028422.39,-2428363.0,-1926008.0,-1.570804076E7,-9563115.86,-1.566097E7,-1554112.5600000005,89065.0,-9036025.78];
const lineData2 = [7595377.63,2.1347946519999996E7,4216278.03,1718582.0,3588316.0,1798279.0,816855.0,4781943.18,2470184.0,5288343.0,1561364.0,4878420.6];
const lineData3 = [3.691431579E7,1.78279668E7,1.2931798360000001E7,6747004.39,6016679.0,3724287.0,1.652489576E7,1.434505904E7,1.8131154E7,6842455.5600000005,1472299.0,1.3914446379999999E7];

const data = [["主力净流入","主力流入","主力流出","超大买单成交量","超大买单成交额","超大卖单成交量","超大卖单成交额","大买单成交量","大买单成交额","大卖单成交量","大卖单成交额","中买单成交量","中买单成交额","中卖单成交量","中卖单成交额","小买单成交量","小买单成交额","小卖单成交量","小卖单成交额","	时间区间"],["-2.931893816E7","7595377.63","3.691431579E7","0.0","0.0","1760063.0","1.640066279E7","813100.0","7595377.63","2207200.0","2.0513653E7","655500.0","6111914.0","3419797.0","3.1824155799999997E7","728003.0","6789832.2","1431937.0","1.335220721E7	","20190102093000000 to 20190102095000000"],["3519979.719999995","2.1347946519999996E7","1.78279668E7","735000.0","6815220.0","348000.0","3221788.0","1567819.0","1.453272652E7","1578040.0","1.46061788E7","1534900.0","1.4222001E7","1485000.0","1.37384144E7","670100.0","6208446.0","660510.0","6115087.88	","20190102095000000 to 20190102101000000"],["-8715520.330000002","4216278.03","1.2931798360000001E7","184500.0","1706625.0","900100.0","8316045.390000001","271661.0","2509653.0300000003","498939.0","4615752.97","535950.0","4960349.5","515600.0","4768171.0","394200.0","3648705.0","461158.0","4265036.6	","20190102101000000 to 20190102103000000"],["-5028422.39","1718582.0","6747004.39","0.0","0.0","336000.0","3106185.0","185900.0","1718582.0","394400.0","3640819.39","316861.0","2927342.03","880339.0","8128984.970000001","324200.0","2995999.0","546800.0","5050376.0	","20190102103000000 to 20190102105000000"],["-2428363.0","3588316.0","6016679.0","0.0","0.0","170000.0","1567400.0","388400.0","3588316.0","481900.0","4449279.0","227000.0","2098173.0","1100600.0","1.0158642E7","341300.0","3153595.0","478989.0","4422062.47	","20190102105000000 to 20190102111000000"],["-1926008.0","1798279.0","3724287.0","114900.0","1060527.0","271000.0","2499987.0","79800.0","737752.0","132500.0","1224300.0","179500.0","1659164.0","576783.0","5324877.89","282820.0","2613903.8","212743.0","1964274.8900000001	","20190102111000000 to 20190102113000000"],["-1.570804076E7","816855.0","1.652489576E7","0.0","0.0","1449812.0","1.336383716E7","88500.0","816855.0","343100.0","3161058.6","247140.0","2280426.4","743572.0","6853064.08","343588.0","3170187.9200000004","572528.0","5276505.84	","20190102130000000 to 20190102132000000"],["-9563115.86","4781943.18","1.434505904E7","150258.0","1383876.1800000002","902000.0","8301769.42","368500.0","3398067.0","656122.0","6043289.62","450200.0","4148751.0","724912.0","6676725.75","488021.0","4496716.409999999","429187.0","3952530.62	","20190102132000000 to 20190102134000000"],["-1.566097E7","2470184.0","1.8131154E7","172800.0","1591488.0","1258000.0","1.1574213E7","95200.0","878696.0","712100.0","6556941.0","336500.0","3104234.0","774900.0","7137924.04","302340.0","2788201.0","563260.0","5190261.5600000005	","20190102134000000 to 20190102140000000"],["-1554112.5600000005","5288343.0","6842455.5600000005","119900.0","1099483.0","296040.0","2717647.2","455800.0","4188860.0","449396.0","4124808.3600000003","509900.0","4691264.0","920200.0","8451307.6","627400.0","5772102.0","493520.0","4534638.6	","20190102140000000 to 20190102142000000"],["89065.0","1561364.0","1472299.0","0.0","0.0","0.0","0.0","169200.0","1561364.0","159800.0","1472299.0","441900.0","4077137.0","333500.0","3074687.0","277900.0","2564120.0","304791.0","2810042.51	","20190102142000000 to 20190102144000000"],["-9036025.78","4878420.6","1.3914446379999999E7","0.0","0.0","857900.0","7891101.0","529780.0","4878420.6","655093.0","6023345.38","436040.0","4013130.29","832486.0","7657544.920000001","520665.0","4789683.44","605569.0","5570553.0	","20190102144000000 to 20190102150000000"]];
document.addEventListener('DOMContentLoaded', function() {
    const timeWindowSelect = document.getElementById('timeWindowId');
    const updateButton = document.getElementById('updateButton');
    const dataTable = document.getElementById('dataDisplay');
    const ctxPie = document.getElementById('myPieChart').getContext('2d');
    const ctxPie2 = document.getElementById('myPieChart2').getContext('2d');
    const ctxPie3 = document.getElementById('myPieChart3').getContext('2d');
    const ctxBar = document.getElementById('myBarChart').getContext('2d');
    const ctxBarSell = document.getElementById('myBarChartSell').getContext('2d');

    const myLineChart = new Chart(document.getElementById('myLineChart').getContext('2d'), {
        type: 'line',
        data: {
            labels: lineLabels,
            datasets: [
                { label: '主力净流入', data: lineData1, borderColor: '#FF6384', fill: false },
                { label: '主力流入', data: lineData2, borderColor: '#36A2EB', fill: false },
                { label: '主力流出', data: lineData3, borderColor: '#FFCE56', fill: false }
            ]
        },
        options: {
            responsive: true,
            scales: {
                x: { title: { display: true, text: '时间窗口' } },
                y: { title: { display: true, text: '金额' } }
            }
        }
    });

let myPieChart;
let myPieChart2;
let myPieChart3;
let myBarChart;
let myBarChartSell;
function updateCharts(timeWindowId) {
    const rowData = data[timeWindowId];
    if (rowData) {
        // 更新数据展示

        const rows = dataTable.getElementsByTagName('tr');
        while (rows.length > 1) { 
            dataTable.deleteRow(1); 
        }
        const newRow = document.createElement('tr');
        newRow.innerHTML = `<td>${rowData[0]}</td><td>${rowData[1]}</td><td>${rowData[2]}</td>
                            <td>${rowData[3]}</td><td>${rowData[4]}</td>
                            <td>${rowData[5]}</td><td>${rowData[6]}</td>
                            <td>${rowData[7]}</td><td>${rowData[8]}</td>
                            <td>${rowData[9]}</td><td>${rowData[10]}</td>
                            <td>${rowData[11]}</td><td>${rowData[12]}</td>
                            <td>${rowData[13]}</td><td>${rowData[14]}</td>
                            <td>${rowData[15]}</td><td>${rowData[16]}</td>
                            <td>${rowData[17]}</td><td>${rowData[18]}</td><td>${rowData[19]}</td>`;
        dataTable.appendChild(newRow);

        document.getElementById('mainNetInflow').textContent = rowData[0];
        document.getElementById('mainInflow').textContent = rowData[1];
        document.getElementById('mainOutflow').textContent = rowData[2];

        // 更新饼状图1数据
        const pieData = [parseFloat(rowData[1]), parseFloat(rowData[2])];
        if (myPieChart) {
            myPieChart.destroy(); // 销毁旧的图表
        }
        myPieChart = new Chart(ctxPie, {
            type: 'pie',
            data: {
                labels: ['主力流入', '主力流出'],
                datasets: [{
                    data: pieData,
                    backgroundColor: ['#FF6384', '#36A2EB'],
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'top' }
                }
            }
        });

        // 更新饼状图2数据
        const pieData2 = [parseFloat(rowData[4]), parseFloat(rowData[8])];
        if (myPieChart2) {
            myPieChart2.destroy(); // 销毁旧的图表
        }
        myPieChart2 = new Chart(ctxPie2, {
            type: 'pie',
            data: {
                labels: ['超大买单成交额', '大买单成交额'],
                datasets: [{
                    data: pieData2,
                    backgroundColor: ['#d5285f', '#e393bf'],
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'top' }
                }
            }
        });

        // 更新饼状图3数据
        const pieData3 = [parseFloat(rowData[6]), parseFloat(rowData[10])];
        if (myPieChart3) {
            myPieChart3.destroy(); // 销毁旧的图表
        }
        myPieChart3 = new Chart(ctxPie3, {
            type: 'pie',
            data: {
                labels: ['超大卖单成交额', '大卖单成交额'],
                datasets: [{
                    data: pieData3,
                    backgroundColor: ['#5374c7', '#4dd4f1'],
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'top' }
                }
            }
        });

        // 更新买单柱状图数据
        const barVolumeData = [
            parseFloat(rowData[3]), // 超大买单成交量
            parseFloat(rowData[7]), // 大买单成交量
            parseFloat(rowData[11]), // 中买单成交量
            parseFloat(rowData[15])  // 小买单成交量
        ];
        const barAmountData = [
            parseFloat(rowData[4]), // 超大买单成交额
            parseFloat(rowData[8]), // 大买单成交额
            parseFloat(rowData[12]), // 中买单成交额
            parseFloat(rowData[16])  // 小买单成交额
        ];
        if (myBarChart) {
            myBarChart.destroy(); // 销毁旧的图表
        }
        myBarChart = new Chart(ctxBar, {
            type: 'bar',
            data: {
                labels: ['超大买单', '大买单', '中买单', '小买单'],
                datasets: [
                    { label: '成交量', data: barVolumeData, backgroundColor: '#FF6384' },
                    { label: '成交额', data: barAmountData, backgroundColor: '#36A2EB' }
                ]
            },
            options: {
                responsive: true,
                scales: {
                    x: { title: { display: true, text: '买单类型' } },
                    y: { title: { display: true, text: '金额' } }
                }
            }
        });

        // 更新卖单柱状图数据
        const barSellVolumeData = [
            parseFloat(rowData[5]), // 超大卖单成交量
            parseFloat(rowData[9]), // 大卖单成交量
            parseFloat(rowData[13]), // 中卖单成交量
            parseFloat(rowData[17])  // 小卖单成交量
        ];
        const barSellAmountData = [
            parseFloat(rowData[6]), // 超大卖单成交额
            parseFloat(rowData[10]), // 大卖单成交额
            parseFloat(rowData[14]), // 中卖单成交额
            parseFloat(rowData[18])  // 小卖单成交额
        ];
        if (myBarChartSell) {
            myBarChartSell.destroy(); // 销毁旧的图表
        }
        myBarChartSell = new Chart(ctxBarSell, {
            type: 'bar',
            data: {
                labels: ['超大卖单', '大卖单', '中卖单', '小卖单'],
                datasets: [
                    { label: '成交量', data: barSellVolumeData, backgroundColor: '#FF6384' },
                    { label: '成交额', data: barSellAmountData, backgroundColor: '#36A2EB' }
                ]
            },
            options: {
                responsive: true,
                scales: {
                    x: { title: { display: true, text: '卖单类型' } },
                    y: { title: { display: true, text: '金额' } }
                }
            }
        });
    } else {
        alert('未找到对应的时间窗口数据！');
    }
}

    // 填充下拉框
    data.forEach((row, index) => {
        const option = document.createElement('option');
        option.value = index;
        option.textContent = '时间窗口' + index + ': '+ row[19];
        timeWindowSelect.appendChild(option);
    });

    updateButton.addEventListener('click', function() {
        const timeWindowId = parseInt(timeWindowSelect.value);
        updateCharts(timeWindowId);
    });
});
