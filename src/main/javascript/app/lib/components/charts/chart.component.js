import defaultsDeep from 'lodash/defaultsDeep';
import forEach from 'lodash/forEach';
import set from 'lodash/set';
import isArray from 'lodash/isArray';
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
    constructor(Highcharts, $element) {
      'ngInject';
      this.Highcharts = Highcharts;
      this.$element = $element;
      this.chart = null;
    }

    $onInit() {
      this.config = defaultsDeep(this.options, chartOptions);
      this.chart = this.Highcharts.chart(this.$element[0], this.config);

      // if we have an updater$ observable, subscribe to it
      if (this.updater$) {
        this.subscription = this.updater$.subscribe({
          next: this.onOptionsChartUpdate.bind(this)
        });
      }
    }

    $onDestroy() {
      this.subscription.unsubscribe();
    }

    onOptionsChartUpdate(updates) {
      if (!isArray(updates)) {
        if (updates.export) {
          this.onExport();
        }
      } else {
        forEach(updates, updateObj => {
          set(this.config, updateObj.path, updateObj.data);
        });
      }

      // Not using chart.update due to a bug with navigation
      // update and bar styles.
      this.chart = this.Highcharts.chart(this.$element[0], this.config);
    }

    onExport() {
      // TODO investigate
      // this.chart.exportChart is not a function despite importing the exporting and offline-exporting modules

      // this.chart.exportChart({
      //   type: 'application/pdf',
      //   filename: 'my-pdf'
      // });
    }
  }
};
