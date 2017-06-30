import angular from 'angular';
import Highstock from 'highcharts/highstock';
import Highcharts from 'highcharts/highcharts';
import more from 'highcharts/highcharts-more';

import exporting from 'highcharts/modules/exporting';
import offlineExporting from 'highcharts/modules/offline-exporting';

import {chartComponent} from './chart.component';
import {businessTransactionVolumeService} from './business-transaction-volume.service';
import {businessTransactionVolumeChart} from './business-transaction-volume.chart';

export const ChartsModule = 'components.charts';

more(Highcharts);
exporting(Highcharts);
offlineExporting(Highcharts);

angular.module(ChartsModule, [])
        .constant('Highstock', Highstock)
        .constant('Highcharts', Highcharts)
        .factory('businessTransactionVolumeService', businessTransactionVolumeService)
        .component('chart', chartComponent)
        .component('businessTransactionVolumeChart', businessTransactionVolumeChart);
