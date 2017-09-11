import * as defaultsDeep from 'lodash/defaultsDeep';
import * as forEach from 'lodash/forEach';
import * as set from 'lodash/set';
import * as isArray from 'lodash/isArray';
import {globalChartOptions, chartOptions} from './default-chart-options';

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

      Highcharts.setOptions(globalChartOptions);
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
      if (this.subscription) {
        this.subscription.unsubscribe();
      }
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
        // Not using chart.update due to a bug with navigation
        // update and bar styles.
        this.chart = this.Highcharts.chart(this.$element[0], this.config);
      }
    }

    onExport() {
      this.chart.exportChartLocal({
        type: 'application/pdf',
        filename: 'chart'
      });
    }
  }
};
