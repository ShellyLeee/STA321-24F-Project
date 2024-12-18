async function loadData() {
    const windowId = document.getElementById("time-window").value;

    // 获取数据
    const response = await fetch(`/data/time-window/${windowId}`);
    const data = await response.json();

    // 更新数据展示
    document.getElementById("main-net-flow").innerText = `Main Net Flow: ${data.mainNetFlow}`;
    document.getElementById("main-flow-in").innerText = `Main Flow In: ${data.mainFlowIn}`;
    document.getElementById("main-flow-out").innerText = `Main Flow Out: ${data.mainFlowOut}`;

    // 更新饼图
    updatePieChart(data);

    // 更新柱状图
    updateBarChart(data.filteredData);

    // 更新折线图
    updateLineChart(data.filteredData);
}

function updatePieChart(data) {
    const pieChartCtx = document.getElementById("pie-chart").getContext("2d");
    const labels = ['Flow In', 'Flow Out'];
    const values = [data.mainFlowIn, data.mainFlowOut];
    new Chart(pieChartCtx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: ['#FF5733', '#33FF57']
            }]
        }
    });
}

function updateBarChart(filteredData) {
    const barChartCtx = document.getElementById("bar-chart").getContext("2d");
    const labels = ['Super Buy', 'Large Buy', 'Super Sell', 'Large Sell'];
    const buyVolumes = [0, 0, 0, 0]; // 按成交量填充
    const buyAmounts = [0, 0, 0, 0]; // 按成交额填充

    filteredData.forEach(row => {
        buyVolumes[0] += parseFloat(row["超大买单成交量"]);
        buyVolumes[1] += parseFloat(row["大买单成交量"]);
        buyVolumes[2] += parseFloat(row["超大卖单成交量"]);
        buyVolumes[3] += parseFloat(row["大卖单成交量"]);

        buyAmounts[0] += parseFloat(row["超大买单成交额"]);
        buyAmounts[1] += parseFloat(row["大买单成交额"]);
        buyAmounts[2] += parseFloat(row["超大卖单成交额"]);
        buyAmounts[3] += parseFloat(row["大卖单成交额"]);
    });

    new Chart(barChartCtx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '成交量',
                data: buyVolumes,
                backgroundColor: '#FF5733',
            }, {
                label: '成交额',
                data: buyAmounts,
                backgroundColor: '#33FF57',
            }]
        }
    });
}

function updateLineChart(filteredData) {
    const lineChartCtx = document.getElementById("line-chart").getContext("2d");
    const times = filteredData.map(row => row["相应时间范围"]);
    const netFlow = filteredData.map(row => parseFloat(row["主力净流入"]));

    new Chart(lineChartCtx, {
        type: 'line',
        data: {
            labels: times,
            datasets: [{
                label: 'Net Flow',
                data: netFlow,
                borderColor: '#FF5733',
                fill: false
            }]
        }
    });
}
