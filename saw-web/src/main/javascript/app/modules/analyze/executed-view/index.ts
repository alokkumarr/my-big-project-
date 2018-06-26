import { NgModule } from '@angular/core';
import { CommonModuleTs } from '../../../common';
import { AnalyzeActionsModule } from '../actions';
import { ExecutedViewComponent } from './executed-view.component';
import { ExecutedListComponent } from './list';
import { ExecutedReportViewComponent } from './report';
import { ExecuteService } from '../services/execute.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { AnalyzeExportService } from '../services/analyze-export.service';
import { AnalyzeFilterModule } from '../components/designer/filter';

const COMPONENTS = [
  ExecutedViewComponent,
  ExecutedListComponent,
  ExecutedReportViewComponent
];

@NgModule({
  imports: [
    CommonModuleTs,
    AnalyzeFilterModule,
    AnalyzeActionsModule
  ],
  declarations: [
    ...COMPONENTS
  ],
  entryComponents: COMPONENTS,
  providers: [
    ExecuteService,
    ToastService,
    AnalyzeExportService
  ],
  exports: [ExecutedViewComponent]
})
export class ExecutedViewModule {}
export { ExecutedViewComponent };
