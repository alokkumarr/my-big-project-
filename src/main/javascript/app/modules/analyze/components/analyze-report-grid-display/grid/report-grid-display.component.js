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
    constructor(dxDataGridService, FilterService) {
      'ngInject';
      this._dxDataGridService = dxDataGridService;
      this._FilterService = FilterService;
      this._gridInstance = null;
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
        dataSource: this.data,
        onInitialized: this.onGridInitialized.bind(this)
      });
    }

    $onChanges() {
      if (this._gridInstance) {
        this._gridInstance.option('dataSource', this.data);
        this._gridInstance.refresh();
      }
    }

    onGridInitialized(e) {
      this._gridInstance = e.component;
    }
  }
};
