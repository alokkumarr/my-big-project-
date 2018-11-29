import * as Highstock from 'highcharts/highstock';
import * as Highmaps from 'highcharts/highmaps';
import * as Highcharts from 'highcharts/highcharts';

import { NgModule } from '@angular/core';

require('highcharts/highcharts-more')(Highcharts);
require('highcharts/modules/exporting')(Highcharts);
require('highcharts/modules/no-data-to-display')(Highcharts);
require('highcharts/modules/offline-exporting')(Highcharts);
require('highcharts/modules/bullet')(Highcharts);

require('highcharts/modules/drag-panes')(Highstock);
require('highcharts/modules/exporting')(Highstock);
require('highcharts/modules/offline-exporting')(Highstock);

require('highcharts/modules/exporting')(Highmaps);
require('highcharts/modules/offline-exporting')(Highmaps);

import { ChartComponent } from './chart.component';
import { MapChartComponent } from './map-chart.component';

export const ChartsModule = 'components.charts';

const COMPONENTS = [
  ChartComponent,
  MapChartComponent
];
@NgModule({
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  exports: COMPONENTS
})
export class UChartModule {}
