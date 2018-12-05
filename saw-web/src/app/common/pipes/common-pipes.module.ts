import { NgModule } from '@angular/core';

import { FilterPipe } from './filter.pipe';
import { HighlightPipe } from './highlight.pipe';
import { ChangeCasePipe } from './change-case.pipe';
import { CheckedArtifactColumnFilterPipe } from './filterArtifactColumns.pipe';
import { TruncatePipe } from './truncate.pipe';

const PIPES = [
  FilterPipe,
  HighlightPipe,
  ChangeCasePipe,
  CheckedArtifactColumnFilterPipe,
  TruncatePipe
];

@NgModule({
  exports: PIPES,
  declarations: PIPES
})
export class CommonPipesModule {}
