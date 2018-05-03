import { NgModule } from '@angular/core';

import { CommonModuleTs } from '../../../common';

import { AnalyzeViewComponent } from './analyze-view.component';
import { AnalyzeCardViewComponent } from './card-view';
import { AnalyzeListViewComponent } from './list-view';
import { AnalyzeCardComponent } from './card';

const COMPONENTS = [
  AnalyzeViewComponent,
  AnalyzeCardViewComponent,
  AnalyzeListViewComponent,
  AnalyzeCardComponent
];

@NgModule({
  imports: [CommonModuleTs],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  exports: [AnalyzeViewComponent]
})
export class AnalyzeViewModule {}
export { AnalyzeViewComponent };
