import { NgModule } from '@angular/core';

import { AnalyzePublishDialogComponent } from './dialog';
import { CronDatePickerComponent } from './cron-date-picker';
import { CronJobSchedularComponent } from './cron-job-schedular';
import { OwlDateTimeModule, OwlNativeDateTimeModule } from 'ng-pick-datetime';

import { CommonModuleTs } from '../../../common';

const COMPONENTS = [
  AnalyzePublishDialogComponent,
  CronJobSchedularComponent,
  CronDatePickerComponent
]

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
