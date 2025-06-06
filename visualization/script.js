const lineLabels = ["20190102093000000 to 20190102094000000","20190102094000000 to 20190102095000000","20190102095000000 to 20190102100000000","20190102100000000 to 20190102101000000","20190102101000000 to 20190102102000000","20190102102000000 to 20190102103000000","20190102103000000 to 20190102104000000","20190102104000000 to 20190102105000000","20190102105000000 to 20190102110000000","20190102110000000 to 20190102111000000","20190102111000000 to 20190102112000000","20190102112000000 to 20190102113000000","20190102130000000 to 20190102131000000","20190102131000000 to 20190102132000000","20190102132000000 to 20190102133000000","20190102133000000 to 20190102134000000","20190102134000000 to 20190102135000000","20190102135000000 to 20190102140000000","20190102140000000 to 20190102141000000","20190102141000000 to 20190102142000000","20190102142000000 to 20190102143000000","20190102143000000 to 20190102144000000","20190102144000000 to 20190102145000000","20190102145000000 to 20190102150000000"];
const lineData1 = [-5564492.0,-2.375444616E7,-6643798.280000001,1.0163778E7,-9003510.329999998,287990.0,-5184610.390000001,156188.0,894475.0,-3322838.0,-2039136.0,113128.0,-4738232.96,-1.09698078E7,-2321611.0,-7241504.859999999,16064.0,-1.5677034E7,-2458534.5600000005,904421.9999999999,-648953.0,738018.0,-5193262.800000001,-3842762.9799999995];
const lineData2 = [3067448.0,4527929.63,8479403.52,1.2868543E7,2509653.0300000003,1706625.0,801050.0,917532.0,3126816.0,461500.0,1060527.0,737752.0,816855.0,0.0,1259452.0,3522491.18,878696.0,1591488.0,3880134.0,1408209.0,324544.0,1236820.0,3634132.5999999996,1244288.0];
const lineData3 = [8631940.0,2.828237579E7,1.51232018E7,2704765.0,1.151316336E7,1418635.0,5985660.390000001,761344.0,2232341.0,3784338.0,3099663.0,624624.0,5555087.96,1.09698078E7,3581063.0,1.076399604E7,862632.0,1.7268522E7,6338668.5600000005,503787.0000000001,973497.0,498802.0,8827395.4,5087050.9799999995];

const data = [["主力净流入","主力流入","主力流出","超大买单成交量","超大买单成交额","超大卖单成交量","超大卖单成交额","大买单成交量","大买单成交额","大卖单成交量","大卖单成交额","中买单成交量","中买单成交额","中卖单成交量","中卖单成交额","小买单成交量","小买单成交额","小卖单成交量","小卖单成交额","	时间区间"],["-5564492.0","3067448.0","8631940.0","0.0","0.0","521600.0","4886715.0","327000.0","3067448.0","399800.0","3745225.0","274500.0","2574730.0","1106897.0","1.03686878E7","301303.0","2824989.2","664000.0","6219446.0	","20190102093000000 to 20190102094000000"],["-2.375444616E7","4527929.63","2.828237579E7","0.0","0.0","1238463.0","1.151394779E7","486100.0","4527929.63","1807400.0","1.6768428E7","381000.0","3537184.0","2312900.0","2.1455468E7","426700.0","3964843.0","767937.0","7132761.21	","20190102094000000 to 20190102095000000"],["-6643798.280000001","8479403.52","1.51232018E7","0.0","0.0","348000.0","3221788.0","915519.0","8479403.52","1286240.0","1.19014138E7","1123700.0","1.0408937E7","1030700.0","9529079.4","397500.0","3680779.0","334862.0","3097221.92	","20190102095000000 to 20190102100000000"],["1.0163778E7","1.2868543E7","2704765.0","735000.0","6815220.0","0.0","0.0","652300.0","6053323.0","291800.0","2704765.0","411200.0","3813064.0","454300.0","4209335.0","272600.0","2527667.0","325648.0","3017865.96	","20190102100000000 to 20190102101000000"],["-9003510.329999998","2509653.0300000003","1.151316336E7","0.0","0.0","900100.0","8316045.390000001","271661.0","2509653.0300000003","345639.0","3197117.9699999997","283600.0","2623505.0","387300.0","3581320.0","222800.0","2061635.0","235380.0","2176546.6	","20190102101000000 to 20190102102000000"],["287990.0","1706625.0","1418635.0","184500.0","1706625.0","0.0","0.0","0.0","0.0","153300.0","1418635.0","252350.0","2336844.5","128300.0","1186851.0","171400.0","1587070.0","225778.0","2088490.0	","20190102102000000 to 20190102103000000"],["-5184610.390000001","801050.0","5985660.390000001","0.0","0.0","336000.0","3106185.0","86600.0","801050.0","311900.0","2879475.39","138261.0","1277567.03","547539.0","5056235.97","141700.0","1309821.0","353400.0","3264646.0	","20190102103000000 to 20190102104000000"],["156188.0","917532.0","761344.0","0.0","0.0","0.0","0.0","99300.0","917532.0","82500.0","761344.0","178600.0","1649775.0","332800.0","3072749.0","182500.0","1686178.0","193400.0","1785730.0	","20190102104000000 to 20190102105000000"],["894475.0","3126816.0","2232341.0","0.0","0.0","0.0","0.0","338400.0","3126816.0","241800.0","2232341.0","130300.0","1204687.0","625800.0","5778919.0","184600.0","1706631.0","241589.0","2231358.4699999997	","20190102105000000 to 20190102110000000"],["-3322838.0","461500.0","3784338.0","0.0","0.0","170000.0","1567400.0","50000.0","461500.0","240100.0","2216938.0","96700.0","893486.0","474800.0","4379723.0","156700.0","1446964.0","237400.0","2190704.0	","20190102110000000 to 20190102111000000"],["-2039136.0","1060527.0","3099663.0","114900.0","1060527.0","271000.0","2499987.0","0.0","0.0","64900.0","599676.0","75700.0","699252.0","318500.0","2939063.0","157000.0","1450440.0","113700.0","1049322.0	","20190102111000000 to 20190102112000000"],["113128.0","737752.0","624624.0","0.0","0.0","0.0","0.0","79800.0","737752.0","67600.0","624624.0","103800.0","959912.0","258283.0","2385814.8899999997","125820.0","1163463.8","99043.0","914952.89	","20190102112000000 to 20190102113000000"],["-4738232.96","816855.0","5555087.96","0.0","0.0","506752.0","4675008.96","88500.0","816855.0","95400.0","880079.0","98500.0","909697.0","241672.0","2230599.08","142448.0","1315418.52","193228.0","1782894.44	","20190102130000000 to 20190102131000000"],["-1.09698078E7","0.0","1.09698078E7","0.0","0.0","943060.0","8688828.200000001","0.0","0.0","247700.0","2280979.6","148640.0","1370729.4","501900.0","4622465.0","201140.0","1854769.4","379300.0","3493611.4	","20190102131000000 to 20190102132000000"],["-2321611.0","1259452.0","3581063.0","0.0","0.0","200000.0","1842612.0","136600.0","1259452.0","188700.0","1738451.0","214500.0","1977600.0","332735.0","3064706.35","234165.0","2157920.65","220565.0","2031256.0	","20190102132000000 to 20190102133000000"],["-7241504.859999999","3522491.18","1.076399604E7","150258.0","1383876.1800000002","702000.0","6459157.42","231900.0","2138615.0","467422.0","4304838.62","235700.0","2171151.0","392177.0","3612019.4","253856.0","2338795.76","208622.0","1921274.62	","20190102133000000 to 20190102134000000"],["16064.0","878696.0","862632.0","0.0","0.0","0.0","0.0","95200.0","878696.0","93500.0","862632.0","160200.0","1478646.0","198000.0","1826310.0","126700.0","1169593.0","237800.0","2193120.0	","20190102134000000 to 20190102135000000"],["-1.5677034E7","1591488.0","1.7268522E7","172800.0","1591488.0","1258000.0","1.1574213E7","0.0","0.0","618600.0","5694309.0","176300.0","1625588.0","576900.0","5311614.04","175640.0","1618608.0","325460.0","2997141.5600000005	","20190102135000000 to 20190102140000000"],["-2458534.5600000005","3880134.0","6338668.5600000005","119900.0","1099483.0","296040.0","2717647.2","302900.0","2780651.0","394696.0","3621021.36","363000.0","3338304.0","841900.0","7730557.6","393700.0","3619547.0","353020.0","3241774.6	","20190102140000000 to 20190102141000000"],["904421.9999999999","1408209.0","503787.0000000001","0.0","0.0","0.0","0.0","152900.0","1408209.0","54700.0","503787.0000000001","146900.0","1352960.0","78300.0","720750.0","233700.0","2152555.0","140500.0","1292864.0	","20190102141000000 to 20190102142000000"],["-648953.0","324544.0","973497.0","0.0","0.0","0.0","0.0","35200.0","324544.0","105700.0","973497.0","295600.0","2725681.0","139300.0","1282865.0","146000.0","1346123.0","106391.0","979834.51	","20190102142000000 to 20190102143000000"],["738018.0","1236820.0","498802.0","0.0","0.0","0.0","0.0","134000.0","1236820.0","54100.0","498802.0","146300.0","1351456.0","194200.0","1791822.0","131900.0","1217997.0","198400.0","1830208.0	","20190102143000000 to 20190102144000000"],["-5193262.800000001","3634132.5999999996","8827395.4","0.0","0.0","659900.0","6071481.0","394480.0","3634132.5999999996","299551.0","2755914.4","229340.0","2112469.29","336669.0","3100986.69","135800.0","1250533.0899999999","279669.0","2575532.0	","20190102144000000 to 20190102145000000"],["-3842762.9799999995","1244288.0","5087050.9799999995","0.0","0.0","198000.0","1819620.0","135300.0","1244288.0","355542.0","3267430.98","206700.0","1900661.0","495817.0","4556558.23","384865.0","3539150.35","325900.0","2995021.0	","20190102145000000 to 20190102150000000"]];
document.addEventListener('DOMContentLoaded', function() {
    const timeWindowSelect = document.getElementById('timeWindowId');
    const updateButton = document.getElementById('updateButton');
    const dataTable = document.getElementById('dataDisplay');
    const ctxPie = document.getElementById('myPieChart').getContext('2d');
    const ctxPie2 = document.getElementById('myPieChart2').getContext('2d');
    const ctxPie3 = document.getElementById('myPieChart3').getContext('2d');
    const ctxPie4 = document.getElementById('myPieChart4').getContext('2d');
    const ctxPie5 = document.getElementById('myPieChart5').getContext('2d');
    const ctxPie6 = document.getElementById('myPieChart6').getContext('2d');
    const ctxPie7 = document.getElementById('myPieChart7').getContext('2d');
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
let myPieChart4;
let myPieChart5;
let myPieChart6;
let myPieChart7;
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

        // 更新饼状图4数据
        const pieData4 = [parseFloat(rowData[3]), parseFloat(rowData[7]), parseFloat(rowData[11]), parseFloat(rowData[15])];
        if (myPieChart4) {
            myPieChart4.destroy(); // 销毁旧的图表
        }
        myPieChart4 = new Chart(ctxPie4, {
            type: 'pie',
            data: {
                labels: ['超大买单成交量', '大买单成交量', '中买单成交量', '小买单成交量'],
                datasets: [{
                    data: pieData4,
                    backgroundColor: ['#FF6384', '#36A2EB', '#ebd336', '#36ebdc'],
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'top' }
                }
            }
        });

        // 更新饼状图5数据
        const pieData5 = [parseFloat(rowData[4]), parseFloat(rowData[8]), parseFloat(rowData[12]), parseFloat(rowData[16])];
        if (myPieChart5) {
            myPieChart5.destroy(); // 销毁旧的图表
        }
        myPieChart5 = new Chart(ctxPie5, {
            type: 'pie',
            data: {
                labels: ['超大买单成交额', '大买单成交额', '中买单成交额', '小买单成交额'],
                datasets: [{
                    data: pieData5,
                    backgroundColor: ['#FF6384', '#36A2EB', '#ebd336', '#36ebdc'],
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'top' }
                }
            }
        });

        // 更新饼状图6数据
        const pieData6 = [parseFloat(rowData[5]), parseFloat(rowData[9]), parseFloat(rowData[13]), parseFloat(rowData[17])];
        if (myPieChart6) {
            myPieChart6.destroy(); // 销毁旧的图表
        }
        myPieChart6 = new Chart(ctxPie6, {
            type: 'pie',
            data: {
                labels: ['超大卖单成交量', '大卖单成交量', '中卖单成交量', '小卖单成交量'],
                datasets: [{
                    data: pieData6,
                    backgroundColor: ['#FF6384', '#36A2EB', '#ebd336', '#36ebdc'],
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'top' }
                }
            }
        });

        // 更新饼状图7数据
        const pieData7 = [parseFloat(rowData[6]), parseFloat(rowData[10]), parseFloat(rowData[14]), parseFloat(rowData[18])];
        if (myPieChart7) {
            myPieChart7.destroy(); // 销毁旧的图表
        }
        myPieChart7 = new Chart(ctxPie7, {
            type: 'pie',
            data: {
                labels: ['超大卖单成交额', '大卖单成交额', '中卖单成交额', '小卖单成交额'],
                datasets: [{
                    data: pieData7,
                    backgroundColor: ['#FF6384', '#36A2EB', '#ebd336', '#36ebdc'],
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
