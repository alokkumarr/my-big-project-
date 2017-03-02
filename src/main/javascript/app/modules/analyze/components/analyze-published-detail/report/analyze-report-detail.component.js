import template from './analyze-report-detail.component.html';

const REPORT_GRID_ID = 'analyze-detail-report-grid-container';

export const AnalyzeReportDetailComponent = {
  template,
  bindings: {
    analysis: '<'
  },
  controller: class AnalyzeReportDetailController {
    constructor(AnalyzeService, dxDataGridService) {
      this.REPORT_GRID_ID = REPORT_GRID_ID;
      this._AnalyzeService = AnalyzeService;
      this._dxDataGridService = dxDataGridService;
      const columns = [{
        caption: 'CustomerName',
        dataField: 'CustomerName',
        allowSorting: true,
        alignment: 'left',
        width: '40%'
      }, {
        caption: 'TotalPrice',
        dataField: 'TotalPrice',
        allowSorting: true,
        alignment: 'left',
        width: '30%'
      }, {
        caption: 'ShipperName',
        dataField: 'ShipperName',
        dataType: 'date',
        allowSorting: true,
        alignment: 'left',
        width: '30%'
      }, {
        caption: 'WarehouseName',
        dataField: 'WarehouseName',
        allowSorting: true,
        alignment: 'left',
        width: '30%'
      }];

      this.gridConfig = this._dxDataGridService.mergeWithDefaultConfig({
        columns,
        bindingOptions: {
          dataSource: '$ctrl.data'
        }
      });
    }

    $onInit() {
      this._AnalyzeService.getDataByQuery()
      .then(data => {
        this.data = data;
      });
    }
  }
};
