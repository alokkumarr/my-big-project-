import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material';
import * as isUndefined from 'lodash/isUndefined';
import { DatasourceService } from '../../../services/datasource.service';

import { CHANNEL_TYPES } from '../../../wb-comp-configs';
import { TestConnectivityComponent } from '../test-connectivity/test-connectivity.component';

@Component({
  selector: 'createsource-dialog',
  templateUrl: './createSource-dialog.component.html',
  styleUrls: ['./createSource-dialog.component.scss']
})
export class CreateSourceDialogComponent implements OnInit {
  selectedSource = '';
  form: FormGroup;
  sources = CHANNEL_TYPES;
  firstStep: FormGroup;
  public detailsFormGroup: FormGroup;
  opType = 'create';
  show = false;

  constructor(
    private _formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<CreateSourceDialogComponent>,
    private snackBar: MatSnackBar,
    private datasourceService: DatasourceService,
    @Inject(MAT_DIALOG_DATA) public channelData: any
  ) {
    this.createForm();
  }

  createForm() {
    this.firstStep = this._formBuilder.group({
      channelType: ['', Validators.required]
    });

    this.detailsFormGroup = this._formBuilder.group({
      channelName: ['', Validators.required],
      hostName: ['', Validators.required],
      portNo: ['', Validators.required],
      userName: ['', Validators.required],
      password: ['', Validators.required],
      description: [''],
      accessType: ['R', Validators.required]
    });
  }

  ngOnInit() {
    if (isUndefined(this.channelData.length)) {
      this.opType = 'update';
      if (!isUndefined(this.channelData.password)) {
        this.decryptPWD(this.channelData.password);
      }
      this.selectedSource = this.channelData.channelType;
      this.firstStep.patchValue(this.channelData);
      this.detailsFormGroup.patchValue(this.channelData);
    }
  }

  sourceSelected(source) {
    if (source.supported) {
      this.selectedSource = source.uid;
      this.firstStep.controls.channelType.reset(source.uid);
    }
  }

  createSource(formData) {
    this.datasourceService.encryptPWD(formData.password).subscribe(data => {
      formData.password = data.data;
      const sourceDetails = this.mapData(formData);
      this.dialogRef.close({ sourceDetails, opType: this.opType });
    });
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

  decryptPWD(pwd) {
    this.datasourceService.decryptPWD(pwd).subscribe(data => {
      this.detailsFormGroup.controls.password.setValue(data.data);
    });
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
