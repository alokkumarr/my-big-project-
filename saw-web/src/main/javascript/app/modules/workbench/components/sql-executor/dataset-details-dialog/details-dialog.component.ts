declare function require(string): string;

import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

import * as get from 'lodash/get';

const template = require('./details-dialog.component.html');
require('./details-dialog.component.scss');

@Component({
  selector: 'details-dialog',
  template,
  styles: []
})

export class DetailsDialogComponent {
  form: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<DetailsDialogComponent>
  ) {  }

  ngOnInit() {
    this.form = this.formBuilder.group({
      nameControl: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(18)]],
      descControl: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]]
    })
  }

  submit(form) {
    const details = {
      name: form.value.nameControl,
      desc: form.value.descControl
    }
    this.dialogRef.close(details);
  }

  onClose() {
    this.dialogRef.close(false);
  }
}
