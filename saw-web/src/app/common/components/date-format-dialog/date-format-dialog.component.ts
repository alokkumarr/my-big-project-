import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import { DATE_FORMATS } from '../../consts';
import * as isUndefined from 'lodash/isUndefined';

@Component({
  selector: 'date-format-dialog',
  templateUrl: './date-format-dialog.component.html',
  styleUrls: ['./date-format-dialog.component.scss']
})
export class DateFormatDialogComponent {
  public dateFormats;

  constructor(
    public _dialogRef: MatDialogRef<DateFormatDialogComponent>,
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

  trackByIndex(index) {
    return index;
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
