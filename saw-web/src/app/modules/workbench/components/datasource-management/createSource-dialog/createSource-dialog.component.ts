import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material';
import * as isUndefined from 'lodash/isUndefined';
import { DatasourceService } from '../../../services/datasource.service';
import { isUnique } from '../../../../../common/validators';

import { CHANNEL_TYPES } from '../../../wb-comp-configs';
import { TestConnectivityComponent } from '../test-connectivity/test-connectivity.component';

@Component({
  selector: 'createsource-dialog',
  templateUrl: './createSource-dialog.component.html',
  styleUrls: ['./createSource-dialog.component.scss']
})
export class CreateSourceDialogComponent {
  selectedSource = '';
  form: FormGroup;
  sources = CHANNEL_TYPES;
  firstStep: FormGroup;
  public detailsFormGroup: FormGroup;
  opType: 'create' | 'update' = 'create';
  show = false;
  dialogTitle = 'Create Data Channel';
  selectedStepIndex = 0;
  isTypeEditable = true;

  constructor(
    private _formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<CreateSourceDialogComponent>,
    private snackBar: MatSnackBar,
    private datasourceService: DatasourceService,
    @Inject(MAT_DIALOG_DATA) public channelData: any
  ) {
    if (isUndefined(this.channelData.length)) {
      this.opType = 'update';
      this.isTypeEditable = false;
      this.dialogTitle = 'Edit Channel';
      this.selectedStepIndex = 1;
      this.selectedSource = this.channelData.channelType;
    }
    this.createForm();
    if (isUndefined(this.channelData.length)) {
      this.firstStep.patchValue(this.channelData);
      this.detailsFormGroup.patchValue(this.channelData);
    }
  }

  createForm() {
    this.firstStep = this._formBuilder.group({
      channelType: ['', Validators.required]
    });

    const oldChannelName = this.opType === 'update' ? this.channelData.channelName : '';

    this.detailsFormGroup = this._formBuilder.group({
      channelName: ['', Validators.required, isUnique(this.datasourceService.isDuplicateChannel, v => v, oldChannelName)],
      hostName: ['', Validators.required],
      portNo: [
        '',
        Validators.compose([
          Validators.required,
          Validators.pattern('^[0-9]*$')
        ])
      ],
      userName: ['', Validators.required],
      password: ['', Validators.required],
      description: [''],
      accessType: ['R', Validators.required]
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

  togglePWD() {
    this.show = !this.show;
  }

  testChannel(formData) {
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
