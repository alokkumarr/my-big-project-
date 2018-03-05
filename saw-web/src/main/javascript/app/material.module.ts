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
  MatIconRegistry,
  MatListModule,
  MatCheckboxModule,
  MatMenuModule,
  MatTooltipModule,
  MatInputModule,
  MatToolbarModule,
<<<<<<< HEAD
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
=======
  MatSlideToggleModule
>>>>>>> 47174b002db30c2b4e1bb29001816d702bd958f3
} from '@angular/material';

import { MatMomentDateModule } from '@angular/material-moment-adapter';

require('@angular/material/prebuilt-themes/indigo-pink.css');
require('nouislider/distribute/nouislider.min.css');

import '../../../../themes/_angular_next.scss';
@NgModule({
  imports: [
    BrowserAnimationsModule,
    MatButtonModule,
<<<<<<< HEAD
    MatRadioModule,
    MatSelectModule,
    MatIconModule,
    MatDialogModule,
=======
    MatIconModule,
    MatDialogModule,
    MatSidenavModule,
    MatRadioModule,
    MatSelectModule,
    MatAutocompleteModule,
    MatExpansionModule,
    MatMomentDateModule,
    MatDatepickerModule,
>>>>>>> 47174b002db30c2b4e1bb29001816d702bd958f3
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    NouisliderModule,
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
<<<<<<< HEAD
    MatSnackBarModule
=======
    MatToolbarModule,
    MatSlideToggleModule
>>>>>>> 47174b002db30c2b4e1bb29001816d702bd958f3
  ],
  providers: [MatIconRegistry],
  exports: [
    MatButtonModule,
    MatRadioModule,
    MatSidenavModule,
    MatExpansionModule,
    MatSelectModule,
<<<<<<< HEAD
    MatIconModule,
    MatDialogModule,
=======
    MatAutocompleteModule,
    MatMomentDateModule,
    MatDatepickerModule,
>>>>>>> 47174b002db30c2b4e1bb29001816d702bd958f3
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    NouisliderModule,
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
<<<<<<< HEAD
    MatSnackBarModule
=======
    MatToolbarModule,
    MatSlideToggleModule
>>>>>>> 47174b002db30c2b4e1bb29001816d702bd958f3
  ]
})
export class MaterialModule { }
