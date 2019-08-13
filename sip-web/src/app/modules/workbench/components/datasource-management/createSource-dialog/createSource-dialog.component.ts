import { Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material';
import * as isUndefined from 'lodash/isUndefined';
import { DatasourceService } from '../../../services/datasource.service';

import { CHANNEL_TYPES, CHANNEL_UID } from '../../../wb-comp-configs';
import { TestConnectivityComponent } from '../test-connectivity/test-connectivity.component';
import {
  DetailFormable,
  CHANNEL_OPERATION
} from '../../../models/workbench.interface';

@Component({
  selector: 'createsource-dialog',
  templateUrl: './createSource-dialog.component.html',
  styleUrls: ['./createSource-dialog.component.scss']
})
export class CreateSourceDialogComponent {
  selectedSource: CHANNEL_UID = CHANNEL_UID.NONE;
  sources = CHANNEL_TYPES;
  uid = CHANNEL_UID;
  firstStep: FormGroup;
  opType: CHANNEL_OPERATION = CHANNEL_OPERATION.CREATE;
  dialogTitle = 'Create Data Channel';
  selectedStepIndex = 0;
  isTypeEditable = true;

  // All channel forms implement this interface to guarantee common properties
  @ViewChild('channelDetails') channelDetails: DetailFormable;

  constructor(
    private _formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<CreateSourceDialogComponent>,
    private snackBar: MatSnackBar,
    private datasourceService: DatasourceService,
    @Inject(MAT_DIALOG_DATA) public channelData: any
  ) {
    if (isUndefined(this.channelData.length)) {
      this.opType = CHANNEL_OPERATION.UPDATE;
      this.isTypeEditable = false;
      this.dialogTitle = 'Edit Channel';
      this.selectedStepIndex = 1;
      this.selectedSource = this.channelData.channelType;
    }
    this.createForm();
    if (isUndefined(this.channelData.length)) {
      this.firstStep.patchValue(this.channelData);
    }
  }

  createForm() {
    this.firstStep = this._formBuilder.group({
      channelType: ['', Validators.required]
    });
  }

  sourceSelected(source) {
    if (source.supported) {
      this.selectedSource = source.uid;
      this.firstStep.controls.channelType.reset(source.uid);
    }
  }

  createSource(formData) {
    const sourceDetails = this.mapData(formData);
    this.dialogRef.close({ sourceDetails, opType: this.opType });
  }

  mapData(data) {
    const sourceDetails = {
      channelName: data.channelName,
      channelType: this.selectedSource,
      hostName: data.hostName,
      portNo: data.portNo,
      accessType: data.accessType,
      userName: data.userName,
      password: data.password,
      description: data.description
    };
    return sourceDetails;
  }

  onCancelClick(): void {
    this.dialogRef.close();
  }

  get isDetailsFormValid() {
    return this.channelDetails && this.channelDetails.valid;
  }

  get detailsFormValue() {
    return this.channelDetails && this.channelDetails.value;
  }

  testChannel(formData) {
    console.log(formData);
    const channelData = {
      channelType: this.selectedSource,
      hostName: formData.hostName,
      password: formData.password,
      portNo: formData.portNo,
      userName: formData.userName
    };
    this.datasourceService.testChannelWithBody(channelData).subscribe(data => {
      this.showConnectivityLog(data);
    });
  }

  showConnectivityLog(logData) {
    this.dialogRef.updatePosition({ top: '30px' });
    const snackBarRef = this.snackBar.openFromComponent(
      TestConnectivityComponent,
      {
        data: logData,
        horizontalPosition: 'center',
        panelClass: ['mat-elevation-z9', 'testConnectivityClass']
      }
    );

    snackBarRef.afterDismissed().subscribe(() => {
      this.dialogRef.updatePosition({ top: '' });
    });
  }
}
