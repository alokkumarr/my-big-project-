import map from 'lodash/map';

import template from './report-grid-display.component.html';

const COLUMN_WIDTH = 175;

export const ReportGridDisplayComponent = {
  template,
  bindings: {
    data: '<',
    columns: '<'
  },
  controller: class ReportGridDisplayController {
    constructor(dxDataGridService) {
      'ngInject';
      this._dxDataGridService = dxDataGridService;
    }

    $onInit() {
      const columns = map(this.columns, column => {
        return {
          caption: column.label,
          dataField: column.columnName,
          dataType: column.type,
          width: COLUMN_WIDTH
        };
      });

      this.gridConfig = this._dxDataGridService.mergeWithDefaultConfig({
        columns,
        dataSource: this.data
      });
    }
  }
};
