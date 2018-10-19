import * as Highstock from 'highcharts/highstock';
import * as Highcharts from 'highcharts/highcharts';

import { NgModule } from '@angular/core';

require('highcharts/highcharts-more')(Highcharts);
require('highcharts/modules/exporting')(Highcharts);
require('highcharts/modules/no-data-to-display')(Highcharts);
require('highcharts/modules/offline-exporting')(Highcharts);
require('highcharts/modules/drag-panes')(Highstock);
require('highcharts/modules/exporting')(Highstock);
require('highcharts/modules/offline-exporting')(Highstock);
require('highcharts/modules/bullet')(Highcharts);

import { ChartComponent } from './chart.component';

export const ChartsModule = 'components.charts';

@NgModule({
  declarations: [ChartComponent],
  entryComponents: [ChartComponent],
  exports: [ChartComponent]
})
export class UChartModule {}
