# STA321-24F-Project: Distributed calculation of the main capital flow in Shenzhen Stock Exchange

> Project Code Guidance of STA321 Distributed storage and parallel computing in 2024 Fall, Department of Statistics and Data Science, SUSTech

In this project, we use MapReduce framework to do distributed calculation. The data that we use is the Shenzhen Stock Exchange Level-2 data, including the entrustment and transaction data for a certain day, which record the detailed information of all purchase and sale orders (You can use SUSTech network to download the data via the website: http://172.18.30.155/p/DWa7pnYQNRg5IAA). 

Specifically, we use the existing data and design methods to distinguish between active buy orders and sell orders, calculate the transaction volume and transaction volume, and then calculate the main capital flow based on the transaction order type (large orders, super large orders). Finally, use Termius as the platform to run the jar package of the MapReduce program. The detailed instruction of the project and the calculation process can be seen in the Project Instruction: https://github.com/ShellyLeee/STA321-24F-Project/blob/main/project-v1.pdf.

After the calculation completed, we output 19 indicators, including the inflow, outflow, net inflow and other indicators of the main capital inflow in each time window, such as the transaction volume and transaction volume of the ultra-large order. In addition, we also used HTML and CSS to realize data visualization of front-end networks, and conducted data accuracy and speed tests. 

In testing, we need to calculate the corresponding indicators in a certain time period based on the given stock code and test the calculation time. Finally, we successfully passed the test with a 100% accuracy rate, which took 1 minute and 05 seconds. After the project ends, we refer to the optimal solutions of other groups, fully consider the amount of data in the project and the calculation software, and finally optimize the run time to about 30 seconds using pseudo-distributed operations.


## Code Structure

```
.idea/

STA321-Project/
├── .DS_Store
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   ├── driver/
│       │   ├── mapper/
│       │   ├── reducer/
│       │   └── CsvToHtml.java
│       └── resources/
├── target/
│   └── classes/

data/
├── Final_1_new.csv
├── Final_10.csv
└── Final_20.csv

out/
└── artifacts/
    └── STA321_24F_Project_jar/

visualization/
├── script.js
├── style.css
└── visualization.html

```

## Running Instruction
You can download the program: `STA321 24F Project.jar` from https://github.com/ShellyLeee/STA321-24F-Project/tree/main/out/artifacts/STA321_24F_Project_jar, as well as the data (use SUSTech network to download the data via the website: http://172.18.30.155/p/DWa7pnYQNRg5IAA). Then, you should upload the jar program and data into your own cloud server, and run the computation from your end. 

Also, you can do some customized setting on the driver, mapper and reducer based on your need, and generate a new jar program to run.
