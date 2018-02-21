declare const require: any;

import {downgradeComponent} from '@angular/upgrade/static';
import * as angular from 'angular';
import * as Highstock from 'highcharts/highstock';
import * as Highcharts from 'highcharts/highcharts';

require('highcharts/highcharts-more')(Highcharts);
require('highcharts/modules/exporting')(Highcharts);
require('highcharts/modules/no-data-to-display')(Highcharts);
require('highcharts/modules/offline-exporting')(Highcharts);
require('highcharts/modules/drag-panes')(Highstock);
require('highcharts/modules/exporting')(Highstock);
require('highcharts/modules/offline-exporting')(Highstock);

import {ChartComponent} from './chart.component';
import {businessTransactionVolumeService} from './business-transaction-volume.service';
import {businessTransactionVolumeChart} from './business-transaction-volume.chart';

export const ChartsModule = 'components.charts';

angular.module(ChartsModule, [])
        .constant('Highstock', Highstock)
        .constant('Highcharts', Highcharts)
        .factory('businessTransactionVolumeService', businessTransactionVolumeService)
        .directive('chart', downgradeComponent({component: ChartComponent}) as angular.IDirectiveFactory)
        .component('businessTransactionVolumeChart', businessTransactionVolumeChart);
