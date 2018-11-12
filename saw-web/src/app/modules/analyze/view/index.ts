import { NgModule } from '@angular/core';
import { CommonModuleTs } from '../../../common';
import { CustomIconService } from '../../../common/services';
import { AnalyzeActionsModule } from '../actions';
import { AnalyzeViewComponent } from './analyze-view.component';
import { AnalyzeCardComponent } from './card';
import { AnalyzeCardViewComponent } from './card-view';
import { AnalyzeListViewComponent } from './list-view';
import { AnalyzeNewDialogComponent } from './new-dialog';
import { AnalysesFilterPipe } from './analyses-filter.pipe';



const COMPONENTS = [
  AnalyzeViewComponent,
  AnalyzeCardViewComponent,
  AnalyzeListViewComponent,
  AnalyzeCardComponent,
  AnalyzeNewDialogComponent
];

@NgModule({
  imports: [
    CommonModuleTs,
    AnalyzeActionsModule
  ],
  declarations: [
    ...COMPONENTS,
    AnalysesFilterPipe
  ],
  providers: [CustomIconService],
  entryComponents: COMPONENTS,
  exports: [AnalyzeViewComponent]
})
export class AnalyzeViewModule {}
export { AnalyzeViewComponent };
