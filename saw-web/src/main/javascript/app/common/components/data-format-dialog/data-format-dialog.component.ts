declare const require: any;
import {
  Component,
  Input,
  Inject
} from '@angular/core';
import * as currencyCodes from 'currency-codes/data.js';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

const template = require('./data-format-dialog.component.html');
require('./data-format-dialog.component.scss');

@Component({
  selector: 'data-format-dialog',
  template
})
export class DataFormatDialogComponent {

  public currencyCodes = currencyCodes;

  constructor(
    private _dialogRef: MatDialogRef<DataFormatDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {format: any}
  ) {}

  ngOnInit() {
    if (!this.data.format) {
      this.data.format = {};
    }
  }

  close() {
    this._dialogRef.close();
  }
  format() {
    this._dialogRef.close(this.data.format);
  }

  onCommaSeparatorChange(checked) {
    this.data.format.commaSeparator = checked;
  }

  onCurrencyFlagChange(checked) {
    this.data.format.currencyFlag = checked;
  }

  onCurrencyCodeChange(code) {
    this.data.format.currencyCode = code;
  }
}
