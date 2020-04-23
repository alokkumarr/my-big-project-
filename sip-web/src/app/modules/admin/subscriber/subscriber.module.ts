import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListSubscriberComponent } from './list-subscriber/list-subscriber.component';
import { MaterialModule } from 'src/app/material.module';
import { SubscriberRoutingModule } from './subscriber-routing.module';
import { DxDataGridModule } from 'devextreme-angular';
import { CommonModuleTs } from 'src/app/common';
import { AddSubscriberModule } from './add-subscriber/add-subscriber.module';

@NgModule({
  declarations: [ListSubscriberComponent],
  entryComponents: [ListSubscriberComponent],
  imports: [
    CommonModule,
    SubscriberRoutingModule,
    MaterialModule,
    DxDataGridModule,
    CommonModuleTs,
    AddSubscriberModule
  ]
})
export class SubscriberModule {}
