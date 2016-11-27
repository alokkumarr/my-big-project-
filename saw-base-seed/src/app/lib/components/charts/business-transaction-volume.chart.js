import values from 'lodash/values';
import set from 'lodash/set';
import template from './business-transaction-volume.chart.html';

export const businessTransactionVolumeChart = {
  template,
  controller: class BusinessTransactionVolumeChartController {
    constructor(businessTransactionVolumeService) {
      'ngInject';
      this.businessTransactionVolumeService = businessTransactionVolumeService;

      this.categoryViews = values(this.businessTransactionVolumeService.VIEWS);
      this.categoryView = businessTransactionVolumeService.VIEWS.MONTHLY;
      this.getChartData(this.categoryView);
      this.transactionVolumeChartOptions = this.getChartOptions();
    }

    onViewSelected(view) {
      this.getChartData(view);
    }

    getChartData(view) {
      this.businessTransactionVolumeService.getChartData(view).then(data => {
        this.setDynamicData(data);
      });
    }

    setDynamicData(data) {
      set(this.transactionVolumeChartOptions.dynamic, 'xAxis.categories', data.categories);
      set(this.transactionVolumeChartOptions.dynamic.series[0], 'data', data.series);
    }

    getChartOptions() {
      return {
        static: {
          xAxis: {
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
            type: 'line',
            marginRight: 120
          },
          scrollbar: {
            enabled: true,
            barBackgroundColor: 'gray',
            barBorderRadius: 7,
            barBorderWidth: 0,
            buttonBackgroundColor: 'gray',
            buttonBorderWidth: 0,
            buttonArrowColor: 'yellow',
            buttonBorderRadius: 7,
            rifleColor: 'yellow',
            trackBackgroundColor: 'white',
            trackBorderWidth: 1,
            trackBorderColor: 'silver',
            trackBorderRadius: 7
          }
        },
        dynamic: {
          series: [{
            name: 'Data',
            data: []
          }]
        }
      };
    }
  }
};
