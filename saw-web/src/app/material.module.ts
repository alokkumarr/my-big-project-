import { NgModule } from '@angular/core';
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
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMomentDateModule } from '@angular/material-moment-adapter';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [
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
    MatSnackBarModule,
    MatBadgeModule
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
    MatSnackBarModule,
    MatBadgeModule
  ]
})
export class MaterialModule {}
