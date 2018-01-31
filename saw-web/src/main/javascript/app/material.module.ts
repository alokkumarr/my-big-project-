declare const require: any;

import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  MatButtonModule,
  MatRadioModule,
  MatSelectModule,
  MatIconModule,
  MatDialogModule,
  MatFormFieldModule,
  MatSidenavModule,
  MatDatepickerModule,
  MatExpansionModule,
  MatProgressBarModule,
  MatChipsModule,
  MatInputModule
} from '@angular/material';

import { MatMomentDateModule } from '@angular/material-moment-adapter';

require('@angular/material/prebuilt-themes/indigo-pink.css');
import '../../../../themes/_angular_next.scss';

@NgModule({
  imports: [
    BrowserAnimationsModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSidenavModule,
    MatRadioModule,
    MatSelectModule,
    MatExpansionModule,
    MatMomentDateModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    MatInputModule
  ],
  exports: [
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatRadioModule,
    MatSidenavModule,
    MatExpansionModule,
    MatSelectModule,
    MatMomentDateModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    MatInputModule
  ]
})
export class MaterialModule {}
