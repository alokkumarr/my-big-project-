import values from 'lodash/values';
import upperFirst from 'lodash/upperFirst';
import {Subject} from 'rxjs/Subject';

import {VIEWS} from './business-transaction-volume.service';
import {UPDATE_PATHS} from './chart.component';
import template from './business-transaction-volume.chart.html';

const MAX_POINTS_ON_X_AXIS = {
  [VIEWS.DAILY]: 32,
  [VIEWS.WEEKLY]: 20,
  [VIEWS.MONTHLY]: null,
  [VIEWS.QUARTERLY]: null
};

export const businessTransactionVolumeChart = {
  template,
  controller: class BusinessTransactionVolumeChartController {
    constructor(businessTransactionVolumeService) {
      'ngInject';
      this.businessTransactionVolumeService = businessTransactionVolumeService;

      this.categoryViews = values(VIEWS);
      this.categoryView = VIEWS.MONTHLY;
      this.transactionVolumeChartOptions = this.getChartOptions();
      this.updater$ = new Subject();

      // fetch asynch data
      this.fetchChartData(this.categoryView);
    }

    onViewSelected(view) {
      this.fetchChartData(view);
    }

    fetchChartData(view) {
      this.businessTransactionVolumeService.getChartData(view).then(data => {
        this.setDynamicData(data, view);
      });
    }

    setDynamicData(data, view) {
      const xAxis = {
        title: {
          text: upperFirst(view)
        },
        max: MAX_POINTS_ON_X_AXIS[view],
        categories: data.categories
      };
      const series = {
        name: 'Revenue',
        data: data.series
      };
      // updates for different paths on the Highcharts config object
      const updates = [{
        path: UPDATE_PATHS.SERIES,
        data: series
      }, {
        path: UPDATE_PATHS.X_AXIS,
        data: xAxis
      }];

      // push the new data to the chart
      this.updater$.next(updates);
    }

    $onDestroy() {
      this.updater$.complete();
    }

    getChartOptions() {
      return {
        navigator: {
          adaptToUpdatedData: true
        },
        scrollbar: {
          enabled: true,
          showFull: false
        },
        xAxis: {
          title: {
            text: upperFirst(VIEWS.MONTHLY)
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
          type: 'line'
        },
        series: [{
          name: 'Data',
          data: []
        }]
      };
    }
  }
};
