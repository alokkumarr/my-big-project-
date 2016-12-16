import angular from 'angular';
import Highstock from 'highcharts/highstock';

import {chartComponent} from './chart.component';
import {businessTransactionVolumeService} from './business-transaction-volume.service';
import {businessTransactionVolumeChart} from './business-transaction-volume.chart';

export const ChartsModule = 'components.charts';

angular.module(ChartsModule, [])
        .constant('Highstock', Highstock)
        .factory('businessTransactionVolumeService', businessTransactionVolumeService)
        .component('chart', chartComponent)
        .component('businessTransactionVolumeChart', businessTransactionVolumeChart);
