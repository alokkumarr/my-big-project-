import map from 'lodash/map';
import range from 'lodash/range';

import template from './analyze-chart-detail.component.html';
import style from './analyze-chart-detail.component.scss';

export const AnalyzeChartDetailComponent = {
  template,
  bindings: {
    analysis: '<'
  },
  styles: [style],
  controller: class AnalyzeChartDetailController {
    constructor(AnalyzeService, $state, $window) {
      'ngInject';
      this._AnalyzeService = AnalyzeService;
      this._$state = $state;
      this._$window = $window;
      this.barChartOptions = {
        xAxis: {
          categories: ['A', 'B', 'C', 'D', 'E'],
          title: {
            text: 'Customer',
            y: 25
          }
        },
        chart: {
          type: 'column',
          spacingLeft: 45,
          spacingBottom: 45,
          width: 700
        },
        yAxis: {
          title: {
            text: 'Revenue (millions)',
            x: -25
          }
        },
        legend: {
          align: 'right',
          layout: 'vertical'
        },
        series: [{
          name: 'Data',
          data: [100, 25, 45, 100, 22]
        }]
      };
    }

    generateData() {
      const series = ['John'];
      const dataPoints = 5;
      const min = 0;
      const max = 100;

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
  }
};
