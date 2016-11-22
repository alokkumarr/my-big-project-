import template from './charts.component.html';

export const ChartsComponent = {
  template,
  controller: class ChartsController {
    constructor($scope, $interval, $timeout) {
      this.$interval = $interval;
      this.$timeout = $timeout;

      this.barChartData = this.generateData();
      this.barChartOptions = {
        xAxis: {
          categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
        }
      };

      this.lineChartData = this.generateData();
      this.lineChartOptions = {
        xAxis: {
          categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
        }
      };

      this.areaChartData = {alerts: [4, 6, 5, 6.6, 4.5, 6, 8]};
      this.areaChartOptions = {
        xAxis: {
          categories: ['M', 'T', 'W', 'Th', 'F', 'S', 'Su']
        },
        yAxis: {
          min: 4,
          max: 8
        },
        plotOptions: {
          area: {
            color: '#0084FF',
            pointPlacement: 'on'
          }
        },
        chart: {
          height: 250
        },
        legend: {
          enabled: false
        }
      };

      this.transactionVolumeChartData = {
        Alpha: [
          [0.3, 5],
          [2.1, 25],
          [3.5, 10],
          [4.5, 11],
          [5.6, 6],
          [6.5, 21],
          [7.1, 20],
          [7.8, 29],
          [8.7, 35],
          [9, 29],
          [9.5, 5],
          [11.1, 20]
        ],
        Bravo: [
          [0.3, 2],
          [4.8, 13],
          [6.2, 35],
          [8.9, 10],
          [10.6, 22],
          [11.1, 10]
        ]
      };
      this.transactionVolumeChartOptions = {
        xAxis: {
          categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
          startOnTick: true,
          title: {
            text: 'Months'
          }
        },
        yAxis: {
          title: {
            text: 'Revenue'
          }
        },
        legend: {
          align: 'right',
          verticalAlign: 'top',
          layout: 'vertical',
          x: 0,
          y: 100
        },
        chart: {
          marginRight: 120
        },
        plotOptions: {
          line: {
            pointPlacement: -0.5
          }
        }
      };

      this.gridOptions = {
        rowHeight: 36,
        data: Object.keys(this.lineChartData).map(k => {
          return {
            name: k,
            values: this.lineChartData[k].reduce((a, b) => a + b)
          };
        })
      };

      this.snapshotBarChartData = {
        Jane: [2, 2, 3, 7, 1]
      };
    }
    generateData() {
      const series = ['John', 'Jane', 'Joe'];
      const dataPoints = 4;
      const min = 0;
      const max = 8;
      const result = {};

      series.forEach(name => {
        const data = [];
        for (let i = 0; i < dataPoints; i++) {
          data.push(min + Math.floor(Math.random() * max));
        }
        result[name] = data;
      });

      return result;
    }

    refreshData() {
      this.barChartData = this.generateData();
      this.lineChartData = this.generateData();
      this.snapshotBarChartData = this.generateData();
    }
  }
};
