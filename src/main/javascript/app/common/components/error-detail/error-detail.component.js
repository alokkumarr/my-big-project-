import * as template from './error-detail.component.html';
import style from './error-detail.component.scss';

export const ErrorDetailComponent = {
  template,
  styles: [style],
  bindings: {
    errorObj: '<'
  },
  controller: class ErrorDetailController {
    constructor($mdDialog, ErrorDetail) {
      'ngInject';

      this._$mdDialog = $mdDialog;
      this._ErrorDetail = ErrorDetail;
    }

    $onInit() {
      this.errorMessage = this._ErrorDetail.getTitle(this.errorObj);
      this.errorBody = this._ErrorDetail.getDetail(this.errorObj);
    };

    cancel() {
      this._$mdDialog.hide(true);
    }
  }
};
