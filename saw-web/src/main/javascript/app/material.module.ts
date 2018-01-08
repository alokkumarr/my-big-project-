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
  // NoConflictStyleCompatibilityMode,
  MatIconRegistry,
  MatListModule,
  MatCheckboxModule,
  MatMenuModule,
  MatTooltipModule,
  MatDatepickerModule
} from '@angular/material';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatInputModule} from '@angular/material/input'
import {MatCardModule} from '@angular/material/card';
import {MatMomentDateModule} from '@angular/material-moment-adapter';

require('@angular/material/prebuilt-themes/indigo-pink.css');
import '../../../../themes/_angular_next.scss';
@NgModule({
  imports: [
    // NoConflictStyleCompatibilityMode,
    BrowserAnimationsModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatRadioModule,
    MatSelectModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    MatListModule,
    MatCheckboxModule,
    MatMenuModule,
    MatTooltipModule,
    MatExpansionModule,
    MatButtonToggleModule,
    MatInputModule,
    MatCardModule,
    MatDatepickerModule,
    MatMomentDateModule
  ],
  providers: [MatIconRegistry],
  exports: [
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatRadioModule,
    MatSelectModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    MatListModule,
    MatCheckboxModule,
    MatMenuModule,
    MatTooltipModule,
    MatExpansionModule,
    MatButtonToggleModule,
    MatInputModule,
    MatCardModule,
    MatDatepickerModule,
    MatMomentDateModule
  ]
})
export class MaterialModule {}
