import * as template from './report-format-dialog.component.html';
import style from './report-format-dialog.component.scss';
import * as currencyCodeList from 'currency-codes/data.js';
import * as getSymbolFromCurrency from 'currency-symbol-map/currency-symbol-map.js'

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
      this.StringNumber = '';
      this.format = {
        column: this.modelData.dataField,
        type: this.modelData.dataType
      };
      if(this.modelData.dataType === 'number') {
        this.format.CurrencySymbol = '';
        if (this.modelData.dataType === 'number' && this.modelData.format.type === 'fixedpoint') {
          this.format.CommaSeparator = true;
        } else {
          this.format.CommaSeparator = false;
        }
        this.format.NumberDecimal = this.modelData.format.precision;
        if (this.modelData.format.currency){
          this.format.CurrencyCode = this.modelData.format.currency;
          this.format.CurrencySymbol = this.getSymbolFromCurrency(this.format.CurrencyCode);
          this.format.CurrencyFlag = true;
        } else {
          this.format.CurrencyCode = 'USD';
          this.format.CurrencyFlag = true;
        }
        this.modifyNumber();
      }
      if(this.modelData.dataType === 'date' || this.modelData.dataType === 'timestamp') {
        this.format.dateFormat = this.modelData.format;
      }
    }

    modifyNumber() {
      if(this.format.NumberDecimal > -1 && this.format.CommaSeparator && this.format.CurrencyFlag) {
        this.finalNumber = this._$filter('number')(this.number, this.format.NumberDecimal);
        if(this.format.CurrencyCode){
          this.format.CurrencySymbol = this.getSymbolFromCurrency(this.format.CurrencyCode);
          this.StringNumber = this.finalNumber + ' ' + this.format.CurrencySymbol;
        } else {
          this.StringNumber = this.finalNumber.toString();
        }
      }
      if(this.format.NumberDecimal > -1 && this.format.CommaSeparator && !this.format.CurrencyFlag) {
        this.StringNumber = this._$filter('number')(this.number, this.format.NumberDecimal);
      }
      if(this.format.NumberDecimal > -1 && !this.format.CommaSeparator && this.format.CurrencyFlag) {
        this.StringNumber = this._$filter('number')(this.number, this.format.NumberDecimal);
        this.StringNumber = (num => num.split(',').join(''))(this.StringNumber);
        if(this.format.CurrencyCode){
          this.format.CurrencySymbol = this.getSymbolFromCurrency(this.format.CurrencyCode);
          this.StringNumber = this.StringNumber + ' ' + this.format.CurrencySymbol;
        } else {
          this.format.CurrencySymbol = '';
          this.StringNumber = this.StringNumber.toString();
        }
      }
      if(this.format.NumberDecimal > -1 && !this.format.CommaSeparator && !this.format.CurrencyFlag) {
        this.StringNumber = this._$filter('number')(this.number, this.format.NumberDecimal);
        this.StringNumber = (num => num.split(',').join(''))(this.StringNumber);
      }
      if(!(this.format.NumberDecimal > -1) && this.format.CommaSeparator && !this.format.CurrencyFlag) {
        this.StringNumber = this._$filter('number')(this.number);
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
