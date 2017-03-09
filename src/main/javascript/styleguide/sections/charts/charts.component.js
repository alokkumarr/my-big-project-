import map from 'lodash/map';
import sum from 'lodash/sum';
import range from 'lodash/range';
import template from './charts.component.html';

export const ChartsComponent = {
  template,
  controller: class ChartsController {
    constructor($interval, $timeout, dxDataGridService) {
      'ngInject';
      this.$interval = $interval;
      this.$timeout = $timeout;
      this._dxDataGridService = dxDataGridService;

      this.barChartOptions = {
        xAxis: {
          categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
        },
        chart: {
          type: 'bar'
        },
        series: this.generateData()
      };

      this.lineChartOptions = {
        xAxis: {
          categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
        },
        chart: {
          type: 'line'
        },
        series: this.generateData()
      };

      this.getGridConfig = () => {
        const dataSource = map(this.lineChartOptions.series, series => {
          return {
            name: series.name,
            value: sum(series.data)
          };
        });
        const columns = [{
          caption: 'Name',
          dataField: 'name',
          allowSorting: true,
          alignment: 'left'
        }, {
          caption: 'Value',
          dataField: 'value',
          allowSorting: true,
          alignment: 'left'
        }];

        return this._dxDataGridService.mergeWithDefaultConfig({
          columns,
          dataSource
        });
      };

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
          type: 'area',
          height: 250
        },
        legend: {
          enabled: false
        },
        series: [{
          name: 'Alerts',
          data: [4, 6, 5, 6.6, 4.5, 6, 8]
        }]
      };

      this.chartConfig = {
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
        },
        series: [{
          data: [10, 15, 12, 8, 7]
        }]
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
