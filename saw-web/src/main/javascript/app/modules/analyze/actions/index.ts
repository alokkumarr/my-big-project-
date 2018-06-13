import { NgModule } from '@angular/core';
import { CommonModuleTs } from '../../../common';
import { AnalyzeActionsMenuComponent } from './analyze-actions-menu.component';
import { AnalyzeActionsService } from './analyze-actions.service';


@NgModule({
  imports: [CommonModuleTs],
  declarations: [AnalyzeActionsMenuComponent],
  entryComponents: [AnalyzeActionsMenuComponent],
  providers: [AnalyzeActionsService],
  exports: [
    AnalyzeActionsMenuComponent
  ]
})
export class AnalyzeActionsModule {}

export { AnalyzeActionsService };
