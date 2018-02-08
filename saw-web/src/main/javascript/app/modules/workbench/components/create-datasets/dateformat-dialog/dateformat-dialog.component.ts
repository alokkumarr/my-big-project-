import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormGroup, FormBuilder } from '@angular/forms';

import * as get from 'lodash/get';

const template = require('./dateformat-dialog.component.html');
require('./dateformat-dialog.component.scss');

@Component({
  selector: 'dateformat-dialog',
  template,
  styles: []
})

export class DateformatDialogComponent {
  form: FormGroup;
  private placeholder = '';
  private formatArr = '';

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<DateformatDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any
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
    })
  }

  submit(form) {
    this.dialogRef.close(`${form.value.dateformat}`);
  }
}
