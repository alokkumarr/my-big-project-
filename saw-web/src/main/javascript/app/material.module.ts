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
    MatSlideToggleModule,
    MatExpansionModule,
    MatButtonToggleModule,
    MatCardModule,
    MatStepperModule,
    MatSliderModule,
    MatTabsModule,
    MatDividerModule
} from '@angular/material';

import { MatMomentDateModule } from '@angular/material-moment-adapter';

require('@angular/material/prebuilt-themes/indigo-pink.css');
require('nouislider/distribute/nouislider.min.css');

import '../../../../themes/_angular_next.scss';
@NgModule({
  imports: [
    BrowserAnimationsModule,
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
    NouisliderModule
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
    NouisliderModule
  ]
})
export class MaterialModule { }
