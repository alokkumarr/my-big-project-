import { Component, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material';
import { TestConnectivityComponent } from '../test-connectivity/test-connectivity.component';
import { Schedule } from '../../../models/workbench.interface';

@Component({
  selector: 'create-route-dialog',
  templateUrl: './create-route-dialog.component.html',
  styleUrls: ['./create-route-dialog.component.scss']
})
export class CreateRouteDialogComponent implements OnInit {
  public detailsFormGroup: FormGroup;
  currentDate: Date = new Date();
  scheduleData: Schedule[];

  constructor(
    private _formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<CreateRouteDialogComponent>,
    private snackBar: MatSnackBar
  ) {
    this.scheduleData = [];
  }

  ngOnInit() {
    this.detailsFormGroup = this._formBuilder.group({
      routeNameCtrl: ['', Validators.required],
      sourceLocationCtrl: ['', Validators.required],
      destinationLocationCtrl: ['', Validators.required],
      filePatternCtrl: ['', Validators.required],
      descriptionCtrl: [''],
      startDateCtrl: ['', Validators.required],
      endDateCtrl: ['', Validators.required]
    });
  }

  onCancelClick(): void {
    this.dialogRef.close();
  }

  onAppointmentFormCreated(data) {
    const formData = data;
  }

  testConnection() {
    this.dialogRef.updatePosition({ top: '30px' });
    const snackBarRef = this.snackBar.openFromComponent(
      TestConnectivityComponent,
      {
        horizontalPosition: 'center',
        panelClass: ['mat-elevation-z9', 'testConnectivityClass']
      }
    );

    snackBarRef.afterDismissed().subscribe(() => {
      this.dialogRef.updatePosition({ top: '' });
    });
  }
}
