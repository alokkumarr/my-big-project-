declare const require: any;
import {
  Component,
  Inject
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import { DATE_FORMATS } from '../../consts';

const template = require('./date-format-dialog.component.html');
require('./date-format-dialog.component.scss');

@Component({
  selector: 'date-format-dialog',
  template
})
export class DateFormatDialogComponent {

  public dateFormats;

  constructor(
    private _dialogRef: MatDialogRef<DateFormatDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      format: string
    }
  ) {
    this.dateFormats = DATE_FORMATS;
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
