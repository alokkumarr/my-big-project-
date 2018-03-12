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
  MatAutocompleteModule,
  MatFormFieldModule,
  MatSidenavModule,
  MatDatepickerModule,
  MatProgressBarModule,
  MatChipsModule,
  // NoConflictStyleCompatibilityMode,
  MatIconRegistry,
  MatListModule,
  MatCheckboxModule,
  MatMenuModule,
  MatTooltipModule,
  MatInputModule,
  MatToolbarModule,
  MatSlideToggleModule
} from '@angular/material';
import {MatStepperModule} from '@angular/material/stepper';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatCardModule} from '@angular/material/card';
import {MatMomentDateModule} from '@angular/material-moment-adapter';

require('@angular/material/prebuilt-themes/indigo-pink.css');
require('nouislider/distribute/nouislider.min.css');

import '../../../../themes/_angular_next.scss';
@NgModule({
  imports: [
    // NoConflictStyleCompatibilityMode,
    BrowserAnimationsModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSidenavModule,
    MatRadioModule,
    MatSelectModule,
    MatStepperModule,
    MatAutocompleteModule,
    MatExpansionModule,
    MatMomentDateModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    NouisliderModule,
    MatListModule,
    MatCheckboxModule,
    MatMenuModule,
    MatTooltipModule,
    MatExpansionModule,
    MatButtonToggleModule,
    MatInputModule,
    MatCardModule,
    MatDatepickerModule,
    MatMomentDateModule,
    MatToolbarModule,
    MatSlideToggleModule
  ],
  providers: [MatIconRegistry],
  exports: [
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatRadioModule,
    MatStepperModule,
    MatSidenavModule,
    MatExpansionModule,
    MatSelectModule,
    MatAutocompleteModule,
    MatMomentDateModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    NouisliderModule,
    MatListModule,
    MatCheckboxModule,
    MatMenuModule,
    MatTooltipModule,
    MatExpansionModule,
    MatButtonToggleModule,
    MatInputModule,
    MatCardModule,
    MatDatepickerModule,
    MatMomentDateModule,
    MatToolbarModule,
    MatSlideToggleModule
  ]
})
export class MaterialModule {}
