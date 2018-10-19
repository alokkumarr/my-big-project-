import { NgModule } from '@angular/core';

import { AnalyzePublishDialogComponent } from './dialog/analyze-publish';
import { AnalyzeScheduleDialogComponent } from './dialog/analyze-schedule';
import { CronDatePickerComponent } from './cron-date-picker';
import { CronJobSchedularComponent } from './cron-job-schedular';
import { OwlDateTimeModule, OwlNativeDateTimeModule } from 'ng-pick-datetime';

import { CommonModuleTs } from '../../../common';

const COMPONENTS = [
  AnalyzePublishDialogComponent,
  AnalyzeScheduleDialogComponent,
  CronJobSchedularComponent,
  CronDatePickerComponent
];

@NgModule({
  imports: [
    CommonModuleTs,
    OwlDateTimeModule,
    OwlNativeDateTimeModule
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  exports: [AnalyzePublishDialogComponent]
})
export class AnalyzePublishDialogModule {}

export { AnalyzePublishDialogComponent };
