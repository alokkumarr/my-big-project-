import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as currencyCodes from 'currency-codes/data.js';
import * as getCurrencySymbol from 'currency-symbol-map/currency-symbol-map.js';
import * as has from 'lodash/has';
import * as trim from 'lodash/trim';
import * as isFinite from 'lodash/isFinite';

import { Format } from '../../../models';
import { FLOAT_TYPES } from '../../consts';
import {
  formatNumber,
  isFormatted
} from '../../../common/utils/numberFormatter';

const DEFAULT_CURRENCY = 'USD';
const FLOAT_SAMPLE = 1000.33333;
const INT_SAMPLE = 1000;
export const DEFAULT_PRECISION = 2;

@Component({
  selector: 'data-format-dialog',
  templateUrl: './data-format-dialog.component.html',
  styleUrls: ['./data-format-dialog.component.scss']
})
export class DataFormatDialogComponent implements OnInit {
  public format: Format = {};
  public currencyCodes = currencyCodes;
  public sample: string;
  public isFloat: boolean;

  constructor(
    public _dialogRef: MatDialogRef<DataFormatDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      format: Format;
      type: string;
    }
  ) {}

  ngOnInit() {
    this.format = this.data.format || {};
    this.isFloat = FLOAT_TYPES.includes(this.data.type);
    if (this.isFloat && !has(this.format, 'precision')) {
      this.format.precision = DEFAULT_PRECISION;
    }
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
    format.currencySymbol = this.getSymbol(format.currency);
    this.changeSample();
  }

  onCurrencyCodeChange(code) {
    this.format.currency = code;
    this.format.currencySymbol = this.getSymbol(code);
    this.changeSample();
  }

  getSymbol(code) {
    // if there is no symbol for the code, just return the code
    const symbol = getCurrencySymbol(code);
    if (trim(symbol)) {
      return symbol;
    }
    return code;
  }

  onPrecisionChange(precision) {
    this.format.precision = isFinite(precision) ? precision : 0;
    this.changeSample();
  }

  changeSample() {
    const sampleNr = this.isFloat ? FLOAT_SAMPLE : INT_SAMPLE;

    if (isFormatted(this.format)) {
      this.sample = formatNumber(sampleNr, this.format);
    } else {
      this.sample = null;
    }
  }
}
