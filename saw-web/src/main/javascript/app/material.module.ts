declare const require: any;

import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NouisliderModule } from 'ng2-nouislider';
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
require('nouislider/distribute/nouislider.min.css');

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
    MatInputModule,
    NouisliderModule
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
    MatInputModule,
    NouisliderModule
  ]
})
export class MaterialModule {}
