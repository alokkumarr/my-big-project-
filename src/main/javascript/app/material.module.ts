import { NgModule } from '@angular/core';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {
  MatButtonModule,
  MatIconModule,
  MatDialogModule
} from '@angular/material';
require('@angular/material/prebuilt-themes/indigo-pink.css');

@NgModule({
  imports: [
    NoopAnimationsModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule
  ],
  exports: [
    MatButtonModule,
    MatIconModule,
    MatDialogModule
  ]
})
export class MaterialModule {}