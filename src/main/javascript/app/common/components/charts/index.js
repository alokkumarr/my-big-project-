import * as angular from 'angular';
import * as Highstock from 'highcharts/highstock';
import * as Highcharts from 'highcharts/highcharts';
import * as more from 'highcharts/highcharts-more';

import * as exporting from 'highcharts/modules/exporting';
import * as noData from 'highcharts/modules/no-data-to-display';
import * as offlineExporting from 'highcharts/modules/offline-exporting';

import {chartComponent} from './chart.component';
import {businessTransactionVolumeService} from './business-transaction-volume.service';
import {businessTransactionVolumeChart} from './business-transaction-volume.chart';

export const ChartsModule = 'components.charts';

more(Highcharts);
noData(Highcharts);
exporting(Highcharts);
offlineExporting(Highcharts);

angular.module(ChartsModule, [])
        .constant('Highstock', Highstock)
        .constant('Highcharts', Highcharts)
        .factory('businessTransactionVolumeService', businessTransactionVolumeService)
        .component('chart', chartComponent)
        .component('businessTransactionVolumeChart', businessTransactionVolumeChart);
