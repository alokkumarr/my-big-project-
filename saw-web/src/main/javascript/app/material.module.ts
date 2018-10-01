import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NouisliderModule } from 'ng2-nouislider';
import {
  MatButtonModule,
  MatRadioModule,
  MatSelectModule,
  MatIconModule,
  MatDialogModule,
  MatAutocompleteModule,
  MatFormFieldModule,
  MatSidenavModule,
  MatDatepickerModule,
  MatProgressBarModule,
  MatChipsModule,
  MatIconRegistry,
  MatListModule,
  MatCheckboxModule,
  MatMenuModule,
  MatTooltipModule,
  MatInputModule,
  MatToolbarModule,
  MatSlideToggleModule,
  MatSliderModule,
  MatTabsModule,
  MatDividerModule
} from '@angular/material';
import { MatStepperModule } from '@angular/material/stepper';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar } from '@angular/material';

import { MatMomentDateModule } from '@angular/material-moment-adapter';
import { ReactiveFormsModule } from '@angular/forms';

require('@angular/material/prebuilt-themes/indigo-pink.css');
require('nouislider/distribute/nouislider.min.css');

import '../../../../themes/_angular_next.scss';
@NgModule({
  imports: [
    BrowserAnimationsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatRadioModule,
    MatSelectModule,
    MatIconModule,
    MatDialogModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    MatSidenavModule,
    MatDatepickerModule,
    MatProgressBarModule,
    MatChipsModule,
    MatListModule,
    MatCheckboxModule,
    MatMenuModule,
    MatTooltipModule,
    MatInputModule,
    MatToolbarModule,
    MatSlideToggleModule,
    MatExpansionModule,
    MatButtonToggleModule,
    MatCardModule,
    MatStepperModule,
    MatSliderModule,
    MatTabsModule,
    MatDividerModule,
    MatMomentDateModule,
    MatToolbarModule,
    MatSlideToggleModule,
    MatDividerModule,
    NouisliderModule,
    MatSnackBar
  ],
  providers: [MatIconRegistry],
  exports: [
    MatButtonModule,
    MatRadioModule,
    MatSelectModule,
    MatIconModule,
    MatDialogModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    MatSidenavModule,
    MatDatepickerModule,
    MatProgressBarModule,
    MatChipsModule,
    MatListModule,
    MatCheckboxModule,
    MatMenuModule,
    MatTooltipModule,
    MatInputModule,
    MatToolbarModule,
    MatSlideToggleModule,
    MatExpansionModule,
    MatButtonToggleModule,
    MatCardModule,
    MatStepperModule,
    MatSliderModule,
    MatTabsModule,
    MatDividerModule,
    MatMomentDateModule,
    MatToolbarModule,
    MatSlideToggleModule,
    MatDividerModule,
    NouisliderModule,
    MatSnackBar
  ]
})
export class MaterialModule {}
