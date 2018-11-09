import { NgModule } from '@angular/core';

import { AnalyzePublishDialogComponent } from './dialog/analyze-publish';
import { AnalyzeScheduleDialogComponent } from './dialog/analyze-schedule';

import { CommonModuleTs } from '../../../common';

const COMPONENTS = [
  AnalyzePublishDialogComponent,
  AnalyzeScheduleDialogComponent
];

@NgModule({
  imports: [
    CommonModuleTs
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  exports: [AnalyzePublishDialogComponent]
})
export class AnalyzePublishDialogModule {}

export { AnalyzePublishDialogComponent };
