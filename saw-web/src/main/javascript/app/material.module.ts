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
  MatProgressBarModule,
  MatChipsModule,
  MatIconRegistry,
  MatListModule,
  MatCheckboxModule,
  MatMenuModule,
  MatTooltipModule,
  MatDatepickerModule,
  MatInputModule,
  MatToolbarModule,
  MatCardModule,
  MatGridListModule,
  MatStepperModule,
  MatSliderModule,
  MatSlideToggleModule,
  MatTabsModule,
  MatDividerModule,
  MatExpansionModule,
  MatButtonToggleModule,
  MatSnackBarModule
} from '@angular/material';

import { MatMomentDateModule } from '@angular/material-moment-adapter';

require('@angular/material/prebuilt-themes/indigo-pink.css');
import '../../../../themes/_angular_next.scss';
@NgModule({
  imports: [
    BrowserAnimationsModule,
    MatButtonModule,
    MatRadioModule,
    MatSelectModule,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    MatListModule,
    MatCheckboxModule,
    MatMenuModule,
    MatTooltipModule,
    MatDatepickerModule,
    MatInputModule,
    MatToolbarModule,
    MatCardModule,
    MatGridListModule,
    MatStepperModule,
    MatDividerModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatTabsModule,
    MatExpansionModule,
    MatButtonToggleModule,
    MatMomentDateModule,
    MatSnackBarModule
  ],
  providers: [MatIconRegistry],
  exports: [
    MatButtonModule,
    MatRadioModule,
    MatSelectModule,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    MatListModule,
    MatCheckboxModule,
    MatMenuModule,
    MatTooltipModule,
    MatDatepickerModule,
    MatInputModule,
    MatToolbarModule,
    MatCardModule,
    MatGridListModule,
    MatStepperModule,
    MatDividerModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatTabsModule,
    MatExpansionModule,
    MatButtonToggleModule,
    MatMomentDateModule,
    MatSnackBarModule
  ]
})
export class MaterialModule { }
