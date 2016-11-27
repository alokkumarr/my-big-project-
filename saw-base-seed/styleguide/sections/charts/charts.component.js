import map from 'lodash/map';
import sum from 'lodash/sum';
import range from 'lodash/range';
import template from './charts.component.html';

export const ChartsComponent = {
  template,
  controller: class ChartsController {
    constructor($interval, $timeout) {
      'ngInject';
      this.$interval = $interval;
      this.$timeout = $timeout;

      this.barChartOptions = {
        static: {
          xAxis: {
            categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
          },
          chart: {
            type: 'bar'
          }
        },
        dynamic: {
          series: this.generateData()
        }
      };

      this.lineChartOptions = {
        static: {
          xAxis: {
            categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
          },
          chart: {
            type: 'line'
          }
        },
        dynamic: {
          series: this.generateData()
        }
      };

      this.gridOptions = {
        rowHeight: 36,
        data: map(this.lineChartOptions.dynamic.series, series => {
          return {
            name: series.name,
            values: sum(series.data)
          };
        })
      };

      this.areaChartOptions = {
        static: {
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
            type: 'area',
            height: 250
          },
          legend: {
            enabled: false
          }
        },
        dynamic: {
          series: [{
            name: 'Alerts',
            data: [4, 6, 5, 6.6, 4.5, 6, 8]
          }]
        }
      };

      this.chartConfig = {
        static: {
          // This is the Main Highcharts chart config. Any Highchart options are valid here.
          // will be overriden by values specified below.
          chart: {
            type: 'bar'
          },
          tooltip: {
            style: {
              padding: 10,
              fontWeight: 'bold'
            }
          },
          xAxis: {
            endOnTick: false,
            minorGridLineWidth: 0,
            minorTickLength: 0,
            tickLength: 0,
            lineWidth: 0,
            categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
          },
          yAxis: {
            gridLineWidth: 0,
            labels: {
              overflow: 'justify'
            }
          }
        },
        dynamic: {
          series: [{
            data: [10, 15, 12, 8, 7]
          }]
        }
      };
    }

    generateData() {
      const series = ['John', 'Jane', 'Joe'];
      const dataPoints = 4;
      const min = 0;
      const max = 8;

      return map(series, name => {
        return {
          name,
          data: map(
            range(1, dataPoints),
            () => min + Math.floor(Math.random() * max)
          )
        };
      });
    }

    refreshData() {
      this.barChartData = this.generateData();
      this.lineChartData = this.generateData();
      this.snapshotBarChartData = this.generateData();
    }
  }
};
