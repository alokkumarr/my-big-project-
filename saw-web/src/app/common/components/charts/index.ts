import Highstock from 'highcharts/highstock';
import Highmaps from 'highcharts/highmaps';
import Highcharts from 'highcharts/highcharts';

import { NgModule } from '@angular/core';

import HighChartsExporting from 'highcharts/modules/exporting';
import HighChartsOfflineExporting from 'highcharts/modules/offline-exporting';
import HighChartDragPanes from 'highcharts/modules/drag-panes';

HighChartDragPanes(Highstock);
HighChartsExporting(Highstock);
HighChartsOfflineExporting(Highstock);

import HighChartsMore from 'highcharts/highcharts-more';
import HighChartsNoData from 'highcharts/modules/no-data-to-display';
import HighChartsBullet from 'highcharts/modules/bullet';

HighChartsMore(Highcharts);
HighChartsBullet(Highcharts);
HighChartsNoData(Highcharts);
HighChartsExporting(Highcharts);
HighChartsOfflineExporting(Highcharts);

HighChartsExporting(Highmaps);
HighChartsOfflineExporting(Highmaps);

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
