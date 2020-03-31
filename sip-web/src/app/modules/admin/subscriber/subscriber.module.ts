import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AddSubscriberComponent } from './add-subscriber/add-subscriber.component';
import { ListSubscriberComponent } from './list-subscriber/list-subscriber.component';
import { MaterialModule } from 'src/app/material.module';
import { SubscriberRoutingModule } from './subscriber-routing.module';
import { DxDataGridModule } from 'devextreme-angular';
import { CommonModuleTs } from 'src/app/common';

@NgModule({
  declarations: [AddSubscriberComponent, ListSubscriberComponent],
  entryComponents: [AddSubscriberComponent, ListSubscriberComponent],
  imports: [
    CommonModule,
    SubscriberRoutingModule,
    MaterialModule,
    DxDataGridModule,
    CommonModuleTs
  ]
})
export class SubscriberModule {}
