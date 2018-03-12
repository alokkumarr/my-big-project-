import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../../../../material.module';

import { AddWidgetComponent } from './add-widget.component';
import { WidgetTypeComponent } from './widget-type/widget-type.component';
import { WidgetCategoryComponent } from './widget-category/widget-category.component';


const components = [
  AddWidgetComponent,
  WidgetTypeComponent,
  WidgetCategoryComponent
];

@NgModule({
  imports: [
    CommonModule,
    MaterialModule
  ],
  exports: [
    AddWidgetComponent
  ],
  entryComponents: components,
  declarations: components,
  providers: []
})
export class AddWidgetModule { }
