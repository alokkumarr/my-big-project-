import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import { DATE_FORMATS } from '../../consts';
import * as isUndefined from 'lodash/isUndefined';

const template = require('./date-format-dialog.component.html');
const style = require('./date-format-dialog.component.scss');

@Component({
  selector: 'date-format-dialog',
  template,
  styles: [
    `:host {
      display: block;
      padding: 10px;
    }`,
    style
  ]
})
export class DateFormatDialogComponent {
  public dateFormats;

  constructor(
    private _dialogRef: MatDialogRef<DateFormatDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      format: string;
      availableFormats?: Array<any>;
    }
  ) {
    this.dateFormats = this.data.availableFormats || DATE_FORMATS;
    if (isUndefined(this.data.format)) {
      this.data.format = 'yyyy-MM-dd';
    }
  }

  close() {
    this._dialogRef.close();
  }

  applyFormat() {
    this._dialogRef.close(this.data.format);
  }

  onFormatChange(format) {
    this.data.format = format;
  }
}
