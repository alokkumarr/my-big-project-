import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {MatSnackBar} from '@angular/material';

import { sourceTypes } from '../../../wb-comp-configs';
import * as get from 'lodash/get';

const template = require('./createSource-dialog.component.html');
require('./createSource-dialog.component.scss');

@Component({
  selector: 'createsource-dialog',
  template,
  styles: []
})
export class CreateSourceDialogComponent {
  selectedSource: string = '';
  form: FormGroup;
  folNamePattern = '[A-Za-z0-9]+';
  sources = sourceTypes;
  firstStep: FormGroup;
  public detailsFormGroup: FormGroup;

  constructor(
    private _formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<CreateSourceDialogComponent>,
    public snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.firstStep = this._formBuilder.group({
      firstStepCtrl: ['', Validators.required]
    });

    this.detailsFormGroup = this._formBuilder.group({
      hostNameCtrl: ['', Validators.required],
      portNoCtrl: ['', Validators.required],
      userNameCtrl: ['', Validators.required],
      passwordCtrl: ['', Validators.required]
    });
  }

  sourceSelected(source) {
    this.selectedSource = source;
    this.firstStep.controls.firstStepCtrl.reset(source);
  }

  onCancelClick(): void {
    this.dialogRef.close();
  }
}
