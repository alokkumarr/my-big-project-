import { NgModule } from '@angular/core';

import { FilterPipe } from './filter.pipe';
import { HighlightPipe } from './highlight.pipe';
import { ChangeCasePipe } from './change-case.pipe';

const PIPES = [
  FilterPipe,
  HighlightPipe,
  ChangeCasePipe
];

@NgModule({
  exports: PIPES,
  declarations: PIPES
})
export class CommonPipesModule { }
