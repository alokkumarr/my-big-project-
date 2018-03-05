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
<<<<<<< HEAD
=======
  MatSlideToggleModule,
  MatExpansionModule,
  MatButtonToggleModule,
>>>>>>> ac1d79e1e5d9ffdf3ec25e91f831c57fef2a0c3a
  MatCardModule,
  MatStepperModule,
  MatSliderModule,
  MatTabsModule,
<<<<<<< HEAD
  MatDividerModule,
  MatExpansionModule,
  MatButtonToggleModule,
  MatSnackBarModule
=======
  MatSlideToggleModule
>>>>>>> 47174b002db30c2b4e1bb29001816d702bd958f3
=======
  MatDividerModule
>>>>>>> ac1d79e1e5d9ffdf3ec25e91f831c57fef2a0c3a
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
<<<<<<< HEAD
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
=======
    MatAutocompleteModule,
>>>>>>> ac1d79e1e5d9ffdf3ec25e91f831c57fef2a0c3a
    MatFormFieldModule,
    MatSidenavModule,
    MatDatepickerModule,
    MatProgressBarModule,
    MatChipsModule,
    NouisliderModule,
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
<<<<<<< HEAD
<<<<<<< HEAD
    MatSnackBarModule
=======
    MatToolbarModule,
    MatSlideToggleModule
>>>>>>> 47174b002db30c2b4e1bb29001816d702bd958f3
=======
    NouisliderModule
>>>>>>> ac1d79e1e5d9ffdf3ec25e91f831c57fef2a0c3a
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
<<<<<<< HEAD
=======
    MatAutocompleteModule,
    MatMomentDateModule,
    MatDatepickerModule,
>>>>>>> 47174b002db30c2b4e1bb29001816d702bd958f3
=======
    MatAutocompleteModule,
>>>>>>> ac1d79e1e5d9ffdf3ec25e91f831c57fef2a0c3a
    MatFormFieldModule,
    MatSidenavModule,
    MatDatepickerModule,
    MatProgressBarModule,
    MatChipsModule,
    NouisliderModule,
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
<<<<<<< HEAD
<<<<<<< HEAD
    MatSnackBarModule
=======
    MatToolbarModule,
    MatSlideToggleModule
>>>>>>> 47174b002db30c2b4e1bb29001816d702bd958f3
=======
    NouisliderModule
>>>>>>> ac1d79e1e5d9ffdf3ec25e91f831c57fef2a0c3a
  ]
})
export class MaterialModule { }
