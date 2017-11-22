import * as template from './report-format-dialog.component.html';
import style from './report-format-dialog.component.scss';
import * as currencyCodeList from 'currency-codes/data.js';
import * as getSymbolFromCurrency from 'currency-symbol-map/currency-symbol-map.js';
import * as isUndefined from 'lodash/isUndefined';

export const ReportFormatDialogComponent = {
  bindings: {
    modelData: '<'
  },
  template,
  style: [style],
  controller: class ReportFormatDialogController {
    constructor($mdDialog, $filter) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._$filter = $filter;
      this.currencyCodeList = currencyCodeList;
      this.getSymbolFromCurrency = getSymbolFromCurrency;
      this.number = 1000.33333;
      this.finalNumber = 0;
      this.stringNumber = '';
      this.format = {
        column: this.modelData.dataField,
        type: this.modelData.dataType
      };
      this.format.numberDecimal = 0;
      if (this.modelData.dataType === 'number') {
        this.format.currencySymbol = '';
        if (this.modelData.format.comma) {
          this.format.commaSeparator = true;
        } else {
          this.format.commaSeparator = false;
        }
        this.format.numberDecimal = this.modelData.format.precision;
        if (this.modelData.format.currency) {
          this.format.currencyCode = this.modelData.format.currency;
          this.format.currencySymbol = this.getSymbolFromCurrency(this.format.currencyCode);
          this.format.currencyFlag = true;
        } else {
          this.format.currencyCode = 'USD';
          this.format.currencyFlag = false;
        }
        this.modifyNumber();
      }
      if (this.modelData.dataType === 'date' || this.modelData.dataType === 'timestamp') {
        this.format.dateFormat = this.modelData.format;
      }
    }

    modifyNumber() {
      if (this.format.numberDecimal > -1 && this.format.commaSeparator && this.format.currencyFlag) {
        this.finalNumber = this._$filter('number')(this.number, this.format.numberDecimal);
        this.format.currencySymbol = this.getSymbolFromCurrency(this.format.currencyCode);
        if (!isUndefined(this.format.currencySymbol) && !isUndefined(this.format.currencyCode)) {
          this.stringNumber = this.format.currencySymbol + ' ' + this.finalNumber;
        } else {
          this.stringNumber = this.finalNumber.toString();
        }
      }

      if (this.format.numberDecimal > -1 && this.format.commaSeparator && !this.format.currencyFlag) {
        this.format.currencySymbol = undefined;
        this.stringNumber = this._$filter('number')(this.number, this.format.numberDecimal);
      }

      if (this.format.numberDecimal > -1 && !this.format.commaSeparator && this.format.currencyFlag) {
        this.stringNumber = this._$filter('number')(this.number, this.format.numberDecimal);
        this.stringNumber = (num => num.split(',').join(''))(this.stringNumber);
        this.format.currencySymbol = this.getSymbolFromCurrency(this.format.currencyCode);
        if (!isUndefined(this.format.currencySymbol) && !isUndefined(this.format.currencyCode)) {
          this.stringNumber = this.format.currencySymbol + ' ' + this.stringNumber;
        } else {
          this.format.currencySymbol = undefined;
          this.stringNumber = this.stringNumber.toString();
        }
      }

      if (this.format.numberDecimal > -1 && !this.format.commaSeparator && !this.format.currencyFlag) {
        this.format.currencySymbol = undefined;
        this.stringNumber = this._$filter('number')(this.number, this.format.numberDecimal);
        this.stringNumber = (num => num.split(',').join(''))(this.stringNumber);
      }

      if (!(this.format.numberDecimal > -1) && this.format.commaSeparator && !this.format.currencyFlag) {
        this.format.currencySymbol = undefined;
        this.stringNumber = this._$filter('number')(this.number);
      }
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    apply() {
      this._$mdDialog.hide(this.format);
    }
  }
};
