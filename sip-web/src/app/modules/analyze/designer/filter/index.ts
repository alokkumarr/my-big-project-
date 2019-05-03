import { NgModule } from '@angular/core';

import { CommonModuleTs } from '../../../../common';

import {
  DesignerFilterDialogComponent,
  DesignerFilterDialogData,
  DesignerFilterDialogResult
} from './dialog';
import { DesignerFilterRowComponent } from './row';
import { DesignerStringFilterComponent } from './string';
import { DesignerDateFilterComponent } from './date';
import { DesignerNumberFilterComponent } from './number';
import { FilterChipsComponent } from './chips-u';
export {
  DesignerFilterDialogComponent,
  DesignerFilterDialogData,
  DesignerFilterDialogResult
};

const COMPONENTS = [
  DesignerFilterRowComponent,
  DesignerStringFilterComponent,
  DesignerDateFilterComponent,
  DesignerNumberFilterComponent,
  DesignerFilterDialogComponent,
  FilterChipsComponent
];
@NgModule({
  imports: [CommonModuleTs],
  entryComponents: COMPONENTS,
  declarations: COMPONENTS,
  exports: [
    DesignerFilterDialogComponent,
    FilterChipsComponent
  ]
})
export class AnalyzeFilterModule {}
