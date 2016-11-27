import angular from 'angular';
import Highcharts from 'highcharts/highstock';
import 'highcharts-ng';

import {setSVGRenderer} from './chart-svg-renderer';
import {chartComponent} from './chart.component';
import {businessTransactionVolumeService} from './business-transaction-volume.service';
import {businessTransactionVolumeChart} from './business-transaction-volume.chart';

export const ChartsModule = 'components.charts';

setSVGRenderer(Highcharts);

// needed for highchart-ng
window.Highcharts = Highcharts; // eslint-disable-line

angular.module(ChartsModule, ['highcharts-ng'])
        .constant('Highcharts', Highcharts)
        .factory('businessTransactionVolumeService', businessTransactionVolumeService)
        .component('chart', chartComponent)
        .component('businessTransactionVolumeChart', businessTransactionVolumeChart);
