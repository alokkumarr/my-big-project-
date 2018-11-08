import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material';
import * as isUndefined from 'lodash/isUndefined';

import { TestConnectivityComponent } from '../test-connectivity/test-connectivity.component';

@Component({
  selector: 'create-route-dialog',
  templateUrl: './create-route-dialog.component.html',
  styleUrls: ['./create-route-dialog.component.scss']
})
export class CreateRouteDialogComponent implements OnInit {
  public detailsFormGroup: FormGroup;
  crondetails: any = {};
  opType = 'create';
  dialogTitle = 'Create Route';

  constructor(
    private _formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<CreateRouteDialogComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public routeData: any
  ) {
    this.createForm();
  }

  createForm() {
    this.detailsFormGroup = this._formBuilder.group({
      routeName: ['', Validators.required],
      sourceLocation: ['', Validators.required],
      destinationLocation: ['', Validators.required],
      filePattern: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit() {
    if (isUndefined(this.routeData.length)) {
      this.opType = 'update';
      this.dialogTitle = 'Update Route';
      this.detailsFormGroup.patchValue(this.routeData);
      this.crondetails = this.routeData.schedulerExpression;
    }
  }

  onCancelClick(): void {
    this.dialogRef.close();
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

  onCronChanged(cronexpression) {
    this.crondetails = cronexpression;
  }

  createRoute(data) {
    const routeDetails = this.mapData(data);
    this.dialogRef.close({ routeDetails, opType: this.opType });
  }

  mapData(data) {
    const routeDetails = {
      routeName: data.routeName,
      sourceLocation: data.sourceLocation,
      destinationLocation: data.destinationLocation,
      filePattern: data.filePattern,
      schedulerExpression: this.crondetails,
      description: data.description
    };
    return routeDetails;
  }
}
