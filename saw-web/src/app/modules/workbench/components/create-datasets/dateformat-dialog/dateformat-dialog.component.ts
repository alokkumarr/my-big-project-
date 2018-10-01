
import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormGroup, FormBuilder } from '@angular/forms';

import * as get from 'lodash/get';

const style = require('./dateformat-dialog.component.scss');

@Component({
  selector: 'dateformat-dialog',
  templateUrl: './dateformat-dialog.component.html',
  styles: [style]
})

export class DateformatDialogComponent {
  form: FormGroup;
  public placeholder = ''; // tslint:disable-line
  public formatArr = ''; // tslint:disable-line

  constructor(
    public formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<DateformatDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    if (get(data, 'placeholder')) {
      this.placeholder = data.placeholder;
    }
  }

  ngOnInit() {
    if (get(this.data, 'formatArr')) {
      this.formatArr = this.data.formatArr;
    }
    this.form = this.formBuilder.group({
      dateformat: this.data ? this.data.format : ''
    });
  }

  submit(form) {
    this.dialogRef.close(`${form.value.dateformat}`);
  }
}
