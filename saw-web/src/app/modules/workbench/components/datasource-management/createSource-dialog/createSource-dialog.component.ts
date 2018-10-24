import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material';
import * as isUndefined from 'lodash/isUndefined';
import { sourceTypes } from '../../../wb-comp-configs';
import { TestConnectivityComponent } from '../test-connectivity/test-connectivity.component';

@Component({
  selector: 'createsource-dialog',
  templateUrl: './createSource-dialog.component.html',
  styleUrls: ['./createSource-dialog.component.scss']
})
export class CreateSourceDialogComponent {
  selectedSource: string = '';
  form: FormGroup;
  sources = sourceTypes;
  firstStep: FormGroup;
  public detailsFormGroup: FormGroup;

  constructor(
    private _formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<CreateSourceDialogComponent>,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.firstStep = this._formBuilder.group({
      firstStepCtrl: ['', Validators.required]
    });

    this.detailsFormGroup = this._formBuilder.group({
      sourceNameCtrl: ['', Validators.required],
      hostNameCtrl: ['', Validators.required],
      portNoCtrl: ['', Validators.required],
      userNameCtrl: ['', Validators.required],
      passwordCtrl: ['', Validators.required],
      descriptionCtrl: [''],
      accessTypeCtrl: ['R', Validators.required]
    });
  }

  sourceSelected(source) {
    this.selectedSource = source;
    this.firstStep.controls.firstStepCtrl.reset(source);
  }

  createSource(data) {
    const sourceDetails = {
      sourceName: data.sourceNameCtrl,
      sourceType: this.selectedSource,
      hostName: data.hostNameCtrl,
      portNo: data.portNoCtrl,
      accessType: data.accessTypeCtrl,
      username: data.userNameCtrl,
      password: data.passwordCtrl,
      description: data.descriptionCtrl
    };
    this.dialogRef.close(sourceDetails);
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
}
