import concat from 'lodash/concat';
import first from 'lodash/first';

import template from './analyze-report-preview.component.html';
import style from './analyze-report-preview.component.scss';

export const AnalyzeReportPreviewComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class AnalyzeReportPreviewController {
    constructor($componentHandler, $mdDialog, AnalyzeService) {
      this._$componentHandler = $componentHandler;
      this._$mdDialog = $mdDialog;
      this._AnalyzeService = AnalyzeService;

      this.settings = {
        minRowsToShow: 'auto'
      };
    }

    $onInit() {
      this.reloadPreviewGrid();
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    reloadPreviewGrid() {
      const grid = first(this._$componentHandler.get('arp-grid-container'));

      if (grid) {
        grid.reload(this.model.columns, this.model.gridData);
      }
    }

    loadMore() {
      this._AnalyzeService.getDataByQuery()
        .then(data => {
          this.model.gridData = concat(this.model.gridData, data);
          this.reloadPreviewGrid();
        });
    }
  }
};
