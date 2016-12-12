import defaultsDeep from 'lodash/defaultsDeep';
import forEach from 'lodash/forEach';
import set from 'lodash/set';
import {chartOptions} from './default-chart-options';

export const UPDATE_PATHS = {
  SERIES: 'series.0',
  X_AXIS: 'xAxis'
};
export const chartComponent = {
  bindings: {
    options: '<',
    updater$: '<updater'
  },
  template: '<div></div>',
  controller: class HighChartController {
    constructor(Highstock, $element) {
      'ngInject';
      this.Highcharts = Highstock;
      this.$element = $element;
      this.chart = null;
    }

    $onInit() {
      this.config = defaultsDeep(this.options, chartOptions);
      this.chart = this.Highcharts.chart(this.$element[0], this.config);

      // if we have an updater$ observable, subscribe to it
      if (this.updater$) {
        this.updater$.subscribe({
          next: this.onOptionsChartUpdate.bind(this)
        });
      }
    }

    onOptionsChartUpdate(updates) {
      forEach(updates, updateObj => {
        set(this.config, updateObj.path, updateObj.data);
      });

      if (this.options.scrollbar && this.options.scrollbar.enabled === true) {
        // there is some bug with the scrollbar,
        // if update is used, the scrollbar disappears
        this.chart = this.Highcharts.chart(this.$element[0], this.config);
      } else {
        this.chart.update(this.config);
      }
    }
  }
};
