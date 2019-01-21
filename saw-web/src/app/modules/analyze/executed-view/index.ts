import { NgModule } from '@angular/core';
import { CommonModuleTs } from '../../../common';
import { AnalyzeActionsModule } from '../actions';
import { ExecutedViewComponent } from './executed-view.component';
import { ExecutedListComponent } from './list';
import { ExecutedReportViewComponent } from './report';
import { ExecutedPivotViewComponent } from './pivot';
import { ExecutedChartViewComponent } from './chart';
import { ExecutedMapViewComponent } from './map';
import { ExecutedMapChartViewComponent } from './map-chart';
import { ChartService, ToastService } from '../../../common/services';
import { ExecuteService } from '../services/execute.service';
import { UChartModule } from '../../../common/components/charts';
import { AnalyzeExportService } from '../services/analyze-export.service';
import { AnalyzeFilterModule } from '../designer/filter';

const COMPONENTS = [
  ExecutedViewComponent,
  ExecutedListComponent,
  ExecutedReportViewComponent,
  ExecutedPivotViewComponent,
  ExecutedChartViewComponent,
  ExecutedMapViewComponent,
  ExecutedMapChartViewComponent
];

@NgModule({
  imports: [
    CommonModuleTs,
    AnalyzeFilterModule,
    AnalyzeActionsModule,
    UChartModule
  ],
  declarations: [
    ...COMPONENTS
  ],
  entryComponents: COMPONENTS,
  providers: [
    ExecuteService,
    ToastService,
    AnalyzeExportService,
    ChartService
  ],
  exports: [ExecutedViewComponent]
})
export class ExecutedViewModule {}
export {
  ExecutedViewComponent
};
