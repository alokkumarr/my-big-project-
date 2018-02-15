declare const require: any;
import {
  Component,
  Input,
  Inject
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as currencyCodes from 'currency-codes/data.js';
import * as getCurrencySymbol from 'currency-symbol-map/currency-symbol-map.js';
import * as isNumber from 'lodash/isNumber';

import { Format } from '../../../models'
import { formatNumber } from '../../../common/utils/numberFormatter';

const template = require('./data-format-dialog.component.html');
require('./data-format-dialog.component.scss');

const DEFAULT_CURRENCY = 'USD';
const SAMPLE_NUMBER = 1000.33333;

@Component({
  selector: 'data-format-dialog',
  template
})
export class DataFormatDialogComponent {

  public format: Format = {};
  public currencyCodes = currencyCodes;
  public sample: string
  public isNumber = isNumber;

  constructor(
    private _dialogRef: MatDialogRef<DataFormatDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {format: Format}
  ) {}

  ngOnInit() {
    this.format = this.data.format || {};
    this.changeSample();
  }

  close() {
    this._dialogRef.close();
  }

  applyFormat() {
    this._dialogRef.close(this.format);
  }

  onCommaSeparatorChange(checked) {
    this.format.comma = checked;
    this.changeSample();
  }

  onCurrencyFlagChange(checked) {
    const format = this.format;
    format.currency = checked ? DEFAULT_CURRENCY : null;
    format.currencySymbol= getCurrencySymbol(format.currency);
    this.changeSample();
  }

  onCurrencyCodeChange(code) {
    this.format.currency = code;
    this.format.currencySymbol= getCurrencySymbol(code);
    this.changeSample();
  }

  onPrecisionChange(precision) {
    this.format.precision = precision;
    this.changeSample();
  }

  changeSample() {
    this.sample = formatNumber(SAMPLE_NUMBER, this.format);
  }
}
