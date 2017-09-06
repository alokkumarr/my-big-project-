import * as template from './analyze-pivot-preview.component.html';
import style from './analyze-pivot-preview.component.scss';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

export const AnalyzePivotPreviewComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class AnalyzePivotPreviewController {
    constructor($mdDialog, $timeout) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;
      this.pivotGridUpdater = new BehaviorSubject({});
    }

    $onInit() {
      this.pivotGridUpdater.next({
        dataSource: this.model.dataSource
      });
    }

    cancel() {
      this._$mdDialog.cancel();
    }
  }
};
