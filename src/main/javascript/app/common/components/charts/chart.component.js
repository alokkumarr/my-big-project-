import defaultsDeep from 'lodash/defaultsDeep';
import forEach from 'lodash/forEach';
import filter from 'lodash/filter';
import set from 'lodash/set';
import get from 'lodash/get';
import isArray from 'lodash/isArray';
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
    constructor(Highcharts, $element, toastMessage) {
      'ngInject';

      Highcharts.setOptions(globalChartOptions);
      this.Highcharts = Highcharts;
      this.toastMessage = toastMessage;
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

        const pieNegatives = this.pieHasNegatives();
        if (pieNegatives.all) {
          this.chart.showNoData('Data unsuitable for Pie chart. Please use a different chart type.');
        } else if (pieNegatives.some) {
          this.toastMessage.error('Some negative values have been ommitted. Please use a different chart type if you wish to include them.');
        }
      }
    }

    /* Checks if the chart type is pie and whether the series has negative values.
       This is necessary because pie chart can't display negative values correctly,
       leading to all sorts of problems if not handled explicitly.

       Returns {all: Boolean, some: Boolean} depending on whether all values in all
       series are negative or only some of them.
    */
    pieHasNegatives() {
      const result = {all: true, some: false};
      if (get(this.config, 'chart.type') !== 'pie') {
        result.all = false;
        return result;
      }

      const series = get(this.config, 'series', []) || [];

      forEach(series, pie => {
        if (!isArray(pie.data)) {
          return;
        }

        const positives = filter(pie.data, slice => slice.y >= 0);

        if (positives.length === pie.data.length) {
          result.all = false;
          result.some = result.some || false;
        } else if (positives.length === 0) {
          result.all = result.all && true;
          result.some = true;
        } else {
          result.all = false;
          result.some = true;
        }
      });

      return result;
    }

    onExport() {
      this.chart.exportChartLocal({
        type: 'application/pdf',
        filename: 'chart'
      });
    }
  }
};
