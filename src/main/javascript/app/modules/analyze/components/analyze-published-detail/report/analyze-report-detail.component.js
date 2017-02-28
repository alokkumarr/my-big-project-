import first from 'lodash/first';

import template from './analyze-report-detail.component.html';

const REPORT_GRID_ID = 'analyze-detail-report-grid-container';

export const AnalyzeReportDetailComponent = {
  template,
  bindings: {
    analysis: '<'
  },
  controller: class AnalyzeReportDetailController {
    constructor(AnalyzeService, $componentHandler) {
      this.REPORT_GRID_ID = REPORT_GRID_ID;
      this._AnalyzeService = AnalyzeService;
      this._$componentHandler = $componentHandler;
      this.columns = [{
        name: 'CustomerName',
        checked: true
      }, {
        name: 'TotalPrice',
        checked: true
      }, {
        name: 'ShipperName',
        checked: true
      }, {
        name: 'WarehouseName',
        checked: true
      }];
    }

    $onInit() {
      this._AnalyzeService.getDataByQuery()
      .then(data => {
        this.data = data;
        const grid = first(this._$componentHandler.get(REPORT_GRID_ID));
        grid.updateColumns(this.columns);
        grid.updateSource(data);
      });
    }
  }
};
