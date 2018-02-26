import * as template from './report-format-dialog.component.html';
import style from './report-format-dialog.component.scss';
import * as get from 'lodash/get';

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
      this.format = {
        column: this.modelData.dataField,
        type: this.modelData.dataType
      };
      this.format.dateFormat = get(this.modelData, 'format');
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    apply() {
      this._$mdDialog.hide(this.format);
    }
  }
};
