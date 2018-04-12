import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MaterialModule } from '../../../../material.module';
import { AddWidgetModule } from '../add-widget/add-widget.module';

import { EditWidgetComponent } from './edit-widget.component';

const declarations = [EditWidgetComponent];

@NgModule({
  imports: [CommonModule, MaterialModule, AddWidgetModule],
  exports: [EditWidgetComponent],
  declarations,
  providers: []
})
export class EditWidgetModule {}
