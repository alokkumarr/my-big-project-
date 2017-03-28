import assign from 'lodash/assign';

import template from './analyze-pivot-preview.component.html';
import style from './analyze-pivot-preview.component.scss';

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
    }

    $onInit() {
      this.pivotGridOptions = assign({
        onInitialized: e => {
          this._gridInstance = e.component;
        }
      }, this.model.defaultOptions);

      this._$timeout(() => {
        // have to repaint the grid because of the animation of the modal
        // if it's not repainted it appears smaller
        this._gridInstance.option('dataSource', this.model.dataSource);
        this._gridInstance.getDataSource().state(this.model.pivotState);
      }, 500);
    }

    cancel() {
      this._$mdDialog.cancel();
    }
  }
};
