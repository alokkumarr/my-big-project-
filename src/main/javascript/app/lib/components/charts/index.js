import angular from 'angular';
import Highstock from 'highcharts/highstock';
import Highcharts from 'highcharts/highcharts';

// import 'highcharts/modules/exporting';
// import 'highcharts/modules/offline-exporting';
// import 'highcharts/highcharts-more';

import {chartComponent} from './chart.component';
import {businessTransactionVolumeService} from './business-transaction-volume.service';
import {businessTransactionVolumeChart} from './business-transaction-volume.chart';

export const ChartsModule = 'components.charts';

angular.module(ChartsModule, [])
        .constant('Highstock', Highstock)
        .constant('Highcharts', Highcharts)
        .factory('businessTransactionVolumeService', businessTransactionVolumeService)
        .component('chart', chartComponent)
        .component('businessTransactionVolumeChart', businessTransactionVolumeChart);
