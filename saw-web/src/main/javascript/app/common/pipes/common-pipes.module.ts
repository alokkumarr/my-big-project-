import { NgModule } from '@angular/core';

import { FilterPipe } from './filter.pipe';
import { HighlightPipe } from './highlight.pipe';

@NgModule({
  exports: [
    FilterPipe,
    HighlightPipe
  ],
  declarations: [
    FilterPipe,
    HighlightPipe
  ]
})
export class CommonPipesModule { }
