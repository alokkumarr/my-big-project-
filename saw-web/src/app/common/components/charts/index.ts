import Highstock from 'highcharts/highstock';
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
HighChartsExporting(Highcharts);
HighChartsBullet(Highcharts);
HighChartsNoData(Highcharts);
HighChartsOfflineExporting(Highcharts);

import { ChartComponent } from './chart.component';

export const ChartsModule = 'components.charts';

@NgModule({
  declarations: [ChartComponent],
  entryComponents: [ChartComponent],
  exports: [ChartComponent]
})
export class UChartModule {}
