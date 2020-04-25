import { NgModule } from '@angular/core';

import { CommonModuleTs } from '../../../../common';

import {
  DesignerFilterDialogComponent,
  DesignerFilterDialogData,
  DesignerFilterDialogResult
} from './dialog';

export {
  DesignerFilterDialogComponent,
  DesignerFilterDialogData,
  DesignerFilterDialogResult
};

const COMPONENTS = [
  DesignerFilterDialogComponent
];
@NgModule({
  imports: [CommonModuleTs],
  entryComponents: COMPONENTS,
  declarations: COMPONENTS,
  exports: [
    DesignerFilterDialogComponent
  ]
})
export class AnalyzeFilterModule {}
