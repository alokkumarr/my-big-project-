import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { AddSubscriberComponent } from './add-subscriber.component';
import { MaterialModule } from 'src/app/material.module';
import { CommonModuleTs } from 'src/app/common';

@NgModule({
  declarations: [AddSubscriberComponent],
  entryComponents: [AddSubscriberComponent],
  exports: [AddSubscriberComponent],
  imports: [CommonModule, CommonModuleTs, MaterialModule, ReactiveFormsModule]
})
export class AddSubscriberModule {}
