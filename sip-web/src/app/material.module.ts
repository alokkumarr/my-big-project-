import { NgModule } from '@angular/core';
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
  MatProgressSpinnerModule,
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
  MatDividerModule,
  MatTreeModule
} from '@angular/material';
import { MatStepperModule } from '@angular/material/stepper';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMomentDateModule } from '@angular/material-moment-adapter';
import { ReactiveFormsModule } from '@angular/forms';

import './material-style.scss';

const MODULES = [
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
  MatProgressSpinnerModule,
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
  MatSnackBarModule,
  MatBadgeModule,
  MatTreeModule
];
@NgModule({
  imports: MODULES,
  providers: [MatIconRegistry],
  exports: MODULES
})
export class MaterialModule {}
